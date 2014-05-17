package squeek.earliestofgames.filters;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class SizeFilter implements IFilter
{
	public float maxItemSize = 1f;

	public static float getItemSize(ItemStack itemStack)
	{
		if (Block.getBlockFromItem(itemStack.getItem()).getMaterial() == Material.water)
			return 0f;
		
		if (Block.getBlockFromItem(itemStack.getItem()) == Blocks.gravel)
			return 0.5f;
		
		return 1f;
	}

	@Override
	public boolean passesFilter(ItemStack item)
	{
		return item != null && getItemSize(item) <= maxItemSize;
	}
}
