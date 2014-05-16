package squeek.earliestofgames.blocks;

import squeek.earliestofgames.ModInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class Crate extends BlockContainer
{
	public String blockName;
	
	public Crate()
	{
		super(Material.wood);
		setBlockName(ModInfo.MODID+"."+this.getClass().getSimpleName());
	}
	
	@Override
	public Block setBlockName(String blockName)
	{
		this.blockName = blockName;
		return super.setBlockName(blockName);
	}
	
	public String getBlockName()
	{
		return blockName;
	}
	
	@Override
	public int damageDropped(int meta)
	{
		return meta;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return null;
	}
}
