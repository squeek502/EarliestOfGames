package squeek.earliestofgames.asm;

import java.lang.reflect.InvocationTargetException;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import squeek.earliestofgames.ModEarliestOfGames;
import squeek.earliestofgames.content.CrateTile;

public class Hooks
{
	// return true to cancel the default behavior
	public static boolean onFlowIntoBlockFrom(BlockDynamicLiquid flowingBlock, World world, int x, int y, int z, int newFlowDecay, int flowDirection)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null && tile instanceof CrateTile)
		{
			if (((CrateTile) tile).handleFlowIntoBlock(flowingBlock, newFlowDecay, ForgeDirection.getOrientation(flowDirection).getOpposite()))
				return true;
		}
		try
		{
			flowingBlock.getClass().getDeclaredMethod("func_149813_h", World.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE).invoke(flowingBlock, world, x, y, z, newFlowDecay);
			return false;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return true;
		}
	}
	
	public static boolean doesFlowGetBlockedBy(BlockDynamicLiquid flowingBlock, World world, int x, int y, int z, int flowDirection)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null && tile instanceof CrateTile)
		{
			ModEarliestOfGames.Log.info("doesFlowGetBlockedBy: "+ForgeDirection.getOrientation(flowDirection).toString());
			return !((CrateTile) tile).canItemPassThroughSide(new ItemStack(flowingBlock, 1, 0), ForgeDirection.getOrientation(flowDirection).getOpposite());
		}
		try
		{
			return (Boolean) flowingBlock.getClass().getDeclaredMethod("func_149807_p", World.class, Integer.TYPE, Integer.TYPE, Integer.TYPE).invoke(flowingBlock, world, x, y, z);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return true;
		}
	}
}
