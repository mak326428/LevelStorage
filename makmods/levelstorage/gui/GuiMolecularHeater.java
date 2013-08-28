package makmods.levelstorage.gui;

import makmods.levelstorage.proxy.ClientProxy;
import makmods.levelstorage.tileentity.TileEntityAdvancedMiner;
import makmods.levelstorage.tileentity.TileEntityMolecularHeater;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

public class GuiMolecularHeater extends GuiContainer {

	public TileEntityMolecularHeater tileEntity;

	public GuiMolecularHeater(InventoryPlayer inventoryPlayer,
	        TileEntityMolecularHeater tileEntity) {
		super(new ContainerMolecularHeater(inventoryPlayer, tileEntity));
		this.tileEntity = tileEntity;
		this.xSize = 190;
		this.ySize = 165;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		// draw text and stuff here
		// the parameters for drawString are: string, x, y, color
		this.fontRenderer.drawString("Molecular Heater", 8, 6, 4210752);
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
		this.mc.func_110434_K().func_110577_a(ClientProxy.GUI_MOLECULAR_HEATER);
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
		ContainerMolecularHeater container = (ContainerMolecularHeater) this.inventorySlots;
		// 76 36
		// 191 14
		if (container.energy > 0) {
			int l = container.tileEntity.gaugeEnergyScaled(14);
			drawTexturedModalRect(x + 56, y + 36 + 14 - l, 191, 14 - l, 14, l);
		}
		//if (container.progress > 0) {
			drawTexturedModalRect(x + 76, y + 36, 191, 14, 17, 24);
		//}
	}
}
