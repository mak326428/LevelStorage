package makmods.levelstorage.armor.antimatter;

import ic2.api.item.ElectricItem;
import ic2.api.item.Items;
import ic2.api.recipe.Recipes;

import java.util.List;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.item.SimpleItems.SimpleItemShortcut;
import makmods.levelstorage.logic.LSDamageSource;
import makmods.levelstorage.logic.util.CommonHelper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class ItemArmorAntimatterChestplate extends ItemArmorAntimatterBase
		implements IHasRecipe {

	public static final int INDEX = ItemArmorAntimatterBase.CHESTPLATE + 1;
	public static final int KEEPAWAY_DISTANCE = 16;
	public static final int ENERGY_PER_HIT = 1000;

	public ItemArmorAntimatterChestplate(int id) {
		super(id, ItemArmorAntimatterBase.CHESTPLATE);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@ForgeSubscribe
	public void onUpdate(LivingUpdateEvent event) {
		// if (event.entityLiving.worldObj.isRemote)
		// return;
		if (event.entityLiving instanceof EntityPlayer)
			return;
		if (!(event.entityLiving instanceof EntityMob))
			return;
		List<EntityPlayer> playerList = event.entityLiving.worldObj.playerEntities;
		for (EntityPlayer ep : playerList) {
			ItemStack armor = ep.inventory.armorInventory[INDEX];
			if (armor == null)
				continue;
			if (!(armor.getItem() instanceof ItemArmorAntimatterChestplate))
				continue;
			int distance = CommonHelper.getDistance(ep.posX, ep.posY, ep.posZ,
					event.entityLiving.posX, event.entityLiving.posY,
					event.entityLiving.posZ);
			if (distance < KEEPAWAY_DISTANCE) {
				if (ElectricItem.manager.canUse(armor, ENERGY_PER_HIT)) {
					if (!event.entityLiving.worldObj.isRemote)
						ElectricItem.manager.use(armor, ENERGY_PER_HIT, ep);
					event.entityLiving.attackEntityFrom(
							LSDamageSource.forcefieldArmorInstaKill, 20.0F);
				}
			}
		}
	}

	@ForgeSubscribe
	public void onHurt(LivingHurtEvent event) {
		// if (event.entityLiving.worldObj.isRemote)
		// return;
		if (!(event.entityLiving instanceof EntityPlayer))
			return;
		EntityPlayer ep = (EntityPlayer) event.entityLiving;
		ItemStack armor = ep.inventory.armorInventory[INDEX];
		if (armor == null)
			return;
		if (!(armor.getItem() instanceof ItemArmorAntimatterChestplate))
			return;
		int toBeUsed = (int) (event.ammount * ItemArmorAntimatterBase.ENERGY_PER_DAMAGE);
		if (ElectricItem.manager.canUse(armor, toBeUsed)) {
			if (!event.entityLiving.worldObj.isRemote)
				ElectricItem.manager.use(armor, toBeUsed, ep);
			event.setCanceled(true);
		}
	}

	@Override
	public void addCraftingRecipe() {
		Recipes.advRecipes.addRecipe(new ItemStack(
				LSBlockItemList.itemArmorAntimatterChestplate), "pcp", "pap",
				"pep", 'p', SimpleItemShortcut.PLATE_ANTIMATTER_IRIDIUM
						.getItemStack(), 'e', new ItemStack(
						LSBlockItemList.itemStorageFourtyMillion), 'a',
				new ItemStack(LSBlockItemList.itemArmorForcefieldChestplate),
				'c', Items.getItem("teslaCoil"));
	}
}
