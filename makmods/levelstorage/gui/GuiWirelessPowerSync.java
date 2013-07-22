package makmods.levelstorage.gui;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import makmods.levelstorage.proxy.ClientProxy;
import makmods.levelstorage.registry.ConductorType;
import makmods.levelstorage.tileentity.TileEntityWirelessPowerSynchronizer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;

public class GuiWirelessPowerSync extends GuiContainer {

	protected TileEntityWirelessPowerSynchronizer tileEntity;

	public GuiWirelessPowerSync(InventoryPlayer inventoryPlayer,
			TileEntityWirelessPowerSynchronizer te) {
		super(new ContainerPowerSync(inventoryPlayer, te));
		this.tileEntity = te;
	}

	public static final String defaultInputFieldText = "0";

	public GuiTextField freqTextBox;

	@Override
	public void initGui() {
		super.initGui();
		int xGuiPos = (width - xSize) / 2; // j
		int yGuiPos = (height - ySize) / 2;
		freqTextBox = new GuiTextField(this.fontRenderer, xGuiPos + 50,
				yGuiPos + 15, 75, 15);
		Keyboard.enableRepeatEvents(true);
		this.freqTextBox.setMaxStringLength(4);
		this.freqTextBox.setEnableBackgroundDrawing(false);
		this.freqTextBox.setVisible(true);
		this.freqTextBox.setFocused(true);
		this.freqTextBox.setEnableBackgroundDrawing(true);
		this.freqTextBox.setText(this.defaultInputFieldText);
		this.freqTextBox.setCanLoseFocus(false);
	}

	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}
	
	//protected void mouseClicked(int par1, int par2, int par3) {
	//	Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(par1 + " " + par2 + " " + par3);
	//}

	protected void keyTyped(char par1, int par2) {
		if (this.freqTextBox.isFocused()) {
			this.freqTextBox.textboxKeyTyped(par1, par2);
		}
		super.keyTyped(par1, par2);
	}

	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
		int xGuiPos = (width - xSize) / 2; // j
		int yGuiPos = (height - ySize) / 2;
		//drawRect(169, 49, 200, 60,
		//Integer.MIN_VALUE);
		this.freqTextBox.drawTextBox();
		
	}

	public void updateScreen() {
		this.freqTextBox.updateCursorCounter();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		// draw text and stuff here
		// the parameters for drawString are: string, x, y, color
		// this.fontRenderer.drawString("Wireless Conductor", 8, 6, 4210752);
		// draws "Inventory" or your regional equivalent
		int xGuiPos = (width - xSize) / 2; // j
		int yGuiPos = (height - ySize) / 2;
		this.fontRenderer.drawString(
				StatCollector.translateToLocal("container.inventory"), 8,
				this.ySize - 96 + 2, 4210752);
		// String mode = "Mode: " + (tileEntity.type == ConductorType.SOURCE ?
		// "Energy transmitter" : "Energy receiver");
		// this.fontRenderer.drawString(mode, 8, 55, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2,
			int par3) {
		// draw your Gui here, only thing you need to change is the path
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		// this.mc.renderEngine.bindTexture(ClientProxy.GUI_SINGLE_SLOT);
		this.mc.func_110434_K().func_110577_a(ClientProxy.GUI_NO_SLOTS);
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
	}

}
