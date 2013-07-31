package makmods.levelstorage.render;

import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;

public class ItemWirelessConductorRender implements IItemRenderer {

	public ConductorModel model = new ConductorModel();

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
		switch (type) {
		case ENTITY: {
			this.renderConductor(-0.5F, -1.2F, 0.5F, 1.4F);
			return;
		}
		case EQUIPPED: {
			// GL11.glRotatef(-45F, 0F, 0F, 1F);
			// GL11.glRotatef(-45F, 0F, 1F, 0F);

			// renderConductor(1F, 0.375F, 2F, 1.4F);
			// GL11.glRotatef(-45F, 1F, 0F, 0F);
			// GL11.glRotatef(-45F, 0F, 1F, 0F);
			// GL11.glRotatef(-45F, 0F, 0F, 1F);

			this.renderConductor(0F, 0F, 1.375F, 1.4F);
			return;
		}
		case EQUIPPED_FIRST_PERSON: {
			GL11.glRotatef(-45F, 1F, 0F, 0F);
			// GL11.glRotatef(-45F, 0F, 1F, 0F);
			GL11.glRotatef(-45F, 0F, 0F, 1F);

			this.renderConductor(1F, 1F, 1F, 1.4F);
			return;
		}
		case INVENTORY: {
			// renderConductor(-1.0F, -1.675F, 0.0F, 1.4F);
			this.renderConductor(-1.375F, -1.125F, 0F, 1F);
			return;
		}
		default:
			return;
		}

	}

	private void renderConductor(float x, float y, float z, float scale) {

		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);

		// Scale, Translate, Rotate
		GL11.glScalef(scale, scale, scale);
		GL11.glTranslatef(x, y, z);
		GL11.glRotatef(-90F, 1F, 0, 0);

		// Bind texture
		FMLClientHandler.instance().getClient().renderEngine
				.func_110577_a(ClientProxy.CONDUCTOR_MODEL);

		// Render
		this.model
				.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}

}
