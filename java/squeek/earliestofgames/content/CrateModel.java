package squeek.earliestofgames.content;

import squeek.earliestofgames.ModContent;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
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
			ModContent.blockCrate.getSideBoundingBox(side);
			
			for (int pillarNum = 0; pillarNum < 2; pillarNum++)
			{
				frame.addBox(0f, 0f, 16f * pillarNum, sideWidth, sideLength, sideWidth);
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
