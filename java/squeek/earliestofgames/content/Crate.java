package squeek.earliestofgames.content;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import squeek.earliestofgames.ModInfo;
import squeek.earliestofgames.base.IHollowBlock;
import squeek.earliestofgames.base.TileEntityFluidJunction;
import squeek.earliestofgames.filters.EmptyFilter;
import squeek.earliestofgames.helpers.GuiHelper;

public class Crate extends BlockContainer implements IHollowBlock
{
	public static double sideWidth = 0.125D;

	protected String blockName;
	public static final Material crateMaterial = (new Material(MapColor.woodColor)
	{
		// doesn't block water flowing through it
		public boolean blocksMovement()
		{
			return true;
		}
	});

	public Crate()
	{
		super(Material.wood);
		setBlockName(ModInfo.MODID + "." + this.getClass().getSimpleName());
		setBlockTextureName("minecraft:planks_oak");
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
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int sideId, float p_149727_7_, float p_149727_8_, float p_149727_9_)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null && tile instanceof CrateTile)
		{
			CrateTile crate = (CrateTile) tile;
			ItemStack heldItem = player.getHeldItem();
			ForgeDirection side = ForgeDirection.getOrientation(sideId);
			if (player.isSneaking())
			{
				if (heldItem == null)
				{
					crate.setFilterOfSide(side, new EmptyFilter());
					return true;
				}
				else
					return false;
			}
			else
			{
				if (heldItem != null && crate.getFilter(side) instanceof EmptyFilter)
				{
					if (Block.getBlockFromItem(heldItem.getItem()) == Blocks.wooden_slab)
					{
						crate.setFilterOfSide(side, null);
						if (!player.capabilities.isCreativeMode)
							heldItem.stackSize--;
						return true;
					}
				}

				return GuiHelper.openGuiOfTile(player, crate);
			}
		}

		return super.onBlockActivated(world, x, y, z, player, sideId, p_149727_7_, p_149727_8_, p_149727_9_);
	}

	public AxisAlignedBB getSideBoundingBox(ForgeDirection side)
	{
		return getSideBoundingBox(side, 0, 0, 0);
	}

	public AxisAlignedBB getSideBoundingBox(ForgeDirection side, double offsetX, double offsetY, double offsetZ)
	{
		return getSideBoundingBox(side, offsetX, offsetY, offsetZ, 1f);
	}

	public AxisAlignedBB getSideBoundingBox(ForgeDirection side, double offsetX, double offsetY, double offsetZ, float depthScale)
	{
		return getSideBoundingBox(side, offsetX, offsetY, offsetZ, depthScale, 1f, 1f);
	}

	public AxisAlignedBB getSideBoundingBox(ForgeDirection side, double offsetX, double offsetY, double offsetZ, float depthScale, float widthScale, float heightScale)
	{
		double minX = this.minX, minY = this.minY, minZ = this.minZ;
		double maxX = this.maxX, maxY = this.maxY, maxZ = this.maxZ;

		if (side.offsetX != 0)
		{
			if (side.offsetX > 0)
				minX = maxX - sideWidth * depthScale;
			else
				maxX = minX + sideWidth * depthScale;
			if (widthScale != 1) // z axis
			{
				double width = maxZ - minZ;
				if (widthScale > 0)
					maxZ = minZ + width * widthScale;
				else
					minZ = maxZ + width * widthScale;
			}
			if (heightScale != 1) // y axis
			{
				double height = maxZ - minZ;
				if (heightScale > 0)
					maxY = minY + height * heightScale;
				else
					minY = maxY + height * heightScale;
			}
		}
		if (side.offsetY != 0)
		{
			if (side.offsetY > 0)
				minY = maxY - sideWidth * depthScale;
			else
				maxY = minY + sideWidth * depthScale;
			if (widthScale != 1) // z axis
			{
				double width = maxZ - minZ;
				if (widthScale > 0)
					maxZ = minZ + width * widthScale;
				else
					minZ = maxZ + width * widthScale;
			}
			if (heightScale != 1) // x axis
			{
				double height = maxX - minX;
				if (heightScale > 0)
					maxX = minX + height * heightScale;
				else
					minX = maxX + height * heightScale;
			}
		}
		if (side.offsetZ != 0)
		{
			if (side.offsetZ > 0)
				minZ = maxZ - sideWidth * depthScale;
			else
				maxZ = minZ + sideWidth * depthScale;
			if (widthScale != 1) // x axis
			{
				double width = maxX - minX;
				if (widthScale > 0)
					maxX = minX + width * widthScale;
				else
					minX = maxX + width * widthScale;
			}
			if (heightScale != 1) // y axis
			{
				double height = maxY - minY;
				if (heightScale > 0)
					maxY = minY + height * heightScale;
				else
					minY = maxY + height * heightScale;
			}
		}

		return AxisAlignedBB.getAABBPool().getAABB(offsetX + minX, offsetY + minY, offsetZ + minZ, offsetX + maxX, offsetY + maxY, offsetZ + maxZ);
	}

	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB collidingAABB, List collidingBoundingBoxes, Entity collidingEntity)
	{
		// hack...
		// this function is called with a null entity in World.isBlockFullCube
		if (collidingEntity == null)
			return;

		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null && tile instanceof CrateTile)
		{
			CrateTile crate = (CrateTile) tile;
			boolean isItem = collidingEntity instanceof EntityItem;
			for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
			{
				boolean canPassThrough = ((CrateTile) tile).canEntityPassThroughSide(collidingEntity, side);
				List<AxisAlignedBB> AABBs = new ArrayList<AxisAlignedBB>(4);

				if (isItem && canPassThrough)
					continue;
				else if (isItem)
				{
					AABBs.add(getSideBoundingBox(side, x, y, z, 0f));
				}
				else if (canPassThrough)
				{
					AABBs.add(getSideBoundingBox(side, x, y, z, 1f, (float) sideWidth, 1f));
					AABBs.add(getSideBoundingBox(side, x, y, z, 1f, (float) -sideWidth, 1f));
					AABBs.add(getSideBoundingBox(side, x, y, z, 1f, 1f, (float) sideWidth));
					AABBs.add(getSideBoundingBox(side, x, y, z, 1f, 1f, (float) -sideWidth));
				}
				else
				{
					AABBs.add(getSideBoundingBox(side, x, y, z));
				}

				for (AxisAlignedBB AABB : AABBs)
				{
					if (AABB != null && collidingAABB.intersectsWith(AABB))
					{
						collidingBoundingBoxes.add(AABB);
					}
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
		return getOuterBoundingBox(world, x, y, z);
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
		return AABB;
	}

	// from IHollowBlock
	@Override
	public boolean isBlockFullCube(World world, int x, int y, int z)
	{
		return false;
	}

	@Override
	public boolean isBlockNormalCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public int getRenderType()
	{
		return -1;
	}

	@Override
	public int getRenderBlockPass()
	{
		return 1;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side)
	{
		return super.shouldSideBeRendered(world, x, y, z, side);
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null && tile instanceof CrateTile)
			return ((CrateTile) tile).getFilter(side) == null;

		return true;
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
