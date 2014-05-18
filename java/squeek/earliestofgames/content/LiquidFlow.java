package squeek.earliestofgames.content;

import squeek.earliestofgames.asm.Hooks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class LiquidFlow
{
	public LiquidFlowInfo[] liquidFlows = new LiquidFlowInfo[ForgeDirection.VALID_DIRECTIONS.length];
	protected Vec3 flowVector = Vec3.createVectorHelper(0D, 0D, 0D);
	protected CrateTile crate = null;
	
	public LiquidFlow(CrateTile parentTile)
	{
		this.crate = parentTile;
	}
	
	public class LiquidFlowInfo
	{
		public Fluid fluid;
		public int flowDecay;
		
		public LiquidFlowInfo(Fluid fluid, int flowDecay)
		{
			this.fluid = fluid;
			this.flowDecay = flowDecay;
		}
	}
	
	public void onLiquidFlowFrom(Block block, int flowDecay, ForgeDirection flowDirection)
	{
		// TODO: Water has no fluid for BlockDynamicFluid?
		Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
		liquidFlows[flowDirection.ordinal()] = new LiquidFlowInfo(fluid, flowDecay);
		
		Hooks.onFlowIntoBlockFrom((BlockDynamicLiquid) block, crate.getWorldObj(), crate.xCoord+flowDirection.offsetX, crate.yCoord+flowDirection.offsetY, crate.zCoord+flowDirection.offsetZ, 9, flowDirection.ordinal());
		
		recalculateFlowVector();
	}
	
	public void recalculateFlowVector()
	{
		flowVector.xCoord = flowVector.yCoord = flowVector.zCoord = 0D;
		
		for (ForgeDirection flowDirection : ForgeDirection.VALID_DIRECTIONS)
		{
			if (liquidFlows[flowDirection.ordinal()] != null)
			{
				flowVector = flowVector.addVector(flowDirection.offsetX, flowDirection.offsetY, flowDirection.offsetZ);
			}
		}
		
		flowVector = flowVector.normalize();
	}
	
	public Vec3 getFlowVector()
	{
		return flowVector;
	}
	
	public int getFlowDecay()
	{
		int lowestFlowDecay = 1000;
		for (LiquidFlowInfo flow : liquidFlows)
		{
			if (flow != null && flow.flowDecay < lowestFlowDecay)
				lowestFlowDecay = flow.flowDecay;
		}
		return lowestFlowDecay != 1000 ? lowestFlowDecay : -1;
	}
	
	public boolean isFlowing()
	{
		return flowVector.xCoord != 0 || flowVector.yCoord != 0 || flowVector.zCoord != 0;
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
	
	public void update()
	{
		if (isFlowing())
		{
			for (EntityItem itemEntity : crate.getItemEntitiesInsideOuterBounds())
			{
	            Vec3 velocity = crate.getWorldObj().getWorldVec3Pool().getVecFromPool(0.0D, 0.0D, 0.0D);
				addFlowVelocityToEntity(itemEntity, velocity);
				itemEntity.motionX += velocity.xCoord;
				itemEntity.motionY += velocity.yCoord;
				itemEntity.motionZ += velocity.zCoord;
			}
		}
	}
}
