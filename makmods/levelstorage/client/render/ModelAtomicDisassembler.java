package makmods.levelstorage.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelAtomicDisassembler extends ModelBase {
	// fields
	ModelRenderer Handle;
	ModelRenderer Biggun;
	ModelRenderer Stickpart1;
	ModelRenderer Stickpart2;
	ModelRenderer Shape1;
	ModelRenderer Shape2;
	ModelRenderer Shape3;
	ModelRenderer Shape4;
	ModelRenderer Shape5;
	ModelRenderer Shape6;

	public ModelAtomicDisassembler() {
		textureWidth = 64;
		textureHeight = 32;

		Handle = new ModelRenderer(this, 0, 15);
		Handle.addBox(0F, 0F, 0F, 2, 3, 2);
		Handle.setRotationPoint(-0.5F, 20F, 4.5F);
		Handle.setTextureSize(64, 32);
		Handle.mirror = true;
		setRotation(Handle, -0.6981317F, 0F, 0F);
		Biggun = new ModelRenderer(this, 0, 0);
		Biggun.addBox(0F, 0F, 0F, 5, 4, 10);
		Biggun.setRotationPoint(-2F, 17F, -3F);
		Biggun.setTextureSize(64, 32);
		Biggun.mirror = true;
		setRotation(Biggun, 0F, 0F, 0F);
		Stickpart1 = new ModelRenderer(this, 31, 0);
		Stickpart1.addBox(0F, 0F, 0F, 1, 2, 1);
		Stickpart1.setRotationPoint(0F, 16.3F, -4F);
		Stickpart1.setTextureSize(64, 32);
		Stickpart1.mirror = true;
		setRotation(Stickpart1, 0.7727982F, 0F, 0F);
		Stickpart2 = new ModelRenderer(this, 16, 18);
		Stickpart2.addBox(0F, 0F, 0F, 3, 1, 1);
		Stickpart2.setRotationPoint(-2.55F, 20.5F, -3.766667F);
		Stickpart2.setTextureSize(64, 32);
		Stickpart2.mirror = true;
		setRotation(Stickpart2, -0.5061455F, -1.012291F, -0.2617994F);
		Shape1 = new ModelRenderer(this, 31, 4);
		Shape1.addBox(0F, 0F, 0F, 1, 2, 1);
		Shape1.setRotationPoint(0F, 15.6F, -4F);
		Shape1.setTextureSize(64, 32);
		Shape1.mirror = true;
		setRotation(Shape1, -0.7679449F, 0F, 0F);
		Shape2 = new ModelRenderer(this, 36, 0);
		Shape2.addBox(0F, 0F, 0F, 1, 1, 2);
		Shape2.setRotationPoint(3.4F, 21.9F, -3.7F);
		Shape2.setTextureSize(64, 32);
		Shape2.mirror = true;
		setRotation(Shape2, -0.6108652F, -2.146755F, 2.867925F);
		Shape3 = new ModelRenderer(this, 9, 17);
		Shape3.addBox(0F, 0F, 0F, 1, 1, 2);
		Shape3.setRotationPoint(-1.6F, 19.8F, -4.5F);
		Shape3.setTextureSize(64, 32);
		Shape3.mirror = true;
		setRotation(Shape3, -1.029744F, 0.1745329F, 0.9948377F);
		Shape4 = new ModelRenderer(this, 9, 28);
		Shape4.addBox(0F, 0F, 0F, 1, 1, 1);
		Shape4.setRotationPoint(-0.1F, 18.56667F, -6F);
		Shape4.setTextureSize(64, 32);
		Shape4.mirror = true;
		setRotation(Shape4, 0.5585054F, 0.296706F, 0.4537856F);
		Shape5 = new ModelRenderer(this, 0, 23);
		Shape5.addBox(0F, 0F, 0F, 4, 1, 1);
		Shape5.setRotationPoint(3F, 18F, 4F);
		Shape5.setTextureSize(64, 32);
		Shape5.mirror = true;
		setRotation(Shape5, 0F, 0F, 0F);
		Shape6 = new ModelRenderer(this, 0, 26);
		Shape6.addBox(0F, 0F, 0F, 1, 1, 3);
		Shape6.setRotationPoint(3F, 21.5F, -4F);
		Shape6.setTextureSize(64, 32);
		Shape6.mirror = true;
		setRotation(Shape6, 0.5585054F, -0.0872665F, -0.837758F);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3,
			float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, null);
		Handle.render(f5);
		Biggun.render(f5);
		Stickpart1.render(f5);
		Stickpart2.render(f5);
		Shape1.render(f5);
		Shape2.render(f5);
		Shape3.render(f5);
		Shape4.render(f5);
		Shape5.render(f5);
		Shape6.render(f5);
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