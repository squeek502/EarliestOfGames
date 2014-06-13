package squeek.earliestofgames.filters;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

public class EmptyFilter extends IFilter
{
	@Override
	public boolean passesFilter(Fluid fluid)
	{
		return true;
	}
	
	@Override
	public boolean passesFilter(ItemStack item) 
	{
		return true;
	}
	
	@Override
	public boolean passesFilter(Entity entity)
	{
		return true;
	}
}
