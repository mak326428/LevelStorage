package makmods.levelstorage.client.render;

import makmods.levelstorage.logic.util.RenderHelper;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

public class ItemRendererAtomicDisassembler implements IItemRenderer {

	public ModelAtomicDisassembler model = new ModelAtomicDisassembler();
	public int ticker = 0;
	int mode = 0;

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
		ticker++;
		if (ticker >= 360)
			ticker = -360;
		ticker++;
		switch (type) {
		case ENTITY: {
			this.renderAtomicDisassembler(-0.5F, -1.2F, 0.5F, 1.4F);
			return;
		}
		case EQUIPPED: {
			// GL11.glRotatef(-45F, 0F, 0F, 1F);
			// GL11.glRotatef(-45F, 0F, 1F, 0F);

			// renderAtomicDisassembler(1F, 0.375F, 2F, 1.4F);
			// GL11.glRotatef(-45F, 1F, 0F, 0F);
			// GL11.glRotatef(-45F, 0F, 1F, 0F);
			// GL11.glRotatef(-45F, 0F, 0F, 1F);
			this.renderAtomicDisassembler(0F, 0F, 1.375F, 1.4F);
			return;
		}
		case EQUIPPED_FIRST_PERSON: {
			// GL11.glRotatef(-45F, 1F, 0F, 0F);
			// GL11.glRotatef(-45F, 0F, 1F, 0F);
			// GL11.glRotatef(-45F, 0F, 0F, 1F);
			//GL11.glTranslatef(0.0F, 0.0F, -16.0F);
			//GL11.glRotatef(ticker, 0.0F, 0.0F, 1.0F);
			//GL11.glRotatef(ticker, 0.0F, 1.0F, 0.0F);
			//GL11.glRotatef(ticker, 1.0F, 0.0F, 0.0F);
			
			this.renderAtomicDisassembler(1F, 1F, 1F, 2.4F);
			return;
		}
		case INVENTORY: {
			// renderAtomicDisassembler(-1.0F, -1.675F, 0.0F, 1.4F);
			this.renderAtomicDisassembler(-1.375F, -1.125F, 0F, 1F);
			return;
		}
		default:
			return;
		}

	}

	private void renderAtomicDisassembler(float x, float y, float z, float scale) {

		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);

		// Scale, Translate, Rotate
		GL11.glScalef(scale, scale, scale);
		GL11.glTranslatef(x, y, z);
		GL11.glRotatef(-90F, 1F, 0, 0);

		// Bind texture
		RenderHelper.bindTexture(ClientProxy.MODEL_ATOMIC_DISASSEMBLER);

		// Render
		this.model
				.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}

}
