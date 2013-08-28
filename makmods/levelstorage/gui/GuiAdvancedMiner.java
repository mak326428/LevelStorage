package makmods.levelstorage.gui;

import makmods.levelstorage.proxy.ClientProxy;
import makmods.levelstorage.tileentity.TileEntityAdvancedMiner;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

public class GuiAdvancedMiner extends GuiContainer {

	public TileEntityAdvancedMiner tileEntity;

	public GuiAdvancedMiner(InventoryPlayer inventoryPlayer,
	        TileEntityAdvancedMiner tileEntity) {
		super(new ContainerAdvancedMiner(inventoryPlayer, tileEntity));
		this.tileEntity = tileEntity;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		// draw text and stuff here
		// the parameters for drawString are: string, x, y, color
		this.fontRenderer.drawString("Advanced Miner", 8, 6, 4210752);
		// draws "Inventory" or your regional equivalent
		int xGuiPos = (this.width - this.xSize) / 2; // j
		int yGuiPos = (this.height - this.ySize) / 2;
		this.fontRenderer.drawString(
		        StatCollector.translateToLocal("container.inventory"), 8,
		        this.ySize - 96 + 2, 4210752);

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2,
	        int par3) {
		// draw your Gui here, only thing you need to change is the path
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		// this.mc.renderEngine.bindTexture(ClientProxy.GUI_SINGLE_SLOT);
		this.mc.func_110434_K().func_110577_a(ClientProxy.GUI_MINER);
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
		ContainerAdvancedMiner container = (ContainerAdvancedMiner) this.inventorySlots;
		if (container.energy > 0) {
			int l = container.gaugeEnergyScaled(14);
			drawTexturedModalRect(x + 56, y + 36 + 14 - l, 176, 14 - l, 14, l);
		}
	}
}
