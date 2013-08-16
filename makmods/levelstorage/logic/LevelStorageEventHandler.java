package makmods.levelstorage.logic;

import ic2.api.item.ElectricItem;
import makmods.levelstorage.api.BootsFlyingEvent;
import makmods.levelstorage.item.ItemPocketRefrigerant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class LevelStorageEventHandler {
	@ForgeSubscribe
	public void onFireHurt(LivingHurtEvent event) {
		if (event.source == DamageSource.onFire
				|| event.source == DamageSource.lava) {
			if (event.entityLiving instanceof EntityPlayer) {
				for (int i = 0; i < 9; i++) {
					if (event.entityLiving instanceof EntityPlayer) {
						EntityPlayer player = ((EntityPlayer) event.entityLiving);
						if (!player.capabilities.isCreativeMode) {
							if (player.inventory.mainInventory[i] != null) {
								if (player.inventory.mainInventory[i].getItem() instanceof ItemPocketRefrigerant) {
									if (ElectricItem.manager
											.canUse(((EntityPlayer) event.entityLiving).inventory.mainInventory[i],
													ItemPocketRefrigerant.ENERGY_PER_USE)) {
										ElectricItem.manager
												.use(((EntityPlayer) event.entityLiving).inventory.mainInventory[i],
														ItemPocketRefrigerant.ENERGY_PER_USE,
														event.entityLiving);
										((EntityPlayer) event.entityLiving)
												.addPotionEffect(new PotionEffect(
														12, 100, 1));
										event.setCanceled(true);
									}
								}
							}
						}
					}
				}
			}
		}
	}
}