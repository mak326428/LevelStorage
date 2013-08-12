package makmods.levelstorage.gui;

import makmods.levelstorage.network.PacketPressButton;
import makmods.levelstorage.network.PacketRecipeSelection;
import makmods.levelstorage.network.PacketTypeHandler;
import makmods.levelstorage.proxy.ClientProxy;
import makmods.levelstorage.tileentity.TileEntityMassInfuser;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.network.PacketDispatcher;

public class GuiMassInfuser extends GuiContainer {

	public TileEntityMassInfuser tileEntity;

	public GuiMassInfuser(InventoryPlayer inventoryPlayer,
			TileEntityMassInfuser tileEntity) {
		super(new ContainerMassInfuser(inventoryPlayer, tileEntity));
		this.tileEntity = tileEntity;
		xSize = 200;
		ySize = 255;
	}

	// 3 rows of player inv + 1 hotbar

	protected void handleMouseClick(Slot par1Slot, int par2, int par3, int par4) {
		super.handleMouseClick(par1Slot, par2, par3, par4);
		if (par1Slot != null) {
			ItemStack stack = par1Slot.getStack();
			if (stack != null) {
				int slot = par2;
				PacketRecipeSelection packet = new PacketRecipeSelection();
				packet.dimId = this.tileEntity.worldObj.provider.dimensionId;
				packet.x = this.tileEntity.xCoord;
				packet.y = this.tileEntity.yCoord;
				packet.z = this.tileEntity.zCoord;
				packet.newSlotId = slot;
				PacketDispatcher.sendPacketToServer(PacketTypeHandler
						.populatePacket(packet));
			}
		}
	}

	@Override
	public void initGui() {
		super.initGui();
		int xGuiPos = (this.width - this.xSize) / 2; // j
		int yGuiPos = (this.height - this.ySize) / 2;
		this.buttonList.add(new GuiButton(1, xGuiPos + 9, yGuiPos + 18, 6, 18,
				"<"));
		this.buttonList.add(new GuiButton(2, xGuiPos + 182, yGuiPos + 18, 6,
				18, ">"));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		// draw text and stuff here
		// the parameters for drawString are: string, x, y, color
		this.fontRenderer.drawString("Mass Infuser", 8, 6, 4210752);
		// draws "Inventory" or your regional equivalent
		this.fontRenderer.drawString(
				StatCollector.translateToLocal("container.inventory"), 8,
				this.ySize - 96 + 2, 4210752);
		this.fontRenderer.drawString("Page " + (tileEntity.page + 1)
				+ " out of " + (TileEntityMassInfuser.MAX_PAGE + 1), 60, 40,
				4210752);
		// GuiSlider
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
	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
		// slider.drawButton(par1Minecraft, par2, par3);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2,
			int par3) {
		// GuiSlider
		// draw your Gui here, only thing you need to change is the path
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.func_110434_K().func_110577_a(ClientProxy.GUI_MASS_INFUSER);
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
	}
}
