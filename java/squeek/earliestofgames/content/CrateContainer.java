package squeek.earliestofgames.content;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import squeek.earliestofgames.base.Container;

public class CrateContainer extends Container
{
	CrateTile crate = null;
	
	public CrateContainer(InventoryPlayer playerInventory, CrateTile crate)
	{
		this.crate = crate;
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1)
	{
		return false;
	}
	
}
