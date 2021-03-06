package squeek.earliestofgames;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = ModInfo.MODID, version = ModInfo.VERSION)
public class ModEarliestOfGames
{
	public static final Logger Log = LogManager.getLogger(ModInfo.MODID);

    @Instance(ModInfo.MODID)
    public static ModEarliestOfGames instance;
    
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		ModContent.registerBlocks();
		ModContent.registerTileEntities();
		ModContent.registerRecipes();
		ModContent.registerHandlers();
		
		if (event.getSide() == Side.CLIENT)
		{
			ModContent.registerRenderers();
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{

	}
}
