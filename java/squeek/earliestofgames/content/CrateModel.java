package squeek.earliestofgames.content;

import squeek.earliestofgames.ModContent;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

public class CrateModel extends ModelBase
{
	private ModelRenderer[] frame = new ModelRenderer[ForgeDirection.VALID_DIRECTIONS.length*2];
	private ModelRenderer[] sides = new ModelRenderer[ForgeDirection.VALID_DIRECTIONS.length];
	private ModelRenderer sideBox;
	private float scale = 0.0625f;
	
	public CrateModel()
	{
		int sideWidth = (int) (ModContent.blockCrate.sideWidth / scale);
		int sideLength = (int) (1f / scale);
		int pillarIndex = 0;
		
		for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
		{
			if (side == ForgeDirection.UP || side == ForgeDirection.DOWN)
				continue;
			
			AxisAlignedBB sideBounds = ModContent.blockCrate.getSideBoundingBox(side);
			int numPillars = side.offsetX != 0 ? 4 : 2;
			
			for (int pillarNum = 0; pillarNum < numPillars; pillarNum++)
			{
				double originX = sideBounds.minX / scale, originY = sideBounds.minY / scale, originZ = sideBounds.minZ / scale;
				int sizeX = (int) ((sideBounds.maxX-sideBounds.minX) / scale);
				int sizeY = (int) ((sideBounds.maxY-sideBounds.minY) / scale);
				int sizeZ = (int) ((sideBounds.maxZ-sideBounds.minZ) / scale);
				
				if (side.offsetX != 0)
				{
					if (pillarNum < 2)
					{
						sizeZ = sizeX;
						originZ = originZ + pillarNum * (sideLength - sideWidth);
					}
					else
					{
						originZ += sideWidth;
						sizeZ -= sideWidth*2;
						originY = originY + (pillarNum-2) * (sideLength - sideWidth);
						sizeY = sizeX;
					}
				}
				else if (side.offsetZ != 0)
				{
					originX += sideWidth;
					sizeX -= sideWidth*2;
					originY = originY + pillarNum * (sideLength - sideWidth);
					sizeY = sizeZ;
				}
				
				ModelRenderer pillar = new ModelRenderer(this, 0, 0).setTextureSize(16, 16);
				pillar.addBox((float) originX, (float) originY, (float) originZ, sizeX, sizeY, sizeZ);
				
				frame[pillarIndex++] = pillar;
			}
		}
		
		sideBox = new ModelRenderer(this, 1, 1).setTextureSize(14, 14);
		sideBox.addBox(1, 1, 1, 14, 14, 14);
		sideBox.mirror = true;
		
		/*
		for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
		{
			if (side == ForgeDirection.UP)
				continue;

			AxisAlignedBB sideBounds = ModContent.blockCrate.getSideBoundingBox(side);
			
			double originX = sideBounds.minX / scale, originY = sideBounds.minY / scale, originZ = sideBounds.minZ / scale;
			int sizeX = (int) ((sideBounds.maxX-sideBounds.minX) / scale);
			int sizeY = (int) ((sideBounds.maxY-sideBounds.minY) / scale);
			int sizeZ = (int) ((sideBounds.maxZ-sideBounds.minZ) / scale);
			
			if (side.offsetX != 0)
			{
				originX += Math.abs(side.offsetX) * (float) sideWidth/4;
				sizeX /= 2;
				originZ += sideWidth;
				originY += sideWidth;
				sizeZ -= sideWidth*2;
				sizeY -= sideWidth*2;
			}
			else if (side.offsetY != 0)
			{
				originY += Math.abs(side.offsetY) * (float) sideWidth/4;
				sizeY /= 2;
				originX += sideWidth;
				originZ += sideWidth;
				sizeX -= sideWidth*2;
				sizeZ -= sideWidth*2;
			}
			else if (side.offsetZ != 0)
			{
				originZ += Math.abs(side.offsetZ) * (float) sideWidth/4;
				sizeZ /= 2;
				originX += sideWidth;
				originY += sideWidth;
				sizeX -= sideWidth*2;
				sizeY -= sideWidth*2;
			}

			ModelRenderer sideModel = new ModelRenderer(this, 0, 0).setTextureSize(16, 16);
			sideModel.addBox((float) originX, (float) originY, (float) originZ, sizeX, sizeY, sizeZ);
			
			sides[side.ordinal()] = sideModel;
		}
		*/
	}
	
	public void renderFrame()
	{
		for (ModelRenderer framePart : frame)
		{
			framePart.render(scale);
		}
	}
	
	public void renderSides()
	{
		for (ModelRenderer side : sides)
		{
			if (side != null)
				side.render(scale);
		}
		sideBox.setTextureOffset(-2, -2);
		sideBox.setTextureSize(16, 16);
		sideBox.render(scale);
	}
	
	@Override
	public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
	{
		super.render(par1Entity, par2, par3, par4, par5, par6, par7);
	}
}
