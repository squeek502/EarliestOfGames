package squeek.earliestofgames.content;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import squeek.earliestofgames.ModInfo;

public class CrateRenderer extends TileEntitySpecialRenderer
{
	private static final ResourceLocation frameTexture = new ResourceLocation(ModInfo.MODID_LOWER, "textures/blocks/crateFrame.png");
	private static final ResourceLocation sideTexture = new ResourceLocation(ModInfo.MODID_LOWER, "textures/blocks/crateSides.png");
	private CrateModel crateModel = new CrateModel();

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
			
			bindTexture(sideTexture);
			crateModel.renderSides();

			GL11.glPopMatrix();
		}
	}
}
