package squeek.earliestofgames.base;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class BlockFluidJunction extends Block
{
	protected BlockFluidJunction(Material material)
	{
		super(material);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
	{
		super.onNeighborBlockChange(world, x, y, z, block);
		
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null && tile instanceof TileEntityFluidJunction)
			((TileEntityFluidJunction) tile).onNeighborBlockChange(block);
	}
}
