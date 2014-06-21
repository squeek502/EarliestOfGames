package squeek.earliestofgames.helpers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import squeek.earliestofgames.asm.Wrappers;
import squeek.earliestofgames.base.TileEntityFluidJunction;

public class FluidHelper
{
	public static int getFlowDecayOfFluidAt(World world, int x, int y, int z, Fluid fluid)
	{
		int flowDecay = -1;
		TileEntity tile = world.getTileEntity(x, y, z);

		if (tile != null && tile instanceof TileEntityFluidJunction)
		{
			flowDecay = ((TileEntityFluidJunction) tile).getFlowDecay(fluid);
		}
		else
		{
			Block block = world.getBlock(x, y, z);

			if (block != null && getFluidTypeOfBlock(block) == fluid)
			{
				if (block instanceof BlockLiquid)
					flowDecay = Wrappers.getFlowDecay(((BlockLiquid) block), world, x, y, z);
				else if (block instanceof BlockFluidBase)
					flowDecay = convertQuantaRemainingToFlowDecay(((BlockFluidBase) block).getQuantaValue(world, x, y, z), getFluidTypeOfBlock(block));
			}
		}

		return flowDecay;
	}

	public static Fluid getFluidAt(World world, int x, int y, int z, ForgeDirection outputSide)
	{
		Fluid fluid = null;

		TileEntity tile = world.getTileEntity(x, y, z);

		if (tile != null && tile instanceof TileEntityFluidJunction)
		{
			if (((TileEntityFluidJunction) tile).getOutputFlow(outputSide) != null)
				fluid = ((TileEntityFluidJunction) tile).getOutputFlow(outputSide).getFluid();
		}
		else
		{
			Block block = world.getBlock(x, y, z);

			if (block != null)
				fluid = getFluidTypeOfBlock(block);
		}

		return fluid;
	}

	public static int convertQuantaRemainingToFlowDecay(int quanta, Fluid fluid)
	{
		return getQuantaPerBlock(fluid.getBlock()) - quanta;
	}

	public static int convertFlowDecayToQuantaRemaining(int flowDecay, Fluid fluid)
	{
		return getQuantaPerBlock(fluid.getBlock()) - flowDecay;
	}

	public static int getQuantaPerBlock(Fluid fluid)
	{
		return getQuantaPerBlock(fluid.getBlock());
	}

	public static int getQuantaPerBlock(Block block)
	{
		if (block instanceof BlockFluidBase)
			return Wrappers.getQuantaPerBlock((BlockFluidBase) block);
		else
		{
			if (isBlockWater(block))
				return 8;
			else if (isBlockLava(block))
				return 8;
		}
		return 0;
	}

	public static boolean isBlockWater(Block block)
	{
		return block != null && block.getMaterial() == Material.water;
	}

	public static boolean isBlockLava(Block block)
	{
		return block != null && block.getMaterial() == Material.lava;
	}

	public static boolean isBlockLiquid(Block block)
	{
		return block != null && block.getMaterial().isLiquid();
	}

	public static Fluid getFluidTypeOfBlock(Block block)
	{
		if (!isBlockLiquid(block))
			return null;
		else if (isBlockWater(block))
			return FluidRegistry.WATER;
		else if (isBlockLava(block))
			return FluidRegistry.LAVA;
		else
			return FluidRegistry.lookupFluidForBlock(block);
	}

	public static Block getFlowingFluidBlock(Fluid fluid)
	{
		if (fluid == null)
			return null;
		else if (fluid == FluidRegistry.WATER)
			return Blocks.flowing_water;
		else if (fluid == FluidRegistry.LAVA)
			return Blocks.flowing_lava;
		else
			return fluid.getBlock();
	}

	public static int getNextFlowDecay(Fluid fluid, int curFlowDecay, ForgeDirection flowDirection)
	{
		if (curFlowDecay < 0)
			return curFlowDecay;

		int quantaPerBlock = getQuantaPerBlock(fluid);
		if (flowDirection == ForgeDirection.DOWN || flowDirection == ForgeDirection.UP)
		{
			if (curFlowDecay >= quantaPerBlock)
				return curFlowDecay;
			else
				return curFlowDecay + quantaPerBlock;
		}
		else
		{
			if (curFlowDecay >= quantaPerBlock)
				return 1;
			else if (curFlowDecay + 1 >= quantaPerBlock)
				return -1;
			else
				return curFlowDecay + 1;
		}
	}
}
