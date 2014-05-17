package squeek.earliestofgames.asm;

import net.minecraft.world.World;

public class Hooks
{
	public static boolean handleFlowIntoBlock(World world, int x, int y, int z, int newFlowDecay)
	{
		return true;
	}
}
