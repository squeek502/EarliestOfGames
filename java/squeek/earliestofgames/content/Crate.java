package squeek.earliestofgames.content;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import squeek.earliestofgames.ModEarliestOfGames;
import squeek.earliestofgames.ModInfo;
import squeek.earliestofgames.filters.IFilter;
import squeek.earliestofgames.helpers.GuiHelper;

public class Crate extends BlockContainer
{
	public static double sideWidth = 0.00125D;
	
	protected String blockName;

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

	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB collidingAABB, List collidingBoundingBoxes, Entity collidingEntity)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null && tile instanceof CrateTile && collidingEntity instanceof EntityItem)
		{
			EntityItem itemEntity = (EntityItem) collidingEntity;
			for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
			{
				if (side == ForgeDirection.UP || ((CrateTile)tile).canItemPassThroughSide(itemEntity.getEntityItem(), side))
					continue;

				double minX = this.minX, minY = this.minY, minZ = this.minZ;
				double maxX = this.maxX, maxY = this.maxY, maxZ = this.maxZ;

				if (side.offsetX != 0)
				{
					if (side.offsetX > 0)
						minX = maxX - sideWidth;
					else
						maxX = minX + sideWidth;
				}
				if (side.offsetY != 0)
				{
					if (side.offsetY > 0)
						minY = maxY - sideWidth;
					else
						maxY = minY + sideWidth;
				}
				if (side.offsetZ != 0)
				{
					if (side.offsetZ > 0)
						minZ = maxZ - sideWidth;
					else
						maxZ = minZ + sideWidth;
				}

				AxisAlignedBB AABB = AxisAlignedBB.getAABBPool().getAABB((double) x + minX, (double) y + minY, (double) z + minZ, (double) x + maxX, (double) y + maxY, (double) z + maxZ);

				if (AABB != null && collidingAABB.intersectsWith(AABB))
				{
					collidingBoundingBoxes.add(AABB);
				}
			}
		}
		else
		{
			AxisAlignedBB AABB = getOuterBoundingBox(world, x, y, z);

			if (AABB != null && collidingAABB.intersectsWith(AABB))
			{
				collidingBoundingBoxes.add(AABB);
			}
		}
	}

	// hack to get World.isBlockFullCube to return false so that items won't always get pushed out
	// just need to return a bounding box with an average side length < 1
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		return AxisAlignedBB.getAABBPool().getAABB(0D, 0D, 0D, 0D, 0D, 0D);
	}

	public AxisAlignedBB getOuterBoundingBox(World world, int x, int y, int z)
	{
		return AxisAlignedBB.getAABBPool().getAABB(x + minX, y + minY, z + minZ,
													x + maxX, y + maxY, z + maxZ);
	}
	
	public AxisAlignedBB getInnerBoundingBox(World world, int x, int y, int z)
	{
		AxisAlignedBB AABB = AxisAlignedBB.getAABBPool().getAABB(x + minX + sideWidth, y + minY + sideWidth, z + minZ + sideWidth,
		     													x + maxX - sideWidth, y + maxY - sideWidth, z + maxZ - sideWidth);
		ModEarliestOfGames.Log.info(AABB.getAverageEdgeLength());
		return AABB;
	}

	@Override
	public boolean isBlockNormalCube()
	{
		return false;
	}
}
