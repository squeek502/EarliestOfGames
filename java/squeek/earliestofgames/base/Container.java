package squeek.earliestofgames.base;

import java.lang.reflect.InvocationTargetException;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

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
			try
			{
				this.addSlotToContainer(slotClass.getConstructor(IInventory.class, Integer.class, Integer.class, Integer.class).newInstance(inventory, i, xStart + col * 18, yStart + row * 18));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			if (col >= numSlotsPerRow)
			{
				row++;
				col = 0;
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
}
