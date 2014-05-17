package squeek.earliestofgames.filters;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.item.ItemStack;

public abstract class IFilter
{
	public boolean allowsAllLiquidsThrough()
	{
		return true;
	}
	
	public boolean passesFilter(ItemStack item)
	{
		if (item == null)
			return false;
		
		if (allowsAllLiquidsThrough() && Block.getBlockFromItem(item.getItem()).getMaterial().isLiquid())
			return true;
		
		return false;
	}
}
