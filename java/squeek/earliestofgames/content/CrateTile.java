package squeek.earliestofgames.content;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import squeek.earliestofgames.ModEarliestOfGames;
import squeek.earliestofgames.base.TileEntityFluidJunction;
import squeek.earliestofgames.filters.IFilter;
import squeek.earliestofgames.filters.SizeFilter;
import squeek.earliestofgames.helpers.WorldHelper;

public class CrateTile extends TileEntityFluidJunction implements IInventory
{
	protected ItemStack[] inventoryItems;
	protected int captureCooldown = 0;
	protected int captureTickInterval = 8;

	protected IFilter[] filters = new IFilter[ForgeDirection.VALID_DIRECTIONS.length];

	protected int ticksUntilNextBubble = 0;
	protected int ticksPerBubble = 2;

	/*
	 * Constructors
	 */
	public CrateTile()
	{
		inventoryItems = new ItemStack[14];

		setFilterOfSide(ForgeDirection.UP, new SizeFilter());

		SizeFilter down = new SizeFilter();
		down.maxItemSize = 0.5f;
		setFilterOfSide(ForgeDirection.DOWN, down);

		SizeFilter north = new SizeFilter();
		north.maxItemSize = 4f;
		setFilterOfSide(ForgeDirection.NORTH, north);

		SizeFilter south = new SizeFilter();
		north.maxItemSize = 4f;
		setFilterOfSide(ForgeDirection.SOUTH, south);
	}

	/*
	 * Update
	 */
	@Override
	public void updateEntity()
	{
		super.updateEntity();

		captureCooldown--;

		if (shouldCaptureItems() && couldCaptureItems())
		{
			captureItemsInside();
			captureCooldown = captureTickInterval;
		}

		if (getInputFlow(ForgeDirection.UP) != null && getInputFlow(ForgeDirection.UP).getFluid() == FluidRegistry.WATER)
		{
			for (int i = 0; i < 8; i++)
			{
				ForgeDirection side = ForgeDirection.getOrientation(i % 4 + 2);
				double x = (double) (xCoord + 0.5f + side.offsetX / 1.75f);
				double y = (double) (yCoord + 1.3f);
				double z = (double) (zCoord + 0.5f + side.offsetZ / 1.75f);
				double velX = (double) (worldObj.rand.nextFloat() * side.offsetX * 0.1f);
				double velZ = (double) (worldObj.rand.nextFloat() * side.offsetZ * 0.1f);

				if (side.offsetX != 0)
				{
					z += worldObj.rand.nextFloat() - 0.5f;
					velZ += (worldObj.rand.nextFloat() - 0.5f) * 0.2f;
					if (side.offsetX < 0)
						x -= 0.05f;
				}
				else if (side.offsetZ != 0)
				{
					x += worldObj.rand.nextFloat() - 0.5f;
					velX += (worldObj.rand.nextFloat() - 0.5f) * 0.2f;
					if (side.offsetZ < 0)
						z -= 0.05f;
				}

				worldObj.spawnParticle("splash", x, y, z, velX, 0, velZ);
			}

			if (--ticksUntilNextBubble <= 0)
			{
				this.worldObj.spawnParticle("bubble", xCoord + 0.5f + (worldObj.rand.nextFloat() - 0.5f) / 2f, yCoord + 1, zCoord + 0.5f + (worldObj.rand.nextFloat() - 0.5f) / 2f, (worldObj.rand.nextFloat() - 0.25f) * 0.1f,
											worldObj.rand.nextFloat(), (worldObj.rand.nextFloat()) * 0.25f);
				ticksUntilNextBubble = 8;
			}
		}
	}

	/*
	 * Liquid flow
	 */
	@Override
	public boolean canFluidFlowIntoSide(Fluid fluid, ForgeDirection side)
	{
		return canFluidPassThroughSide(fluid, side) && super.canFluidFlowIntoSide(fluid, side);
	}

	@Override
	public boolean canFluidFlowOutOfSide(Fluid fluid, ForgeDirection side)
	{
		return canFluidPassThroughSide(fluid, side) && super.canFluidFlowOutOfSide(fluid, side);
	}

	/*
	 * Filters
	 */
	public IFilter getFilter(ForgeDirection side)
	{
		return side == ForgeDirection.UNKNOWN ? null : filters[side.ordinal()];
	}

	public void setFilterOfSide(ForgeDirection side, IFilter filter)
	{
		if (side == ForgeDirection.UNKNOWN)
			return;

		if (getFilter(side) != filter)
		{
			filters[side.ordinal()] = filter;
			onFilterChanged(side);
		}
	}

	protected void onFilterChanged(ForgeDirection side)
	{
		ModEarliestOfGames.Log.info("onFilterChanged");
		
		if (side != ForgeDirection.UP)
			releaseEscapableItems();
		
		if (getInputFlow(side) != null && !canFluidFlowIntoSide(getInputFlow(side).getFluid(), side))
			setInputFlow(side, null);
		
		if (getOutputFlow(side) != null && !canFluidFlowOutOfSide(getOutputFlow(side).getFluid(), side))
			stopOutputFlowOnSide(side);
		
		updateOutputFlows(true);

		if (worldObj != null)
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord));
	}

	public boolean canItemPassThroughSide(ItemStack item, ForgeDirection side)
	{
		return getFilter(side) != null && getFilter(side).passesFilter(item);
	}

	public boolean canFluidPassThroughSide(Fluid fluid, ForgeDirection side)
	{
		return getFilter(side) != null && getFilter(side).passesFilter(fluid);
	}

	public boolean canEntityPassThroughSide(Entity entity, ForgeDirection side)
	{
		return getFilter(side) != null && getFilter(side).passesFilter(entity);
	}

	/*
	 * Capturing
	 */
	public boolean captureItemsInside()
	{
		boolean didCapture = false;
		List<EntityItem> itemEntities = getItemEntitiesInside();

		for (EntityItem itemEntity : itemEntities)
		{
			if (canItemEscape(itemEntity.getEntityItem()))
				continue;

			// func_145898_a = insertStackFromEntity
			didCapture = didCapture || TileEntityHopper.func_145898_a(this, itemEntity);
		}

		return didCapture;
	}

	public boolean couldCaptureItems()
	{
		return !isInventoryFull();
	}

	public boolean shouldCaptureItems()
	{
		return captureCooldown <= 0;
	}

	@SuppressWarnings("unchecked")
	public List<EntityItem> getItemEntitiesInside()
	{
		return worldObj.selectEntitiesWithinAABB(EntityItem.class, ((Crate) getBlockType()).getInnerBoundingBox(worldObj, xCoord, yCoord, zCoord), IEntitySelector.selectAnything);
	}

	@SuppressWarnings("unchecked")
	public List<EntityItem> getItemEntitiesInsideOuterBounds()
	{
		return worldObj.selectEntitiesWithinAABB(EntityItem.class, ((Crate) getBlockType()).getOuterBoundingBox(worldObj, xCoord, yCoord, zCoord), IEntitySelector.selectAnything);
	}

	/*
	 * Releasing
	 */
	public boolean releaseEscapableItems()
	{
		boolean didItemEscape = false;
		List<Integer> escapableSlots = getInventorySlotsWithEscapableItems();

		for (int slotNum : escapableSlots)
		{
			didItemEscape = didItemEscape || releaseItemFromSlot(slotNum);
		}

		return didItemEscape;
	}

	public boolean canItemEscape(ItemStack itemStack)
	{
		for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
		{
			if (side == ForgeDirection.UP)
				continue;

			boolean canMoveTowardsSide = side == ForgeDirection.DOWN;
			if (!canMoveTowardsSide)
				continue;

			if (WorldHelper.getBlockOnSide(this, side).isBlockSolid(worldObj, xCoord + side.offsetX, yCoord + side.offsetY, zCoord + side.offsetZ, side.ordinal()))
				continue;

			if (canItemPassThroughSide(itemStack, side))
				return true;
		}
		return false;
	}

	public List<Integer> getInventorySlotsWithEscapableItems()
	{
		List<Integer> escapableSlots = new ArrayList<Integer>();
		for (int slotNum = 0; slotNum < inventoryItems.length; slotNum++)
		{
			ItemStack itemStack = getStackInSlot(slotNum);
			if (itemStack != null && canItemEscape(itemStack))
				escapableSlots.add(slotNum);
		}
		return escapableSlots;
	}

	public boolean releaseItemFromSlot(int slotNum)
	{
		ItemStack itemStack = getStackInSlot(slotNum);
		if (itemStack != null)
		{
			EntityItem entityItem = new EntityItem(worldObj, xCoord + 0.5f, yCoord + 0.5f, zCoord + 0.5f, getStackInSlot(slotNum));
			worldObj.spawnEntityInWorld(entityItem);
			setInventorySlotContents(slotNum, null);
			return true;
		}
		return false;
	}

	/*
	 * Inventory utility
	 */
	protected void onSlotFilled(int slotNum)
	{
		if (canItemEscape(getStackInSlot(slotNum)))
			releaseItemFromSlot(slotNum);
	}

	protected void onSlotEmptied(int slotNum)
	{

	}

	public boolean isInventoryEmpty()
	{
		for (ItemStack itemStack : inventoryItems)
		{
			if (itemStack != null)
				return false;
		}
		return true;
	}

	public boolean isInventoryFull()
	{
		for (ItemStack itemStack : inventoryItems)
		{
			if (itemStack == null || itemStack.stackSize != itemStack.getMaxStackSize())
				return false;
		}
		return true;
	}

	/*
	 * IInventory implementation
	 */
	@Override
	public int getSizeInventory()
	{
		return inventoryItems.length;
	}

	@Override
	public ItemStack getStackInSlot(int slotNum)
	{
		return inventoryItems[slotNum];
	}

	@Override
	public ItemStack decrStackSize(int slotNum, int count)
	{
		ItemStack itemStack = getStackInSlot(slotNum);

		if (itemStack != null)
		{
			if (itemStack.stackSize <= count)
				setInventorySlotContents(slotNum, null);
			else
			{
				itemStack = itemStack.splitStack(count);
				markDirty();
			}
		}

		return itemStack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slotNum)
	{
		ItemStack item = getStackInSlot(slotNum);
		setInventorySlotContents(slotNum, null);
		return item;
	}

	@Override
	public void setInventorySlotContents(int slotNum, ItemStack itemStack)
	{
		if (slotNum >= getSizeInventory() || slotNum < 0)
			return;

		boolean wasEmpty = inventoryItems[slotNum] == null;
		inventoryItems[slotNum] = itemStack;

		if (itemStack != null && itemStack.stackSize > getInventoryStackLimit())
			itemStack.stackSize = getInventoryStackLimit();

		if (wasEmpty && itemStack != null)
			onSlotFilled(slotNum);
		else if (!wasEmpty && itemStack == null)
			onSlotEmptied(slotNum);

		markDirty();
	}

	@Override
	public String getInventoryName()
	{
		return getBlockType().getLocalizedName();
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		return false;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : player.getDistanceSq((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D, (double) this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory()
	{

	}

	@Override
	public void closeInventory()
	{

	}

	@Override
	public boolean isItemValidForSlot(int slotNum, ItemStack itemStack)
	{
		return true;
	}

	/*
	 * Save data
	 */
	@Override
	public void writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);

		NBTTagList items = new NBTTagList();

		for (int slotNum = 0; slotNum < getSizeInventory(); slotNum++)
		{
			ItemStack stack = getStackInSlot(slotNum);

			if (stack != null)
			{
				NBTTagCompound item = new NBTTagCompound();
				item.setByte("Slot", (byte) slotNum);
				stack.writeToNBT(item);
				items.appendTag(item);
			}
		}

		compound.setTag("Items", items);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);

		NBTTagList items = compound.getTagList("Items", Constants.NBT.TAG_COMPOUND);

		for (int slotNum = 0; slotNum < items.tagCount(); slotNum++)
		{
			NBTTagCompound item = items.getCompoundTagAt(slotNum);
			int slot = item.getByte("Slot");

			if (slot >= 0 && slot < getSizeInventory())
			{
				setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(item));
			}
		}
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
	{
		super.onDataPacket(net, pkt);
	}

	@Override
	public Packet getDescriptionPacket()
	{
		return super.getDescriptionPacket();
	}
}
