package squeek.earliestofgames.asm;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.world.World;
import squeek.earliestofgames.ModEarliestOfGames;
import squeek.earliestofgames.content.Crate;

public class Hooks
{
	// return true to cancel the default behavior
	public static boolean handleFlowIntoBlock(BlockDynamicLiquid flowingBlock, World world, int x, int y, int z, int newFlowDecay)
	{
		Block block = world.getBlock(x, y, z);
		if (block != null && block instanceof Crate)
		{
			ModEarliestOfGames.Log.info("handleFlowIntoBlock: "+newFlowDecay);
			return true;
		}
		return false;
	}
}
