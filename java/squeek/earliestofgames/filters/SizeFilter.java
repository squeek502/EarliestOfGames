package squeek.earliestofgames.filters;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class SizeFilter extends IFilter
{
	public float maxItemSize = 1f;

	public static float getItemSize(ItemStack itemStack)
	{
		if (Block.getBlockFromItem(itemStack.getItem()).getMaterial().isLiquid())
			return 0.001f;
		
		if (Block.getBlockFromItem(itemStack.getItem()) == Blocks.gravel)
			return 0.5f;
		
		return 1f;
	}

	@Override
	public boolean passesFilter(ItemStack item)
	{
		if (super.passesFilter(item))
			return true;
		
		return item != null && getItemSize(item) <= maxItemSize;
	}
}
