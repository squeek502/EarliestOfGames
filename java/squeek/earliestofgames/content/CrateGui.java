package squeek.earliestofgames.content;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import squeek.earliestofgames.ModInfo;

public class CrateGui extends GuiContainer
{
	public static final ResourceLocation textureLocation = new ResourceLocation(ModInfo.MODID, "textures/gui/crate.png");
	protected IInventory playerInventory = null;
	protected IInventory inventory = null;

	public CrateGui(InventoryPlayer playerInventory, CrateTile crate)
	{
		super(new CrateContainer(playerInventory, crate));
		this.inventory = crate;
		this.playerInventory = playerInventory;
	}

	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		this.fontRendererObj.drawString(this.inventory.hasCustomInventoryName() ? this.inventory.getInventoryName() : I18n.format(this.inventory.getInventoryName()), 8, 6, 4210752);
		this.fontRendererObj.drawString(this.playerInventory.hasCustomInventoryName() ? this.playerInventory.getInventoryName() : I18n.format(this.playerInventory.getInventoryName()), 8, this.ySize - 96 + 2, 4210752);
	}

	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(textureLocation); //new ResourceLocation("test", "test.png"));
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
	}

}
