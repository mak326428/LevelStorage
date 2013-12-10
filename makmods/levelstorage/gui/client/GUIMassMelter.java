package makmods.levelstorage.gui.client;

import makmods.levelstorage.gui.container.ContainerMassMelter;
import makmods.levelstorage.logic.util.RenderHelper;
import makmods.levelstorage.proxy.ClientProxy;
import makmods.levelstorage.tileentity.TileEntityMassMelter;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.Icon;

import org.lwjgl.opengl.GL11;

/**
 * TODO: add a fancier processing GUI
 * 
 * @author Maxim
 * 
 */
public class GUIMassMelter extends GuiContainer {

	public TileEntityMassMelter tileEntity;

	public GUIMassMelter(InventoryPlayer inventoryPlayer,
			TileEntityMassMelter tileEntity) {
		super(new ContainerMassMelter(inventoryPlayer, tileEntity));
		this.tileEntity = tileEntity;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		// draw text and stuff here
		// the parameters for drawString are: string, x, y, color
		// this.fontRenderer.drawString("Rock Desintegrator", 8, 6, 4210752);
		// draws "Inventory" or your regional equivalent
		// this.fontRenderer.drawString(
		// StatCollector.translateToLocal("container.inventory"), 8,
		// this.ySize - 96 + 2, 4210752);
	}

	// p max = ~36
	// p min = 1

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2,
			int par3) {
		// draw your Gui here, only thing you need to change is the path
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		// this.mc.renderEngine.bindTexture(ClientProxy.GUI_SINGLE_SLOT);
		RenderHelper.bindTexture(ClientProxy.GUI_MASS_MELTER);
		// TODO: add progress
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

		int xOffset = x;
		int yOffset = y;
		RenderHelper.renderTank(this.tileEntity.getFluidTank(), xOffset, yOffset, 152, 9);
		RenderHelper.bindTexture(ClientProxy.GUI_MASS_MELTER);

		int l = tileEntity.gaugeEnergyScaled(14);
		if (l > 0)
			drawTexturedModalRect(x + 82, y + 63 + 14 - l, 176, 152 + 14 - l,
					14, l);

		int j = tileEntity.gaugeProgressScaled(53);
		// System.out.println(String.format(
		// "Progress is %d; gauged (53) is %d, max progress is %d",
		// tileEntity.getProgress(), j, tileEntity.getMaxProgress()));
		if (j > 0)
			drawTexturedModalRect(x + 48, y + 9 + 53 - j, 177, 88 + 53 - j, 10,
					j);
	}
}
