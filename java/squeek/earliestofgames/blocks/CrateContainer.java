package squeek.earliestofgames.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;

public class CrateContainer extends Container
{
	CrateTile crate = null;
	
	public CrateContainer(IInventory playerInventory, CrateTile crate)
	{
		this.crate = crate;
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1)
	{
		return false;
	}
	
}
