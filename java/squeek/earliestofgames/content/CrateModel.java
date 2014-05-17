package squeek.earliestofgames.content;

import squeek.earliestofgames.ModContent;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

public class CrateModel extends ModelBase
{
	private ModelRenderer[] frame = new ModelRenderer[ForgeDirection.VALID_DIRECTIONS.length];
	private float scale = 0.0625f;
	
	public CrateModel()
	{
		int sideWidth = (int) (ModContent.blockCrate.sideWidth / scale);
		int sideLength = (int) (1f / scale);
		
		for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
		{
			AxisAlignedBB sideBounds = ModContent.blockCrate.getSideBoundingBox(side);
			
			double originX = sideBounds.minX / scale, originY = sideBounds.minY / scale, originZ = sideBounds.minZ / scale;
			int sizeX = (int) ((sideBounds.maxX-sideBounds.minX) / scale);
			int sizeY = (int) ((sideBounds.maxY-sideBounds.minY) / scale);
			int sizeZ = (int) ((sideBounds.maxZ-sideBounds.minZ) / scale);
			
			frame[side.ordinal()] = new ModelRenderer(this, 0, 0).setTextureSize(16, 16).setTextureOffset(0, 0);
			frame[side.ordinal()].addBox((float) originX, (float) originY, (float) originZ, sizeX, sizeY, sizeZ);
		}
	}
	
	public void renderAll()
	{
		for (ModelRenderer framePart : frame)
		{
			framePart.mirror = true;
			framePart.render(scale);
		}
	}
	
	@Override
	public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
	{
		super.render(par1Entity, par2, par3, par4, par5, par6, par7);
	}
}
