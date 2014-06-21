package squeek.earliestofgames.asm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockLiquid;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;

// TODO: Deal with obfuscation of the methods
public class Wrappers
{
	private static Method doesFlowGetBlockedBy = null;
	private static Method flowIntoBlock = null;
	private static Method getFlowDecay = null;
	private static Method getEffectiveFlowDecay = null;
	private static Method canLiquidDisplaceBlock = null;
	private static Method getSmallestFlowDecay = null;

	private static Field quantaPerBlock = null;
	static
	{
		try
		{
			quantaPerBlock = BlockFluidBase.class.getDeclaredField("quantaPerBlock");
			quantaPerBlock.setAccessible(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static int getFlowDecay(BlockLiquid liquidBlock, World world, int x, int y, int z)
	{
		try
		{
			if (getFlowDecay == null)
			{
				getFlowDecay = BlockLiquid.class.getDeclaredMethod("func_149804_e", World.class, Integer.TYPE, Integer.TYPE, Integer.TYPE);
				getFlowDecay.setAccessible(true);
			}

			return (Integer) getFlowDecay.invoke(liquidBlock, world, x, y, z);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}

	public static int getEffectiveFlowDecay(BlockLiquid liquidBlock, World world, int x, int y, int z)
	{
		try
		{
			if (getEffectiveFlowDecay == null)
			{
				getEffectiveFlowDecay = BlockDynamicLiquid.class.getDeclaredMethod("getEffectiveFlowDecay", World.class, Integer.TYPE, Integer.TYPE, Integer.TYPE);
				getEffectiveFlowDecay.setAccessible(true);
			}

			return (Integer) getEffectiveFlowDecay.invoke(liquidBlock, world, x, y, z);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}

	public static int getQuantaPerBlock(BlockFluidBase fluidBlock)
	{
		try
		{
			return quantaPerBlock.getInt(fluidBlock);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 0;
		}
	}

	public static boolean doesFlowGetBlockedBy(BlockDynamicLiquid flowingBlock, World world, int x, int y, int z)
	{
		try
		{
			if (doesFlowGetBlockedBy == null)
				doesFlowGetBlockedBy = BlockDynamicLiquid.class.getDeclaredMethod("func_149807_p", World.class, Integer.TYPE, Integer.TYPE, Integer.TYPE);

			return (Boolean) doesFlowGetBlockedBy.invoke(flowingBlock, world, x, y, z);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return true;
		}
	}

	public static void flowIntoBlock(BlockDynamicLiquid flowingBlock, World world, int x, int y, int z, int newFlowDecay)
	{
		try
		{
			if (flowIntoBlock == null)
				flowIntoBlock = BlockDynamicLiquid.class.getDeclaredMethod("func_149813_h", World.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE);

			flowIntoBlock.invoke(flowingBlock, world, x, y, z, newFlowDecay);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static boolean canLiquidDisplaceBlock(BlockDynamicLiquid flowingBlock, World world, int x, int y, int z)
	{
		try
		{
			if (canLiquidDisplaceBlock == null)
			{
				canLiquidDisplaceBlock = BlockDynamicLiquid.class.getDeclaredMethod("func_149809_q", World.class, Integer.TYPE, Integer.TYPE, Integer.TYPE);
				canLiquidDisplaceBlock.setAccessible(true);
			}

			return (Boolean) canLiquidDisplaceBlock.invoke(flowingBlock, world, x, y, z);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public static int getSmallestFlowDecay(BlockDynamicLiquid flowingBlock, World world, int x, int y, int z, int curSmallest)
	{
		try
		{
			if (getSmallestFlowDecay == null)
			{
				getSmallestFlowDecay = BlockDynamicLiquid.class.getDeclaredMethod("func_149810_a", World.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE);
				getSmallestFlowDecay.setAccessible(true);
			}

			return (Integer) getSmallestFlowDecay.invoke(flowingBlock, world, x, y, z, curSmallest);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return curSmallest;
		}
	}
}
