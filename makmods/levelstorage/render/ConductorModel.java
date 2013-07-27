package makmods.levelstorage.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ConductorModel extends ModelBase {
	// fields
	ModelRenderer BasicCube;

	public ConductorModel() {
		textureWidth = 64;
		textureHeight = 32;
		BasicCube = new ModelRenderer(this, 0, 0);
		BasicCube.addBox(0F, 0F, 0F, 16, 6, 16);
		BasicCube.setRotationPoint(-8F, 18F, -8F);
		BasicCube.setTextureSize(64, 32);
		BasicCube.mirror = true;
		setRotation(BasicCube, 0F, 0F, 0F);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3,
			float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		BasicCube.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(float f, float f1, float f2, float f3,
			float f4, float f5, Entity e) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
	}

}
