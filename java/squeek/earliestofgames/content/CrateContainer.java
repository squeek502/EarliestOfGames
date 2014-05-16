package squeek.earliestofgames.content;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import squeek.earliestofgames.base.Container;

public class CrateContainer extends Container
{
	CrateTile crate = null;

	public CrateContainer(InventoryPlayer playerInventory, CrateTile crate)
	{
		this.crate = crate;

		for (int i = 0; i < crate.getSizeInventory(); ++i)
		{
			this.addSlotToContainer(new Slot(crate, i, 44 + i * 18, 20));
		}

		addPlayerInventorySlots(playerInventory, 69);
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1)
	{
		return false;
	}

}
