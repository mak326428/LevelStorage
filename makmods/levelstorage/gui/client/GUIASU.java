package makmods.levelstorage.gui.client;

import makmods.levelstorage.gui.container.ContainerASU;
import makmods.levelstorage.logic.util.RenderHelper;
import makmods.levelstorage.proxy.ClientProxy;
import makmods.levelstorage.tileentity.TileEntityASU;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

public class GUIASU extends GuiContainer {

	public TileEntityASU tileEntity;
	public String name = "ASU";
	public String armorInv = "Armor";

	public GUIASU(InventoryPlayer inventoryPlayer, TileEntityASU tileEntity) {
		super(new ContainerASU(inventoryPlayer, tileEntity));
		this.ySize = 196;
		this.tileEntity = tileEntity;
	}

	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		this.fontRenderer.drawString(this.name,
				(this.xSize - this.fontRenderer.getStringWidth(this.name)) / 2,
				6, 4210752);
		this.fontRenderer.drawString(this.armorInv, 8, this.ySize - 126 + 3,
				4210752);
		this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 3, 4210752);

		this.fontRenderer.drawString("Power Level: ", 79, 25, 4210752);
		int e = (int) Math.min(this.tileEntity.stored,
				TileEntityASU.EU_STORAGE);
		this.fontRenderer.drawString(" " + e, 110, 35, 4210752);
		this.fontRenderer.drawString(
				"/" + TileEntityASU.EU_STORAGE, 110, 45, 4210752);

		String output = "Out: " + TileEntityASU.EU_PER_TICK + " EU/t";
		this.fontRenderer.drawString(output, 85, 60, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2,
			int par3) {
		// draw your Gui here, only thing you need to change is the path
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		// this.mc.renderEngine.bindTexture(ClientProxy.GUI_SINGLE_SLOT);
		RenderHelper.bindTexture(ClientProxy.GUI_ELECTRIC_BLOCK);

		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

		int xOffset = x;
		int yOffset = y;

		if (this.tileEntity.stored > 0.0D) {
			int i1 = (int) (24.0F * this.tileEntity.getChargeLevel());
			drawTexturedModalRect(x + 79, y + 34, 176, 14, i1 + 1, 16);
		}
	}
}
