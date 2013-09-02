package makmods.levelstorage.render;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class TestItemRenderer implements IItemRenderer {

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
	        ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		Tessellator draw = Tessellator.instance;
		//draw.draw();
		//draw.setColorOpaque(99, 99, 99);
		glPushMatrix();
		draw.startDrawingQuads();
		draw.setColorRGBA(99, 92, 99, 255);
		draw.addVertex(0, 0, 1);
		draw.setColorRGBA(99, 92, 99, 255);
		draw.addVertex(0, 1, 2);
		draw.setColorRGBA(99, 92, 99, 255);
		draw.addVertex(1, 1, 3);
		draw.setColorRGBA(99, 92, 99, 255);
		draw.addVertex(0, 1, 4);
		draw.draw();
		glPopMatrix();
	}

}
