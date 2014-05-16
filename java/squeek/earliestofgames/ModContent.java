package squeek.earliestofgames;

import squeek.earliestofgames.blocks.BlockCrate;

public class ModContent
{
	public static BlockCrate blockCrate;

	public static void registerBlocks()
	{
		blockCrate = new BlockCrate();
	}
}
