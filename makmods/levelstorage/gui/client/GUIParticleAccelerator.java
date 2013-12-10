package makmods.levelstorage.gui.client;

import ic2.api.item.Items;
import makmods.levelstorage.gui.container.ContainerParticleAccelerator;
import makmods.levelstorage.item.SimpleItems;
import makmods.levelstorage.logic.util.RenderHelper;
import makmods.levelstorage.network.packet.PacketPressButton;
import makmods.levelstorage.proxy.ClientProxy;
import makmods.levelstorage.tileentity.TileEntityParticleAccelerator;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

import org.lwjgl.opengl.GL11;

public class GUIParticleAccelerator extends GuiContainer {

	public TexturedButton modeButton;
	public TileEntityParticleAccelerator tileEntity;

	public GUIParticleAccelerator(InventoryPlayer inventory,
			TileEntityParticleAccelerator tile) {
		super(new ContainerParticleAccelerator(inventory, tile));
		tileEntity = tile;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		// draw text and stuff here
		// the parameters for drawString are: string, x, y, color
		this.fontRenderer.drawString("Particle Accelerator", 8, 6, 4210752);
		int y = 64;
		int x = 12;
	}

	public static ItemStack MATTER_DISPLAY = Items.getItem("matter");

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2,
			int par3) {
		// draw your Gui here, only thing you need to change is the path
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.bindTexture(ClientProxy.GUI_PARTICLE_ACCELERATOR);
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
		switch (tileEntity.mode) {
		case TileEntityParticleAccelerator.ANTIMATTER_PRODUCTION_MODE:
			modeButton.itemStack = SimpleItems.instance.getIngredient(9);
			break;
		case TileEntityParticleAccelerator.MATTER_RESHAPING_MODE:
			modeButton.itemStack = MATTER_DISPLAY;
			break;
		}
		RenderHelper.renderTank(this.tileEntity.getFluidTank(), x, y, 152, 9, tileEntity.gaugeLiquidScaled(60));
		RenderHelper.bindTexture(ClientProxy.GUI_PARTICLE_ACCELERATOR);
		int l = tileEntity.gaugeEnergyScaled(14);
		if (l > 0)
			drawTexturedModalRect(x + 82, y + 63 + 14 - l, 176, 152 + 14 - l,
					14, l);
		int p = tileEntity.gaugeProgressScaled(53);
		if (p > 0)
			drawTexturedModalRect(x + 64, y + 36, 177, 114, p, 11);
	}

	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		PacketPressButton.handleButtonClick(par1GuiButton.id, tileEntity);
	}

	@Override
	public void initGui() {
		super.initGui();
		int xGuiPos = (this.width - this.xSize) / 2; // j
		int yGuiPos = (this.height - this.ySize) / 2;
		modeButton = new TexturedButton(1, xGuiPos + 7, yGuiPos + 59,
				SimpleItems.instance.getIngredient(9).copy(), true);
		this.buttonList.add(modeButton);
	}
}
