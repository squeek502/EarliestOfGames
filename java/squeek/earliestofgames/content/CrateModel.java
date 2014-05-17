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
		// four corner pillars
		frame[0] = new ModelRenderer(this, 0, 0);
		frame[0].addBox(0F, 0F, 0F, 2, 16, 2);
		frame[0].setRotationPoint(0F, 0F, 0F);
		frame[0].setTextureSize(64, 32);

		frame[1] = new ModelRenderer(this, 0, 0);
		frame[1].addBox(0F, 0F, 0F, 2, 16, 2);
		frame[1].setRotationPoint(0F, 0F, 16F);
		frame[1].setTextureSize(64, 32);
		frame[1].rotateAngleY = (float) Math.toRadians(90D);

		frame[2] = new ModelRenderer(this, 0, 0);
		frame[2].addBox(0F, 0F, 0F, 2, 16, 2);
		frame[2].setRotationPoint(16F, 0F, 16F);
		frame[2].setTextureSize(64, 32);
		frame[2].rotateAngleY = (float) Math.toRadians(180D);

		frame[3] = new ModelRenderer(this, 0, 0);
		frame[3].addBox(0F, 0F, 0F, 2, 16, 2);
		frame[3].setRotationPoint(16F, 0F, 0F);
		frame[3].setTextureSize(64, 32);
		frame[3].rotateAngleY = (float) Math.toRadians(270D);
		
		// support beams
		frame[4] = new ModelRenderer(this, 2, 0);
		frame[4].addBox(0F, 0F, 0F, 12, 2, 2);
		frame[4].setRotationPoint(2F, 14F, 0F);
		frame[4].setTextureSize(64, 32);
		
		frame[5] = new ModelRenderer(this, 2, 0);
		frame[5].addBox(0F, 0F, 0F, 12, 2, 2);
		frame[5].setRotationPoint(2F, 0F, 0F);
		frame[5].setTextureSize(64, 32);
		
		frame[6] = new ModelRenderer(this, 2, 0);
		frame[6].addBox(0F, 0F, 0F, 12, 2, 2);
		frame[6].setRotationPoint(0F, 0F, 14F);
		frame[6].setTextureSize(64, 32);
		frame[6].rotateAngleY = (float) Math.toRadians(90D);
		
		frame[7] = new ModelRenderer(this, 2, 0);
		frame[7].addBox(0F, 0F, 0F, 12, 2, 2);
		frame[7].setRotationPoint(0F, 14F, 14F);
		frame[7].setTextureSize(64, 32);
		frame[7].rotateAngleY = (float) Math.toRadians(90D);
		
		frame[8] = new ModelRenderer(this, 2, 0);
		frame[8].addBox(0F, 0F, 0F, 12, 2, 2);
		frame[8].setRotationPoint(14F, 0F, 16F);
		frame[8].setTextureSize(64, 32);
		frame[8].rotateAngleY = (float) Math.toRadians(180D);
		
		frame[9] = new ModelRenderer(this, 2, 0);
		frame[9].addBox(0F, 0F, 0F, 12, 2, 2);
		frame[9].setRotationPoint(14F, 14F, 16F);
		frame[9].setTextureSize(64, 32);
		frame[9].rotateAngleY = (float) Math.toRadians(180D);
		
		frame[10] = new ModelRenderer(this, 2, 0);
		frame[10].addBox(0F, 0F, 0F, 12, 2, 2);
		frame[10].setRotationPoint(16F, 14F, 2F);
		frame[10].setTextureSize(64, 32);
		frame[10].rotateAngleY = (float) Math.toRadians(270D);
		
		frame[11] = new ModelRenderer(this, 2, 0);
		frame[11].addBox(0F, 0F, 0F, 12, 2, 2);
		frame[11].setRotationPoint(16F, 0F, 2F);
		frame[11].setTextureSize(64, 32);
		frame[11].rotateAngleY = (float) Math.toRadians(270D);
		
		/*
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
			{
				originX = sideLength - sideWidth*2;
				originZ = -sideLength + sideWidth*2;
			}
			
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
			
			originX -= side.offsetX * ((float) sideWidth/4);
			originY -= side.offsetY * ((float) sideWidth/4);
			originZ -= side.offsetZ * ((float) sideWidth/4);
			
			ModelRenderer sideModel = new ModelRenderer(this, 0, 0).setTextureSize(32, 32);
			sideModel.addBox((float) originX, (float) originY, (float) originZ, sizeX, sizeY, sizeZ);
			sideModel.setRotationPoint(2f, 2f, 2f);
			sideModel.rotateAngleX = rotateAngleX;
			sideModel.rotateAngleY = rotateAngleY;
			sideModel.rotateAngleZ = rotateAngleZ;
			
			sides[side.ordinal()] = sideModel;
		}
		*/
	}
	
	public void renderFrame()
	{
		for (ModelRenderer framePart : frame)
		{
			if (framePart != null)
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
