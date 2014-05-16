package squeek.earliestofgames.content;

import squeek.earliestofgames.ModInfo;
import squeek.earliestofgames.helpers.GuiHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class Crate extends BlockContainer
{
	public String blockName;

	public Crate()
	{
		super(Material.wood);
		setBlockName(ModInfo.MODID + "." + this.getClass().getSimpleName());
		setBlockTextureName(ModInfo.MODID + ":" + this.getClass().getSimpleName());
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
		return new CrateTile();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
	{
		if (player.isSneaking())
			return false;
		else
		{
			TileEntity tile = world.getTileEntity(x, y, z);
			if (tile != null)
			{
				return GuiHelper.openGuiOfTile(player, tile);
			}
		}

		return super.onBlockActivated(world, x, y, z, player, p_149727_6_, p_149727_7_, p_149727_8_, p_149727_9_);
	}
}
