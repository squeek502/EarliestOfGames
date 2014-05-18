package squeek.earliestofgames.content;

import net.minecraft.block.Block;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class LiquidFlow
{
	public LiquidFlowInfo[] sidedLiquidFlows = new LiquidFlowInfo[ForgeDirection.VALID_DIRECTIONS.length];
	protected Vec3 flowVector = new Vec3(0D, 0D, 0D);
	
	public class LiquidFlowInfo
	{
		public Fluid fluid;
		
		public LiquidFlowInfo(Fluid fluid)
		{
			this.fluid = fluid;
		}
	}
	
	public void onLiquidFlowFrom(Block block, int flowDecay, int flowDirection)
	{
		Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
		if (fluid != null)
		{
			sidedLiquidFlows[flowDirection] = new LiquidFlowInfo(fluid);
		}
	}
	
	public Vec3 getFlowVector()
	{
		
	}
}
