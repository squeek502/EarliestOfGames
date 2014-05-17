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
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_CULL_FACE);

			GL11.glPushMatrix();

			GL11.glTranslated(x, y, z);
			GL11.glColor3f(1, 1, 1);

			bindTexture(crateTexture);
			crateModel.renderAll();

			GL11.glPopMatrix();

			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_CULL_FACE);
		}
	}
}
