package squeek.earliestofgames.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;

public class BlockCrate extends BlockContainer
{
	public BlockCrate()
	{
		super(Material.wood);
		setBlockName("crate");
	}
	
	@Override
	public int damageDropped(int meta)
	{
		return meta;
	}
}
