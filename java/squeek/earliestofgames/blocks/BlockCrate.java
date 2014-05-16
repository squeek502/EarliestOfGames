package squeek.earliestofgames.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockCrate extends Block
{

	protected BlockCrate(Material material)
	{
		super(material);
		setBlockName("crate");
	}
	
	@Override
	public int damageDropped(int meta)
	{
		return meta;
	}
	
}
