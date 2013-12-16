package makmods.levelstorage.armor.antimatter;

import ic2.api.item.ElectricItem;
import ic2.api.item.Items;
import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.api.IFlyArmor;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.item.SimpleItems.SimpleItemShortcut;
import makmods.levelstorage.lib.IC2Items;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingFallEvent;

public class ItemArmorAntimatterBoots extends ItemArmorAntimatterBase implements
		IFlyArmor, IHasRecipe {
	public ItemArmorAntimatterBoots(int id) {
		super(id, ItemArmorAntimatterBase.BOOTS);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@ForgeSubscribe
	public void onEntityLivingFallEvent(LivingFallEvent event) {
		if ((!event.entityLiving.worldObj.isRemote)
				&& ((event.entity instanceof EntityLivingBase))) {
			EntityLivingBase entity = (EntityLivingBase) event.entity;
			ItemStack armor = entity.getCurrentItemOrArmor(1);

			if ((armor != null) && (armor.itemID == this.itemID)) {
				int fallDamage = Math.max((int) event.distance - 3 - 7, 0);
				int energyCost = ENERGY_PER_DAMAGE * fallDamage;

				if (energyCost <= ElectricItem.manager.getCharge(armor)) {
					ElectricItem.manager.discharge(armor, energyCost,
							2147483647, true, false);

					event.setCanceled(true);
				}
			}
		}
	}

	@Override
	public boolean isFlyArmor(ItemStack is) {
		return true;
	}

	@Override
	public void addCraftingRecipe() {
		Recipes.advRecipes.addRecipe(new ItemStack(
				LSBlockItemList.itemArmorAntimatterBoots), "ici", "iai",
				"pep", 'p', SimpleItemShortcut.PLATE_ANTIMATTER_IRIDIUM
						.getItemStack(), 'e', new ItemStack(
						LSBlockItemList.itemAntimatterCrystal), 'a',
				new ItemStack(LSBlockItemList.itemLevitationBoots), 'c', Items
						.getItem("staticBoots"), 'i', IC2Items.IRIDIUM_PLATE);
	}
}
