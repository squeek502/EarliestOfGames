package squeek.earliestofgames;

import cpw.mods.fml.common.registry.GameRegistry;
import squeek.earliestofgames.content.Crate;

public class ModContent
{
	public static Crate blockCrate;

	public static void registerBlocks()
	{
		blockCrate = new Crate();
		GameRegistry.registerBlock(blockCrate, blockCrate.blockName);
	}
}
