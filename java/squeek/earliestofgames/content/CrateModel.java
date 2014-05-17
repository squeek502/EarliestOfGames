package squeek.earliestofgames.content;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.common.util.ForgeDirection;

public class CrateModel extends ModelBase
{
	private ModelRenderer[] frame = new ModelRenderer[ForgeDirection.VALID_DIRECTIONS.length*2];
	private float scale = 0.0625f;
	
	public CrateModel()
	{
		
	}
}
