package squeek.earliestofgames.blocks;

import squeek.earliestofgames.ModInfo;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCrate extends BlockContainer
{
	public BlockCrate()
	{
		super(Material.wood);
		setBlockName(ModInfo.MODID+"."+this.getClass().getName());
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
