package makmods.levelstorage.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ConductorModel extends ModelBase {
	// fields
	ModelRenderer BasicCube;
	
	public ConductorModel() {
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.BasicCube = new ModelRenderer(this, 0, 0);
		this.BasicCube.addBox(0F, 0F, 0F, 16, 6, 16);
		this.BasicCube.setRotationPoint(-8F, 18F, -8F);
		this.BasicCube.setTextureSize(64, 32);
		this.BasicCube.mirror = true;
		this.setRotation(this.BasicCube, 0F, 0F, 0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3,
			float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		this.BasicCube.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float f, float f1, float f2, float f3,
			float f4, float f5, Entity e) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
	}

}
