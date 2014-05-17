package squeek.earliestofgames.content;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import squeek.earliestofgames.ModInfo;

public class CrateRenderer extends TileEntitySpecialRenderer
{
	private static final ResourceLocation crateTexture = new ResourceLocation(ModInfo.MODID_LOWER, "textures/blocks/Crate.png");
	private CrateModel crateModel = new CrateModel();

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float timer)
	{
		if (tile != null && tile instanceof CrateTile)
		{
			GL11.glPushMatrix();
			
			crateModel.renderAll();

			GL11.glPopMatrix();
		}
	}
}
