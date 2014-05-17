package squeek.earliestofgames.content;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import squeek.earliestofgames.ModEarliestOfGames;
import squeek.earliestofgames.filters.IFilter;
import squeek.earliestofgames.filters.SizeFilter;

public class CrateTile extends TileEntity implements IInventory
{
	protected ItemStack[] inventoryItems;
	protected int captureCooldown = 0;
	protected int captureTickInterval = 8;

	protected IFilter[] filters = new IFilter[ForgeDirection.VALID_DIRECTIONS.length];

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
	}

	/*
	 * Liquid flow
	 */
	public void handleFlowIntoBlock(BlockDynamicLiquid flowingBlock, int newFlowDecay)
	{
		ModEarliestOfGames.Log.debug("onFlowIntoBlock: "+newFlowDecay);
		
		if (flowingBlock.getMaterial() == Material.water)
		{
			
		}
	}
	
	/*
	 * Filters
	 */
	public void setFilterOfSide(ForgeDirection side, IFilter filter)
	{
		if (side == ForgeDirection.UNKNOWN)
			return;

		if (filters[side.ordinal()] != filter)
		{
			filters[side.ordinal()] = filter;
			onFilterChanged(side);
		}
	}

	protected void onFilterChanged(ForgeDirection side)
	{
		if (side != ForgeDirection.UP)
			releaseEscapableItems();
	}

	public boolean canItemPassThroughSide(ItemStack item, ForgeDirection side)
	{
		return side != ForgeDirection.UNKNOWN && filters[side.ordinal()] != null && filters[side.ordinal()].passesFilter(item);
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

	public List<EntityItem> getItemEntitiesInside()
	{
		return worldObj.selectEntitiesWithinAABB(EntityItem.class, ((Crate) getBlockType()).getInnerBoundingBox(worldObj, xCoord, yCoord, zCoord), IEntitySelector.selectAnything);
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
}
