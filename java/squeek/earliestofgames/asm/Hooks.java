package squeek.earliestofgames.asm;

import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import squeek.earliestofgames.content.CrateTile;

public class Hooks
{
	// return true to cancel the default behavior
	public static boolean handleFlowIntoBlock(BlockDynamicLiquid flowingBlock, World world, int x, int y, int z, int newFlowDecay)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null && tile instanceof CrateTile)
		{
			return ((CrateTile) tile).handleFlowIntoBlock(flowingBlock, newFlowDecay);
		}
		return false;
	}
	
	public static boolean doesFlowGetBlockedBy(BlockDynamicLiquid flowingBlock, World world, int x, int y, int z, int fromSide)
	{
		return true;
	}
}
