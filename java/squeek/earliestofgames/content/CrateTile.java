package squeek.earliestofgames.content;

import java.util.List;
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

public class CrateTile extends TileEntity implements IInventory
{
	protected ItemStack[] inventoryItems;
	protected int captureCooldown = 0;
	protected int captureCheckInterval = 8;

	public CrateTile()
	{
		inventoryItems = new ItemStack[14];
	}
	
	public boolean isCoolingDown()
	{
		return captureCooldown > 0;
	}
	
	public boolean isInventoryFull()
	{
		for (ItemStack itemStack : inventoryItems)
		{
			if (itemStack == null)
				return false;
		}
		return true;
	}
	
	public boolean couldCaptureItems()
	{
		return !isInventoryFull();
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		
		captureCooldown--;

		if (!isCoolingDown() && couldCaptureItems())
			captureItemEntitiesInside();
	}

	public void captureItemEntitiesInside()
	{
		List<EntityItem> itemEntities = getItemEntitiesInside();
		
		for (EntityItem itemEntity : itemEntities)
		{
			// insertStackFromEntity
			TileEntityHopper.func_145898_a(this, itemEntity);
		}
	}

	public List<EntityItem> getItemEntitiesInside()
	{
		return worldObj.selectEntitiesWithinAABB(EntityItem.class, ((Crate) getBlockType()).getInnerBoundingBox(worldObj, xCoord, yCoord, zCoord), IEntitySelector.selectAnything);
	}

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
		inventoryItems[slotNum] = itemStack;

		if (itemStack != null && itemStack.stackSize > getInventoryStackLimit())
			itemStack.stackSize = getInventoryStackLimit();

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
