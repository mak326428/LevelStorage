package makmods.levelstorage.gui;

import makmods.levelstorage.logic.util.RenderHelper;
import makmods.levelstorage.network.PacketPressButton;
import makmods.levelstorage.network.PacketTextChanged;
import makmods.levelstorage.network.PacketTypeHandler;
import makmods.levelstorage.registry.SyncType;
import makmods.levelstorage.tileentity.TileEntityWirelessPowerSynchronizer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.network.PacketDispatcher;

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
		int xGuiPos = (this.width - this.xSize) / 2; // j
		int yGuiPos = (this.height - this.ySize) / 2;
		this.buttonList.add(new GuiButton(1, xGuiPos + 50, yGuiPos + 35, 75,
		        15, "Change mode"));
		this.freqTextBox = new GuiTextField(this.fontRenderer, xGuiPos + 50,
		        yGuiPos + 15, 75, 15);
		Keyboard.enableRepeatEvents(true);
		this.freqTextBox.setMaxStringLength(4);
		this.freqTextBox.setEnableBackgroundDrawing(false);
		this.freqTextBox.setVisible(true);
		this.freqTextBox.setFocused(true);
		this.freqTextBox.setEnableBackgroundDrawing(true);
		this.freqTextBox.setText(String.valueOf(this.tileEntity.frequency));
		this.freqTextBox.setCanLoseFocus(false);

	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	// protected void mouseClicked(int par1, int par2, int par3) {
	// Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(par1 +
	// " " + par2 + " " + par3);
	// }

	public boolean isCharNumber(char ch) {
		return (ch == '0' || ch == '1' || ch == '2' || ch == '3' || ch == '4'
		        || ch == '5' || ch == '6' || ch == '7' || ch == '8' || ch == '9');
	}

	@Override
	protected void keyTyped(char par1, int par2) {
		if (this.freqTextBox.isFocused()) {
			// 8 = backspace
			// 46 = delete
			if (this.isCharNumber(par1) || par1 == 8 || par1 == 46) {
				this.freqTextBox.textboxKeyTyped(par1, par2);
				if (this.freqTextBox.getText() == "") {
					this.freqTextBox.setText(defaultInputFieldText);
				}
				PacketTextChanged packetTC = new PacketTextChanged();
				packetTC.dimId = this.tileEntity.worldObj.provider.dimensionId;
				packetTC.x = this.tileEntity.xCoord;
				packetTC.y = this.tileEntity.yCoord;
				packetTC.z = this.tileEntity.zCoord;
				packetTC.textBoxId = 0;
				packetTC.newText = this.freqTextBox.getText();
				PacketDispatcher.sendPacketToServer(PacketTypeHandler
				        .populatePacket(packetTC));
			}
		}
		super.keyTyped(par1, par2);
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
		int xGuiPos = (this.width - this.xSize) / 2; // j
		int yGuiPos = (this.height - this.ySize) / 2;
		// drawRect(169, 49, 200, 60,
		// Integer.MIN_VALUE);
		this.freqTextBox.drawTextBox();

	}

	@Override
	public void updateScreen() {
		this.freqTextBox.updateCursorCounter();
	}

	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		PacketPressButton packet = new PacketPressButton();
		packet.buttonId = par1GuiButton.id;
		packet.x = this.tileEntity.xCoord;
		packet.y = this.tileEntity.yCoord;
		packet.z = this.tileEntity.zCoord;
		packet.dimId = this.tileEntity.worldObj.provider.dimensionId;
		PacketDispatcher.sendPacketToServer(PacketTypeHandler
		        .populatePacket(packet));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		// draw text and stuff here
		// the parameters for drawString are: string, x, y, color
		// this.fontRenderer.drawString("Wireless Conductor", 8, 6, 4210752);
		// draws "Inventory" or your regional equivalent
		int xGuiPos = (this.width - this.xSize) / 2; // j
		int yGuiPos = (this.height - this.ySize) / 2;
		this.fontRenderer.drawString(
		        StatCollector.translateToLocal("container.inventory"), 8,
		        this.ySize - 96 + 2, 4210752);
		this.fontRenderer.drawString("Frequency: "
		        + this.tileEntity.frequency
		        + "; "
		        + "Mode: "
		        + (this.tileEntity.deviceType == SyncType.RECEIVER ? "receiver"
		                : "sender"), 8, 55, 4210752);
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
		RenderHelper.bindNoSlotsGUI();
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
	}

}
