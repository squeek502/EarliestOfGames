package squeek.earliestofgames.filters;

import net.minecraft.block.BlockLiquid;
import net.minecraft.item.ItemStack;

public interface IFilter
{
	public boolean passesFilter(ItemStack item);
}
