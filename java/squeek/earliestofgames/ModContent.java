package squeek.earliestofgames;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.oredict.ShapedOreRecipe;
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
		CrateRenderer crateRenderer = new CrateRenderer();
		ClientRegistry.bindTileEntitySpecialRenderer(CrateTile.class, crateRenderer);
		MinecraftForgeClient.registerItemRenderer(ItemBlock.getItemFromBlock(blockCrate), crateRenderer);
	}
	
	public static void registerRecipes()
	{
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockCrate, 1, 0), "___", "_ _", "___", '_', "slabWood"));
	}
}
