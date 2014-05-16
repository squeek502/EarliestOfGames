package squeek.earliestofgames.helpers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import squeek.earliestofgames.ModEarliestOfGames;
import squeek.earliestofgames.content.CrateContainer;
import squeek.earliestofgames.content.CrateTile;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHelper implements IGuiHandler
{
	public enum GuiIds
	{
		CRATE
	}
	
	public static boolean openGuiOfTile(EntityPlayer player, TileEntity tile)
	{
		if (!tile.getWorldObj().isRemote)
		{
			if (tile instanceof CrateTile)
			{
				player.openGui(ModEarliestOfGames.instance, GuiIds.CRATE.ordinal(), tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);
				return true;
			}
			return false;
		}
		return true;
	}

	@Override
	public Object getServerGuiElement(int guiId, EntityPlayer player, World world, int x, int y, int z)
	{
		return getSidedGuiElement(false, guiId, player, world, x, y, z);
	}

	@Override
	public Object getClientGuiElement(int guiId, EntityPlayer player, World world, int x, int y, int z)
	{
		return getSidedGuiElement(true, guiId, player, world, x, y, z);
	}
	
	public Object getSidedGuiElement(boolean isClientSide, int guiId, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null)
		{
			switch (GuiIds.values()[guiId])
			{
				case CRATE:
					if (tile instanceof CrateTile)
					{
						return isClientSide ? new CrateGui(player.inventory, (CrateTile) tile) : new CrateContainer(player.inventory, (CrateTile) tile);
					}
					break;
			}
		}
		return null;
	}
}
