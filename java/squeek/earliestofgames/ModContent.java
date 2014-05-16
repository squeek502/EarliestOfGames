package squeek.earliestofgames;

import cpw.mods.fml.common.registry.GameRegistry;
import squeek.earliestofgames.blocks.BlockCrate;

public class ModContent
{
	public static BlockCrate blockCrate;

	public static void registerBlocks()
	{
		blockCrate = (BlockCrate) new BlockCrate();
		GameRegistry.registerBlock(blockCrate, blockCrate.getUnlocalizedName());
	}
}
