package squeek.earliestofgames.helpers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import squeek.earliestofgames.ModEarliestOfGames;
import squeek.earliestofgames.blocks.CrateTile;

public class GuiHelper
{
	public static boolean openGui(EntityPlayer player, TileEntity tile)
	{
		if (!tile.getWorldObj().isRemote)
		{
			if (tile instanceof CrateTile)
			{
				player.openGui(ModEarliestOfGames.instance, 0, tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);
				return true;
			}
			return false;
		}
		return true;
	}
}
