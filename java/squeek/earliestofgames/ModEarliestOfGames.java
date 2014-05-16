package squeek.earliestofgames;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ModInfo.MODID, version = ModInfo.VERSION)
public class ModEarliestOfGames
{
	public static final Logger Log = LogManager.getLogger(ModInfo.MODID);

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		ModContent.registerBlocks();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{

	}
}
