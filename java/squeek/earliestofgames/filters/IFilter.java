package squeek.earliestofgames.filters;

import squeek.earliestofgames.helpers.FluidHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

public abstract class IFilter
{
	public boolean passesFilter(ItemStack item)
	{
		if (item == null)
			return false;
		
		if (Block.getBlockFromItem(item.getItem()).getMaterial().isLiquid())
			return passesFilter(FluidHelper.getFluidTypeOfBlock(Block.getBlockFromItem(item.getItem())));
		
		return false;
	}
	
	public boolean passesFilter(Fluid fluid)
	{
		return true;
	}

	public boolean passesFilter(Entity entity)
	{
		if (entity instanceof EntityItem)
			return passesFilter(((EntityItem) entity).getEntityItem());
		
		return false;
	}
}
