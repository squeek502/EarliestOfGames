package squeek.earliestofgames.content;

import squeek.earliestofgames.ModContent;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class CrateRenderer implements ISimpleBlockRenderingHandler
{
    public static int modelId = RenderingRegistry.getNextAvailableRenderId();

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
	{
		
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		if (modelId == this.modelId)
		{
			for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
			{
				if (side == ForgeDirection.UP)
					continue;
				
				AxisAlignedBB sideBounds = ModContent.blockCrate.getSideBoundingBox(side, 0D, 0D, 0D);
				renderer.setRenderBounds(sideBounds.minX, sideBounds.minY, sideBounds.minZ, sideBounds.maxX, sideBounds.maxY, sideBounds.maxZ);
				renderer.renderStandardBlock(block, x, y, z);
			}
		}
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId)
	{
		return true;
	}

	@Override
	public int getRenderId()
	{
		return modelId;
	}
}
