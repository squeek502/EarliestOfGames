package squeek.earliestofgames.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class CrateTile extends TileEntity implements IInventory
{
	private ItemStack[] inventoryItems;

	public CrateTile()
	{
		inventoryItems = new ItemStack[8];
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
		return null;
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
	}

	@Override
	public String getInventoryName()
	{
		return null;
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
		return true;
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
}
