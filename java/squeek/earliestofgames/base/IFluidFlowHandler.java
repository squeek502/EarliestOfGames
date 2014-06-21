package squeek.earliestofgames.base;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;

public interface IFluidFlowHandler
{
	public int getEffectiveFlowDecay(Fluid fluid);
	
	public int getFlowDecay(Fluid fluid);
	
	public boolean handleFlowIntoBlock(Fluid fluid, int newFlowDecay, ForgeDirection side);

	public boolean doesFlowGetBlockedBySide(Fluid fluid, ForgeDirection side);

	public int getFlowDecayTo(Fluid fluid, ForgeDirection side);
}
