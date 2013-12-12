package makmods.levelstorage.gui.client;

import makmods.levelstorage.gui.container.ContainerIVGenerator;
import makmods.levelstorage.logic.util.RenderHelper;
import makmods.levelstorage.proxy.ClientProxy;
import makmods.levelstorage.tileentity.TileEntityIVGenerator;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

public class GUIIVGenerator extends GuiContainer {

	public TileEntityIVGenerator tileEntity;

	public GUIIVGenerator(InventoryPlayer inventoryPlayer,
			TileEntityIVGenerator tileEntity) {
		super(new ContainerIVGenerator(inventoryPlayer, tileEntity));
		this.tileEntity = tileEntity;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		// the parameters for drawString are: string, x, y, color
		// this.fontRenderer.drawString("IV Generator", 8, 6, 4210752);
		// draws "Inventory" or your regional equivalent
		this.fontRenderer.drawString(
				StatCollector.translateToLocal("container.inventory"), 8,
				this.ySize - 96 + 2, 4210752);
		this.fontRenderer.drawString("Speed: " + this.tileEntity.latestSpeed
				+ " IV/t", 30, 10, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2,
			int par3) {
		// draw your Gui here, only thing you need to change is the path
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		// this.mc.renderEngine.bindTexture(ClientProxy.GUI_SINGLE_SLOT);
		RenderHelper.bindTexture(ClientProxy.GUI_IV_GENERATOR);
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
		int xOffset = x;
		int yOffset = y;
		RenderHelper.renderTank(tileEntity.getFluidTank(), xOffset, yOffset,
				10, 10);
	}
}
