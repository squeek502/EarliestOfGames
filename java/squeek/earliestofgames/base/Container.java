package squeek.earliestofgames.base;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

public abstract class Container extends net.minecraft.inventory.Container
{
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
