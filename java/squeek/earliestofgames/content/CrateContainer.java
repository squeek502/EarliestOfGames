package squeek.earliestofgames.content;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import squeek.earliestofgames.base.Container;

public class CrateContainer extends Container
{
	CrateTile crate = null;

	public CrateContainer(InventoryPlayer playerInventory, CrateTile crate)
	{
		super(crate);
		this.crate = crate;

		addSlots(crate, 26, 20, 2);
		addPlayerInventorySlots(playerInventory, 69);
	}

}
