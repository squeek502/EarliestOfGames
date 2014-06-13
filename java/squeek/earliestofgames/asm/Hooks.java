package squeek.earliestofgames.asm;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockLiquid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import squeek.earliestofgames.ModEarliestOfGames;
import squeek.earliestofgames.base.IFluidFlowHandler;
import squeek.earliestofgames.base.IHollowBlock;
import squeek.earliestofgames.helpers.FluidHelper;

public class Hooks
{
	// return true to cancel the default behavior
	public static boolean onFlowIntoBlockFrom(BlockDynamicLiquid flowingBlock, World world, int x, int y, int z, int newFlowDecay, int flowDirection)
	{
		if (world == null)
			return false;
		
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null && tile instanceof IFluidFlowHandler)
		{
			return ((IFluidFlowHandler) tile).handleFlowIntoBlock(FluidHelper.getFluidTypeOfBlock(flowingBlock), newFlowDecay, ForgeDirection.getOrientation(flowDirection).getOpposite());
		}
		try
		{
			Wrappers.flowIntoBlock(flowingBlock, world, x, y, z, newFlowDecay);
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
		if (tile != null && tile instanceof IFluidFlowHandler)
		{
			return ((IFluidFlowHandler) tile).doesFlowGetBlockedBySide(FluidHelper.getFluidTypeOfBlock(flowingBlock), ForgeDirection.getOrientation(flowDirection).getOpposite());
		}
		try
		{
			return Wrappers.doesFlowGetBlockedBy(flowingBlock, world, x, y, z);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return true;
		}
	}
	
	// return >= 0 to override default behavior
	public static int getFlowDecay(BlockLiquid liquidBlock, World world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null && tile instanceof IFluidFlowHandler)
		{
			int flowDecay = ((IFluidFlowHandler) tile).getFlowDecay(FluidHelper.getFluidTypeOfBlock(liquidBlock));
			return flowDecay;
		}
		return -1;
	}

	// return >= 0 to override default behavior
	public static int getEffectiveFlowDecay(BlockLiquid liquidBlock, IBlockAccess world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null && tile instanceof IFluidFlowHandler)
		{
			int flowDecay = ((IFluidFlowHandler) tile).getEffectiveFlowDecay(FluidHelper.getFluidTypeOfBlock(liquidBlock));
			return flowDecay;
		}
		return -1;
	}
	
	public static int getSmallestFlowDecayTo(BlockDynamicLiquid flowingBlock, World world, int x, int y, int z, int curSmallest, int fromSide)
	{
        int flowDecay = Hooks.getFlowDecayTo(flowingBlock, world, x, y, z, fromSide);

        if (flowDecay < 0)
        {
            return curSmallest;
        }
        else
        {
            if (flowDecay == 0)
            {
            	int numAdjacentSources = ReflectionHelper.getPrivateValue(BlockDynamicLiquid.class, flowingBlock, "field_149815_a", "a");
            	ReflectionHelper.setPrivateValue(BlockDynamicLiquid.class, flowingBlock, numAdjacentSources+1, "field_149815_a", "a");
            }

            if (flowDecay >= 8)
            {
            	flowDecay = 0;
            }

            return curSmallest >= 0 && flowDecay >= curSmallest ? curSmallest : flowDecay;
        }
	}
	
	public static int getFlowDecayTo(BlockDynamicLiquid flowingBlock, World world, int x, int y, int z, int fromSide)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null && tile instanceof IFluidFlowHandler)
		{
			return ((IFluidFlowHandler) tile).getFlowDecayTo(FluidHelper.getFluidTypeOfBlock(flowingBlock), ForgeDirection.getOrientation(fromSide).getOpposite());
		}
		return Wrappers.getFlowDecay(flowingBlock, world, x, y, z);
	}
	
	public static boolean canLiquidDisplaceBlockFrom(BlockDynamicLiquid flowingBlock, World world, int x, int y, int z, int flowDirection)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null && tile instanceof IFluidFlowHandler)
		{
			boolean canDisplace = !((IFluidFlowHandler) tile).doesFlowGetBlockedBySide(FluidHelper.getFluidTypeOfBlock(flowingBlock), ForgeDirection.getOrientation(flowDirection).getOpposite());
			ModEarliestOfGames.Log.info("canDisplace: " + canDisplace + " side: " + ForgeDirection.getOrientation(flowDirection).getOpposite());
			return canDisplace;
		}
		return Wrappers.canLiquidDisplaceBlock(flowingBlock, world, x, y, z);
	}
	
	public static boolean canLiquidDisplaceBlock(BlockDynamicLiquid flowingBlock, World world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null && tile instanceof IFluidFlowHandler)
		{
			return true;
		}
		return false;
	}
	
	public static int isBlockFullCube(World world, int x, int y, int z)
	{
		Block block = world.getBlock(x, y, z);
		if (block instanceof IHollowBlock)
		{
			return ((IHollowBlock) block).isBlockFullCube(world, x, y, z) ? 1 : 0;
		}
		return -1;
	}
}
