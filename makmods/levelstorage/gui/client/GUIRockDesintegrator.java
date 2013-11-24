package makmods.levelstorage.gui.client;

import makmods.levelstorage.gui.container.ContainerRockDesintegrator;
import makmods.levelstorage.logic.util.RenderHelper;
import makmods.levelstorage.proxy.ClientProxy;
import makmods.levelstorage.tileentity.TileEntityRockDesintegrator;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

public class GUIRockDesintegrator extends GuiContainer {

	public TileEntityRockDesintegrator tileEntity;

	public GUIRockDesintegrator(InventoryPlayer inventoryPlayer,
			TileEntityRockDesintegrator tileEntity) {
		super(new ContainerRockDesintegrator(inventoryPlayer, tileEntity));
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
		RenderHelper.bindTexture(ClientProxy.GUI_ROCK_DESINTEGRATOR);

		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
	}
}
