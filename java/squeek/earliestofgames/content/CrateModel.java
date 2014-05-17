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

		AxisAlignedBB sideBounds = ModContent.blockCrate.getSideBoundingBox(ForgeDirection.EAST);
		
		int sizeX = (int) ((sideBounds.maxX-sideBounds.minX) / scale);
		int sizeY = (int) ((sideBounds.maxY-sideBounds.minY) / scale);
		int sizeZ = (int) ((sideBounds.maxZ-sideBounds.minZ) / scale);

		sizeX /= 2;
		sizeY -= sideWidth*2;
		sizeZ -= sideWidth*2;
		
		for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
		{
			double originX = 0D, originY = 0D, originZ = 0D;
			float rotateAngleX = 0f, rotateAngleY = 0f, rotateAngleZ = 0f;
			
			if (side.offsetX > 0)
				originX = sideLength;
			else if (side.offsetY > 0)
				originY = sideLength;
			else if (side.offsetZ > 0)
				originZ = sideLength;
			
			if (side == ForgeDirection.DOWN)
			{
				rotateAngleZ = -1.570796f;
				//originY = sideWidth;
				//originX = sideWidth;
			}

			if (side == ForgeDirection.WEST)
			{
				rotateAngleY = 1.570796f*2;
				//originX = sideLength;
				//originX = sideLength;
			}

			if (side == ForgeDirection.NORTH)
			{
				rotateAngleY = 1.570796f;
				//originX = sideWidth - sideWidth/4;
				//originZ = sideLength + sideWidth - sideWidth/4;
			}

			if (side == ForgeDirection.SOUTH)
			{
				rotateAngleY = 1.570796f*3;
				//originX = sideLength - sideWidth + sideWidth/4;
				//originZ = -sideWidth + sideWidth/4;
			}
			
			ModelRenderer sideModel = new ModelRenderer(this, 0, 0).setTextureSize(32, 32);
			sideModel.addBox((float) originX, (float) originY, (float) originZ, sizeX, sizeY, sizeZ);
			sideModel.setRotationPoint(2f, 2f, 2f);
			sideModel.rotateAngleX = rotateAngleX;
			sideModel.rotateAngleY = rotateAngleY;
			sideModel.rotateAngleZ = rotateAngleZ;
			
			sides[side.ordinal()] = sideModel;
		}
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
		int i = 0;
		for (ModelRenderer sidePart : sides)
		{
			ForgeDirection side = ForgeDirection.getOrientation(i);
			if (sidePart != null)
			{
				sidePart.render(scale);
			}
			i++;
		}
		if (sideBox != null)
			sideBox.render(scale);
	}
	
	@Override
	public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
	{
		super.render(par1Entity, par2, par3, par4, par5, par6, par7);
	}
}
