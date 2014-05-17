package squeek.earliestofgames.filters;

import net.minecraft.item.ItemStack;

public class SizeFilter implements IFilter
{
	float maxItemSize = 1f;

	@Override
	public boolean passesFilter(ItemStack item)
	{
		return getItemSize(item) <= maxItemSize;
	}

	public static float getItemSize(ItemStack itemStack)
	{
		return 1f;
	}

}
