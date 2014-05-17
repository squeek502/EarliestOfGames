package squeek.earliestofgames.content;

import squeek.earliestofgames.ModContent;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

public class CrateModel extends ModelBase
{
	private ModelRenderer frame = new ModelRenderer(this, 0, 0);
	private float scale = 0.0625f;
	
	public CrateModel()
	{
		int sideWidth = (int) (ModContent.blockCrate.sideWidth / scale);
		int sideLength = (int) (1f / scale);
		
		for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
		{
			if (side == ForgeDirection.UP || side == ForgeDirection.DOWN)
				continue;
			
			// temp
			if (side == ForgeDirection.NORTH || side == ForgeDirection.SOUTH)
				continue;
			
			AxisAlignedBB sideBounds = ModContent.blockCrate.getSideBoundingBox(side);
			int numPillars = side.offsetX != 0 ? 4 : 2;
			
			for (int pillarNum = 0; pillarNum < numPillars; pillarNum++)
			{
				double originX = sideBounds.minX / scale, originY = sideBounds.minY / scale, originZ = sideBounds.minZ / scale;
				int sizeX = (int) ((sideBounds.maxX-sideBounds.minX) / scale);
				int sizeY = (int) ((sideBounds.maxY-sideBounds.minY) / scale);
				int sizeZ = (int) ((sideBounds.maxY-sideBounds.minY) / scale);
				
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
						int oldSizeY = sizeY;
						sizeY = sizeX;
						//sizeX = oldSizeY;
					}
				}
				if (side.offsetY != 0)
					originX = originX + pillarNum * (sideLength - sideWidth);
				if (side.offsetZ != 0)
					originY = originY + pillarNum * (sideLength - sideWidth);
				
				frame.addBox((float) originX, (float) originY, (float) originZ, sizeX, sizeY, sizeZ);
			}
		}
	}
	
	public void renderAll()
	{
		frame.render(scale);
	}
	
	@Override
	public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
	{
		super.render(par1Entity, par2, par3, par4, par5, par6, par7);
	}
}
