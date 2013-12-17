package makmods.levelstorage.item;

import ic2.api.recipe.Recipes;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.armor.ArmorFunctions;
import makmods.levelstorage.armor.ItemArmorLevitationBoots;
import makmods.levelstorage.armor.ItemArmorTeslaHelmet;
import makmods.levelstorage.item.SimpleItems.SimpleItemShortcut;
import makmods.levelstorage.lib.IC2Items;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAntimatterRing extends ItemQuantumRing {

	public ItemAntimatterRing(int id) {
		super(id);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
				.registerIcon(ClientProxy.ANTIMATTER_RING_TEXTURE);
	}

	@Override
	public void addInformation(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		par3List.add(EnumChatFormatting.DARK_PURPLE
				+ StatCollector.translateToLocal("tooltip.antimatterring"));
	}

	public void onUpdate(ItemStack itemStack, World world, Entity par3Entity,
			int par4, boolean par5) {
		if (!(par3Entity instanceof EntityPlayer))
			return;
		EntityPlayer player = (EntityPlayer) par3Entity;
		InventoryPlayer inv = player.inventory;
		boolean didSomething = false;
		for (ItemStack device : inv.armorInventory)
			if (chargeItem(device, itemStack)) {
				didSomething = true;
				break;
			}
		if (!didSomething) {
			for (ItemStack device : inv.mainInventory) {
				if (device != null)
					if (device.getItem() instanceof ItemQuantumRing)
						continue;
				if (chargeItem(device, itemStack)) {
					didSomething = true;
					break;
				}
			}
		}
		ArmorFunctions.extinguish(player, world);
		ArmorFunctions.jumpBooster(world, player, itemStack);
		ArmorFunctions.fly(ItemArmorLevitationBoots.FLYING_ENERGY_PER_TICK,
				player, itemStack, world);
		ArmorFunctions.bootsSpecialFly(player, world, itemStack);
		ArmorFunctions.speedUp(player, itemStack);
		ArmorFunctions.antimatterLeggingsFunctions(world, player, itemStack);
		ArmorFunctions.helmetFunctions(world, player, itemStack,
				ItemArmorTeslaHelmet.RAY_COST, ItemArmorTeslaHelmet.FOOD_COST);
	}

	public static ItemStack getArmorInternal(String s) {
		if (s.equals("h"))
			return new ItemStack(LSBlockItemList.itemArmorAntimatterHelmet);
		else if (s.equals("c"))
			return new ItemStack(LSBlockItemList.itemArmorAntimatterChestplate);
		else if (s.equals("l"))
			return new ItemStack(LSBlockItemList.itemArmorAntimatterLeggings);
		else if (s.equals("b"))
			return new ItemStack(LSBlockItemList.itemArmorAntimatterBoots);
		else
			throw new RuntimeException(
					"getArmorInternal(): wow, this mustn't happen!!");
	}

	public void addCraftingRecipe() {
		// just so it can be craftable to put four armors in the crafting table
		// in any order... yes, a little tricky of an algorithm, but it works
		List<String> lst = Lists.newArrayList();
		lst.add("h");
		lst.add("c");
		lst.add("l");
		lst.add("b");
		for (String s : lst) {
			for (String s2 : lst) {
				for (String s3 : lst) {
					for (String s4 : lst) {
						List<String> curList = Arrays.asList(s, s2, s3, s4);
						if (curList.contains(lst.get(0))
								&& curList.contains(lst.get(1))
								&& curList.contains(lst.get(2))
								&& curList.contains(lst.get(3))) {
							ItemStack armor1, armor2, armor3, armor4;
							armor1 = getArmorInternal(s);
							armor2 = getArmorInternal(s2);
							armor3 = getArmorInternal(s3);
							armor4 = getArmorInternal(s4);
							Recipes.advRecipes.addRecipe(new ItemStack(this),
									"AIB", "IQI", "CID", 'I',
									SimpleItemShortcut.PLATE_ANTIMATTER_IRIDIUM
											.getItemStack(), 'Q',
									new ItemStack(
											LSBlockItemList.itemQuantumRing),
									'A', armor1, 'B', armor2, 'C', armor3, 'D',
									armor4);
						}
					}
				}
			}
		}
	}
	
	@Override
	public int getMaxCharge(ItemStack itemStack) {
		return (int)Math.pow(10, 9);
	}

	@Override
	public int getTier(ItemStack itemStack) {
		return 5;
	}

	@Override
	public int getTransferLimit(ItemStack itemStack) {
		return (int)Math.pow(10, 7);
	}
}
