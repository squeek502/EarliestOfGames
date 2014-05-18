package squeek.earliestofgames.asm;

import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockLiquid;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
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
			((CrateTile) tile).handleFlowIntoBlock(flowingBlock, newFlowDecay, ForgeDirection.getOrientation(flowDirection).getOpposite());
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
	
	public static int getFlowDecay(BlockLiquid liquidBlock, World world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null && tile instanceof CrateTile)
		{
			int flowDecay = ((CrateTile) tile).getFlowDecay(liquidBlock);
			ModEarliestOfGames.Log.info("getFlowDecay: " + flowDecay + " (from " + liquidBlock.getMaterial().toString() + ")");
			return flowDecay;
		}
		return -1;
	}
	
	public static int getEffectiveFlowDecay(BlockLiquid liquidBlock, IBlockAccess world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null && tile instanceof CrateTile)
		{
			int flowDecay = ((CrateTile) tile).getEffectiveFlowDecay(liquidBlock);
			ModEarliestOfGames.Log.info("getEffectiveFlowDecay: " + flowDecay + " (from " + liquidBlock.getMaterial().toString() + ")");
			return flowDecay;
		}
		return -1;
	}
}
