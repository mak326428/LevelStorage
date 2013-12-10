package makmods.levelstorage.item;

import java.util.List;

import com.google.common.collect.Lists;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.logic.util.CommonHelper;
import makmods.levelstorage.logic.util.NBTHelper;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class GaussGunRechargeRecipe implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting inventorycrafting, World world) {
		return get(inventorycrafting) != null;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventorycrafting) {
		return get(inventorycrafting);
	}

	public ItemStack get(InventoryCrafting ic) {
		ItemStack gaussGun = null;
		List<ItemStack> allStacks = Lists.newArrayList();
		for (int i = 0; i < 9; i++)
			if (ic.getStackInSlot(i) != null)
				allStacks.add(ic.getStackInSlot(i));
		if (allStacks.size() != 2)
			return null;
		gaussGun = allStacks.get(0);
		if (gaussGun == null)
			return null;
		if (!(gaussGun.getItem() instanceof ItemGaussGun))
			return null;
		gaussGun = gaussGun.copy();
		NBTHelper.checkNBT(gaussGun);
		NBTTagCompound nbt = gaussGun.stackTagCompound;

		ItemStack alreadyExists = null;
		if (nbt.hasKey(ItemGaussGun.NBT_AMMO_TYPE))
			alreadyExists = ItemStack.loadItemStackFromNBT(nbt
					.getCompoundTag(ItemGaussGun.NBT_AMMO_TYPE));
		int stored = 0;
		if (alreadyExists != null)
			stored = nbt.getInteger(ItemGaussGun.NBT_AMMO_AMOUNT);
		ItemStack supposedAmmo = allStacks.get(1).copy();
		if (alreadyExists != null)
			if (!CommonHelper.areStacksEqual(alreadyExists, supposedAmmo))
				return null;
		stored += 1;

		ItemStack output = gaussGun.copy();
		NBTTagCompound type = new NBTTagCompound();
		supposedAmmo.stackSize = 1;
		supposedAmmo.writeToNBT(type);
		output.getTagCompound()
				.setCompoundTag(ItemGaussGun.NBT_AMMO_TYPE, type);
		output.getTagCompound()
				.setInteger(ItemGaussGun.NBT_AMMO_AMOUNT, stored);
		return output;
	}

	@Override
	public int getRecipeSize() {
		return 10;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return null;
	}

}
