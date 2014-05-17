package squeek.earliestofgames.filters;

import net.minecraft.item.ItemStack;

public class SizeFilter implements IFilter
{

	@Override
	public boolean passesFilter(ItemStack item)
	{
		return false;
	}

}
