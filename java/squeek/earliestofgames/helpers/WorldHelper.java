package squeek.earliestofgames.helpers;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class WorldHelper
{
	public static Block getBlockOnSide(TileEntity tile, ForgeDirection side)
	{
		return tile.getWorldObj().getBlock(tile.xCoord + side.offsetX, tile.yCoord + side.offsetY, tile.zCoord + side.offsetZ);
	}
}
