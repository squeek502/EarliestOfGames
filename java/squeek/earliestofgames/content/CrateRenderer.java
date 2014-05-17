package squeek.earliestofgames.content;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import squeek.earliestofgames.ModInfo;

public class CrateRenderer extends TileEntitySpecialRenderer implements IItemRenderer
{
	private static final ResourceLocation frameTexture = new ResourceLocation(ModInfo.MODID_LOWER, "textures/blocks/crateFrame.png");
	private static final ResourceLocation sideTexture = new ResourceLocation(ModInfo.MODID_LOWER, "textures/blocks/crateSides.png");
	private CrateModel crateModel = new CrateModel();

    private CrateTile dummyItemRenderTile = new CrateTile();

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float timer)
	{
		if (tile != null && tile instanceof CrateTile)
		{
			GL11.glPushMatrix();

			GL11.glTranslated(x, y, z);
			GL11.glColor3f(1, 1, 1);

			bindTexture(frameTexture);
			crateModel.renderFrame();
			crateModel.renderSides((CrateTile) tile, this.field_147501_a.field_147553_e);

			GL11.glPopMatrix();
		}
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		switch (type)
		{
			case ENTITY:
			{
				renderTileEntityAt(dummyItemRenderTile, -0.5D, 0.0D, -0.5D, 0.0F);
				return;
			}
			case EQUIPPED:
			{
				renderTileEntityAt(dummyItemRenderTile, 0.0F, 0.0F, 0.0F, 0.0F);
				return;
			}
			case EQUIPPED_FIRST_PERSON:
			{
				renderTileEntityAt(dummyItemRenderTile, 0.5F, 0.5F, 0.3F, 0.0F);
				return;
			}
			case INVENTORY:
			{
				renderTileEntityAt(dummyItemRenderTile, 0.5F, 0.3F, 0.5F, 0.0F);
				return;
			}
			default:
				return;
		}
	}
}
