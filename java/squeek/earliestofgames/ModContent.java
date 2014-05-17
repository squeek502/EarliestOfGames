package squeek.earliestofgames;

import squeek.earliestofgames.content.Crate;
import squeek.earliestofgames.content.CrateRenderer;
import squeek.earliestofgames.content.CrateTile;
import squeek.earliestofgames.helpers.GuiHelper;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ModContent
{
	public static Crate blockCrate;

	public static void registerBlocks()
	{
		blockCrate = new Crate();
		GameRegistry.registerBlock(blockCrate, blockCrate.getBlockName());
	}
	
	public static void registerTileEntities()
	{
        GameRegistry.registerTileEntity(CrateTile.class, blockCrate.getBlockName());
	}
	
	public static void registerHandlers()
	{
        NetworkRegistry.INSTANCE.registerGuiHandler(ModEarliestOfGames.instance, new GuiHelper());
	}
	
	@SideOnly(Side.CLIENT)
	public static void registerRenderers()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(CrateTile.class, new CrateRenderer());
	}
}
