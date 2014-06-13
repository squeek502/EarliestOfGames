package squeek.earliestofgames.base;

import squeek.earliestofgames.asm.Wrappers;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class FluidFlow
{
	protected int flowDecay = -1;
	protected Fluid fluid = null;

	public FluidFlow()
	{
	}

	public FluidFlow(Fluid fluid, int flowDecay)
	{
		this.fluid = fluid;
		this.flowDecay = flowDecay;
	}

	public int getFlowDecay()
	{
		return flowDecay;
	}

	public Fluid getFluid()
	{
		return fluid;
	}
	
	public void setFlowDecay(int flowDecay)
	{
		this.flowDecay = flowDecay;
	}
	
	public void setFluid(Fluid fluid)
	{
		this.fluid = fluid;
	}

	public int getQuantaRemaining()
	{
		int quantaPerBlock = 8;

		if (fluid != FluidRegistry.WATER || fluid != FluidRegistry.LAVA && fluid.getBlock() instanceof BlockFluidBase)
			quantaPerBlock = Wrappers.getQuantaPerBlock((BlockFluidBase) fluid.getBlock());

		return Math.max(0, quantaPerBlock - flowDecay);
	}

	@Override
	public boolean equals(Object object)
	{
		if (super.equals(object))
			return true;

		if (!(object instanceof FluidFlow))
			return false;

		FluidFlow other = (FluidFlow) object;

		return other.getFluid() == this.getFluid() && other.getFlowDecay() == this.getFlowDecay();
	}

	@Override
	public int hashCode()
	{
		return 41 * (41 + getFluid().getID()) + getFlowDecay();
	}
	
	public static boolean areFluidFlowsEqual(FluidFlow first, FluidFlow second)
	{
		return !((first == null && second != null) || (first != null && !first.equals(second)));
	}

	public void getDescriptionPacket(NBTTagCompound flowTag)
	{
		flowTag.setString("Fluid", fluid.getName());
		flowTag.setShort("Decay", (short) flowDecay);
	}

	public FluidFlow populateFromNBT(NBTTagCompound flowTag)
	{
		this.fluid = FluidRegistry.getFluid(flowTag.getString("Fluid"));
		this.flowDecay = (int) flowTag.getShort("Decay");
		return this;
	}

	public static FluidFlow getNewFluidFlowFromNBT(NBTTagCompound flowTag)
	{
		return new FluidFlow().populateFromNBT(flowTag);
	}
}
