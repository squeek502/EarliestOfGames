package squeek.earliestofgames.filters;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;

public class WeightFilter implements IFilter
{
	public static float getItemWeight(ItemStack itemStack)
	{
		Block block = Block.getBlockFromItem(itemStack.getItem());
		return block.getMaterial() == Material.iron ? 1.5f : 1f;
	}

	@Override
	public boolean passesFilter(ItemStack item)
	{
		return getItemWeight();
	}
}
