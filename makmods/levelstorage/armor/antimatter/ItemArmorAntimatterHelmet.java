package makmods.levelstorage.armor.antimatter;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.recipe.Recipes;
import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.init.IHasRecipe;
import makmods.levelstorage.item.SimpleItems.SimpleItemShortcut;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenDesert;

public class ItemArmorAntimatterHelmet extends ItemArmorAntimatterBase
		implements IHasRecipe {

	public ItemArmorAntimatterHelmet(int id) {
		super(id, ItemArmorAntimatterBase.HELMET);
	}

	@Override
	public void onArmorTickUpdate(World world, EntityPlayer player,
			ItemStack itemStack) {
		super.onArmorTickUpdate(world, player, itemStack);
		int toCharge = 0;

		if (isSunVisible(player.worldObj, (int) player.posX,
				(int) player.posY + 1, (int) player.posZ)) {
			toCharge += 2048;
		}
		
		for (ItemStack is : player.inventory.armorInventory) {
			if (is == null)
				continue;
			if (is.getItem() instanceof IElectricItem) {
				toCharge -= ElectricItem.manager.charge(is, toCharge, 4, true, false);
				if (toCharge == 0)
					break;
			}
		}
		for (ItemStack is : player.inventory.mainInventory) {
			if (is == null)
				continue;
			if (is.getItem() instanceof IElectricItem) {
				toCharge -= ElectricItem.manager.charge(is, toCharge, 4, true, false);
				if (toCharge == 0)
					break;
			}
		}
	}

	public static boolean isSunVisible(World world, int x, int y, int z) {
		return (world.isDaytime())
				&& (!world.provider.hasNoSky)
				&& (world.canBlockSeeTheSky(x, y, z))
				&& (((world.getWorldChunkManager().getBiomeGenAt(x, z) instanceof BiomeGenDesert)) || ((!world
						.isRaining()) && (!world.isThundering())));
	}

	@Override
	public void addCraftingRecipe() {
		Recipes.advRecipes.addRecipe(new ItemStack(
				LSBlockItemList.itemArmorAntimatterHelmet), "pcp", "pap",
				"pep", 'p', SimpleItemShortcut.PLATE_ANTIMATTER_IRIDIUM
						.getItemStack(), 'e', new ItemStack(
						LSBlockItemList.itemStorageFourtyMillion), 'a',
				new ItemStack(LSBlockItemList.itemArmorForcefieldChestplate),
				'c', new ItemStack(LSBlockItemList.blockMulticoreSolarPanel));
	}
}
