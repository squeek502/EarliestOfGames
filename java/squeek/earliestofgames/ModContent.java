package squeek.earliestofgames;

import squeek.earliestofgames.content.Crate;
import squeek.earliestofgames.content.CrateTile;
import squeek.earliestofgames.helpers.GuiHelper;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModContent
{
	public static Crate blockCrate;

	public static void registerBlocks()
	{
		blockCrate = new Crate();
		GameRegistry.registerBlock(blockCrate, blockCrate.blockName);
	}
	
	public static void registerTileEntities()
	{
        GameRegistry.registerTileEntity(CrateTile.class, blockCrate.blockName);
	}
	
	public static void registerHandlers()
	{
        NetworkRegistry.INSTANCE.registerGuiHandler(ModEarliestOfGames.instance, new GuiHelper());
	}
}
