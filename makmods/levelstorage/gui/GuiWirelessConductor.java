package makmods.levelstorage.gui;

import makmods.levelstorage.packet.PacketPressButton;
import makmods.levelstorage.packet.PacketTypeHandler;
import makmods.levelstorage.proxy.ClientProxy;
import makmods.levelstorage.registry.ConductorType;
import makmods.levelstorage.tileentity.TileEntityWirelessConductor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.network.PacketDispatcher;

public class GuiWirelessConductor extends GuiContainer {

	public TileEntityWirelessConductor tileEntity;

	public GuiWirelessConductor(InventoryPlayer inventoryPlayer,
			TileEntityWirelessConductor tileEntity) {
		super(new ContainerWirelessConductor(inventoryPlayer, tileEntity));
		this.tileEntity = tileEntity;
	}

	@Override
	public void initGui() {
		super.initGui();
		int xGuiPos = (this.width - this.xSize) / 2; // j
		int yGuiPos = (this.height - this.ySize) / 2;
		this.buttonList.add(new GuiButton(1, xGuiPos + 50, yGuiPos + 15, 75,
				15, "Change mode"));
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
		String mode = "Mode: "
				+ (this.tileEntity.type == ConductorType.SOURCE ? "Energy transmitter"
						: "Energy receiver");
		this.fontRenderer.drawString(mode, 8, 55, 4210752);
	}

	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		// NetworkHelper.initiateClientTileEntityEvent(tileEntity,
		// par1GuiButton.id);
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
	protected void drawGuiContainerBackgroundLayer(float par1, int par2,
			int par3) {
		// draw your Gui here, only thing you need to change is the path
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		// this.mc.renderEngine.bindTexture(ClientProxy.GUI_SINGLE_SLOT);
		this.mc.func_110434_K().func_110577_a(ClientProxy.GUI_SINGLE_SLOT);
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
	}
}
