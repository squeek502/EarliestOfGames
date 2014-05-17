package squeek.earliestofgames.content;

import squeek.earliestofgames.ModContent;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.util.ForgeDirection;

public class CrateModel extends ModelBase
{
	private ModelRenderer[] frame = new ModelRenderer[ForgeDirection.VALID_DIRECTIONS.length*2];
	private float scale = 0.0625f;
	
	public CrateModel()
	{
		for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
		{
			ModContent.blockCrate.getSideBoundingBox(side);
		}
	}
	
	@Override
	public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
	{
		super.render(par1Entity, par2, par3, par4, par5, par6, par7);
	}
}
