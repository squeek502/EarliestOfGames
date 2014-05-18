package squeek.earliestofgames.content;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class LiquidFlow
{
	public LiquidFlowInfo[] liquidFlows = new LiquidFlowInfo[ForgeDirection.VALID_DIRECTIONS.length];
	protected Vec3 flowVector = Vec3.createVectorHelper(0D, 0D, 0D);
	
	public class LiquidFlowInfo
	{
		public Fluid fluid;
		
		public LiquidFlowInfo(Fluid fluid)
		{
			this.fluid = fluid;
		}
	}
	
	public void onLiquidFlowFrom(Block block, int flowDecay, ForgeDirection flowDirection)
	{
		Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
		if (fluid != null)
		{
			liquidFlows[flowDirection.ordinal()] = new LiquidFlowInfo(fluid);
		}
		
		recalculateFlowVector();
	}
	
	public void recalculateFlowVector()
	{
		flowVector.xCoord = flowVector.yCoord = flowVector.zCoord = 0D;
		
		for (ForgeDirection flowDirection : ForgeDirection.VALID_DIRECTIONS)
		{
			if (liquidFlows[flowDirection.ordinal()] != null)
			{
				flowVector.addVector(flowDirection.offsetX, flowDirection.offsetY, flowDirection.offsetZ);
			}
		}
		
		flowVector = flowVector.normalize();
	}
	
	public Vec3 getFlowVector()
	{
		return flowVector;
	}
	
	public void addFlowVelocityToEntity(Entity entity, Vec3 velocity)
	{
        Vec3 vec_flow = this.getFlowVector();
        velocity.xCoord += vec_flow.xCoord;
        velocity.yCoord += vec_flow.yCoord;
        velocity.zCoord += vec_flow.zCoord;
	}

	public boolean isFlowingTowardsSide(ForgeDirection side)
	{
		return (side.offsetX != 0 && side.offsetX * getFlowVector().xCoord > 0)
				|| (side.offsetY != 0 && side.offsetY * getFlowVector().yCoord > 0)
				|| (side.offsetZ != 0 && side.offsetZ * getFlowVector().zCoord > 0);
	}
}
