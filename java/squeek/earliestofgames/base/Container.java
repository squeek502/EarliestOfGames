package squeek.earliestofgames.base;

import java.lang.reflect.InvocationTargetException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class Container extends net.minecraft.inventory.Container
{
	protected IInventory inventory;

	public Container(IInventory inventory)
	{
		this.inventory = inventory;
	}

	protected void addSlots(IInventory inventory, int xStart, int yStart, int rows)
	{
		addSlotsOfType(Slot.class, inventory, xStart, yStart, rows);
	}

	protected void addSlotsOfType(Class<? extends Slot> slotClass, IInventory inventory, int xStart, int yStart, int rows)
	{
		int numSlotsPerRow = inventory.getSizeInventory() / rows;
		for (int i = 0, col = 0, row = 0; i < inventory.getSizeInventory(); ++i, ++col)
		{
			if (col >= numSlotsPerRow)
			{
				row++;
				col = 0;
			}
			
			try
			{
				this.addSlotToContainer(slotClass.getConstructor(IInventory.class, int.class, int.class, int.class).newInstance(inventory, i, xStart + col * 18, yStart + row * 18));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	protected void addPlayerInventorySlots(InventoryPlayer playerInventory, int yStart)
	{
		addPlayerInventorySlots(playerInventory, 8, yStart);
	}

	protected void addPlayerInventorySlots(InventoryPlayer playerInventory, int xStart, int yStart)
	{
		// inventory
		for (int row = 0; row < 3; ++row)
		{
			for (int col = 0; col < 9; ++col)
			{
				this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, xStart + col * 18, yStart + row * 18));
			}
		}

		// hotbar
		for (int col = 0; col < 9; ++col)
		{
			this.addSlotToContainer(new Slot(playerInventory, col, xStart + col * 18, yStart + 58));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotNum)
	{
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(slotNum);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (slotNum < this.inventory.getSizeInventory())
			{
				if (!this.mergeItemStack(itemstack1, this.inventory.getSizeInventory(), this.inventorySlots.size(), true))
				{
					return null;
				}
			}
			else if (!inventory.isItemValidForSlot(slotNum, itemstack1) || !this.mergeItemStack(itemstack1, 0, this.inventory.getSizeInventory(), false))
			{
				return null;
			}

			if (itemstack1.stackSize == 0)
			{
				slot.putStack((ItemStack) null);
			}
			else
			{
				slot.onSlotChanged();
			}
		}

		return itemstack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return inventory.isUseableByPlayer(player);
	}
}
