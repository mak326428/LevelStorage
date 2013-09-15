package makmods.levelstorage.gui;

import makmods.levelstorage.logic.util.RenderHelper;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

public class GuiWirelessCharger extends GuiContainer {

	public GuiWirelessCharger(EntityPlayer ep) {
		// super(new ContainerWirelessCharger(ep));
		super(null);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.bindNoSlotsGUI();
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
	}

}
