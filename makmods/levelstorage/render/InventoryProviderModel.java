package makmods.levelstorage.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class InventoryProviderModel extends ModelBase {
	// fields
	ModelRenderer SecondLayer;
	ModelRenderer FirstLayer;
	ModelRenderer Shape1;
	ModelRenderer ThirdLayer;

	public InventoryProviderModel() {
		textureWidth = 128;
		textureHeight = 64;

		SecondLayer = new ModelRenderer(this, 0, 0);
		SecondLayer.addBox(0F, 1.533333F, 0F, 16, 1, 16);
		SecondLayer.setRotationPoint(-8F, 22F, -8F);
		SecondLayer.setTextureSize(128, 64);
		SecondLayer.mirror = true;
		setRotation(SecondLayer, 0F, 0F, 0F);
		FirstLayer = new ModelRenderer(this, 0, 17);
		FirstLayer.addBox(0F, 1F, 0F, 14, 1, 14);
		FirstLayer.setRotationPoint(-7F, 22F, -7F);
		FirstLayer.setTextureSize(128, 64);
		FirstLayer.mirror = true;
		setRotation(FirstLayer, 0F, 0F, 0F);
		Shape1 = new ModelRenderer(this, 0, 32);
		Shape1.addBox(0F, 0F, 0F, 5, 8, 5);
		Shape1.setRotationPoint(-3F, 15F, -2F);
		Shape1.setTextureSize(128, 64);
		Shape1.mirror = true;
		setRotation(Shape1, 0F, 0F, 0F);
		ThirdLayer = new ModelRenderer(this, 64, 0);
		ThirdLayer.addBox(0F, 1F, 0F, 12, 1, 12);
		ThirdLayer.setRotationPoint(-6F, 21F, -6F);
		ThirdLayer.setTextureSize(128, 64);
		ThirdLayer.mirror = true;
		setRotation(ThirdLayer, 0F, 0F, 0F);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3,
	        float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		SecondLayer.render(f5);
		FirstLayer.render(f5);
		Shape1.render(f5);
		ThirdLayer.render(f5);
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
