package squeek.earliestofgames.content;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.util.ForgeDirection;
import squeek.earliestofgames.filters.IFilter;
import squeek.earliestofgames.filters.SizeFilter;

public class CrateModel extends ModelBase
{
	private ModelRenderer[] frame = new ModelRenderer[ForgeDirection.VALID_DIRECTIONS.length * 2];
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

		sides[ForgeDirection.DOWN.ordinal()] = new ModelRenderer(this, 8, 4);
		sides[ForgeDirection.DOWN.ordinal()].addBox(-6F, 0F, -0.5F, 12, 12, 1);
		sides[ForgeDirection.DOWN.ordinal()].setRotationPoint(8F, 1F, 2F);
		sides[ForgeDirection.DOWN.ordinal()].setTextureSize(64, 32);
		sides[ForgeDirection.DOWN.ordinal()].rotateAngleX = (float) Math.toRadians(90D);

		sides[ForgeDirection.UP.ordinal()] = new ModelRenderer(this, 8, 4);
		sides[ForgeDirection.UP.ordinal()].addBox(-6F, 0F, -0.5F, 12, 12, 1);
		sides[ForgeDirection.UP.ordinal()].setRotationPoint(8F, 15F, 14F);
		sides[ForgeDirection.UP.ordinal()].setTextureSize(64, 32);
		sides[ForgeDirection.UP.ordinal()].rotateAngleX = (float) Math.toRadians(90D);
		sides[ForgeDirection.UP.ordinal()].rotateAngleY = (float) Math.toRadians(180D);

		sides[ForgeDirection.NORTH.ordinal()] = new ModelRenderer(this, 8, 4);
		sides[ForgeDirection.NORTH.ordinal()].addBox(-6F, 0F, -0.5F, 12, 12, 1);
		sides[ForgeDirection.NORTH.ordinal()].setRotationPoint(8F, 2F, 1F);
		sides[ForgeDirection.NORTH.ordinal()].setTextureSize(64, 32);

		sides[ForgeDirection.SOUTH.ordinal()] = new ModelRenderer(this, 8, 4);
		sides[ForgeDirection.SOUTH.ordinal()].addBox(-6F, 0F, -0.5F, 12, 12, 1);
		sides[ForgeDirection.SOUTH.ordinal()].setRotationPoint(8F, 2F, 15F);
		sides[ForgeDirection.SOUTH.ordinal()].setTextureSize(64, 32);
		sides[ForgeDirection.SOUTH.ordinal()].rotateAngleY = (float) Math.toRadians(180D);

		sides[ForgeDirection.WEST.ordinal()] = new ModelRenderer(this, 8, 4);
		sides[ForgeDirection.WEST.ordinal()].addBox(-6F, 0F, -0.5F, 12, 12, 1);
		sides[ForgeDirection.WEST.ordinal()].setRotationPoint(1F, 2F, 8F);
		sides[ForgeDirection.WEST.ordinal()].setTextureSize(64, 32);
		sides[ForgeDirection.WEST.ordinal()].rotateAngleY = (float) Math.toRadians(90D);

		sides[ForgeDirection.EAST.ordinal()] = new ModelRenderer(this, 8, 4);
		sides[ForgeDirection.EAST.ordinal()].addBox(-6F, 0F, -0.5F, 12, 12, 1);
		sides[ForgeDirection.EAST.ordinal()].setRotationPoint(15F, 2F, 8F);
		sides[ForgeDirection.EAST.ordinal()].setTextureSize(64, 32);
		sides[ForgeDirection.EAST.ordinal()].rotateAngleY = (float) Math.toRadians(270D);
	}

	public void renderFrame()
	{
		for (ModelRenderer framePart : frame)
		{
			if (framePart != null)
				framePart.render(scale);
		}
	}

	public void renderSides(CrateTile tile, TextureManager textureManager)
	{
		int i = 0;
		for (ModelRenderer sidePart : sides)
		{
			ForgeDirection side = ForgeDirection.getOrientation(i);
			if (sidePart != null)
			{
				IFilter filter = tile.filters[side.ordinal()];
				
				sidePart.setTextureOffset(8, 4);
				
				if (filter != null)
				{
					if (filter instanceof SizeFilter)
						sidePart.setTextureOffset(8, 17);
				}
				
				sidePart.render(scale);
			}
			i++;
		}
	}

	public void renderSides()
	{
		for (ModelRenderer sidePart : sides)
		{
			if (sidePart != null)
			{
				sidePart.render(scale);
			}
		}
	}

	@Override
	public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
	{
		super.render(par1Entity, par2, par3, par4, par5, par6, par7);
		renderFrame();
		renderSides();
	}
	
}
