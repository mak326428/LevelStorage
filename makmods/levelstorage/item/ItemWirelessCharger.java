package makmods.levelstorage.item;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.recipe.Recipes;

import java.util.ArrayList;
import java.util.List;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.ModBlocks;
import makmods.levelstorage.ModItems;
import makmods.levelstorage.lib.IC2Items;
import makmods.levelstorage.logic.NBTHelper;
import makmods.levelstorage.logic.NBTHelper.Cooldownable;
import makmods.levelstorage.proxy.ClientProxy;
import makmods.levelstorage.registry.WirelessPowerSynchronizerRegistry;
import makmods.levelstorage.registry.WirelessPowerSynchronizerRegistry.WChargerEntry;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemWirelessCharger extends Item implements IElectricItem {

	public static final String UNLOCALIZED_NAME = "itemWirelessCharger";
	public static final String NAME = "Wireless Charger";
	public static final String FREQUENCY_NBT = "frequency";

	public static final int STORAGE = 100000;
	public static final int FREQUENCY_CHANGE_COOLDOWN = 4;
	public static final int TIER = 2;

	public ItemWirelessCharger() {
		super(LevelStorage.configuration.getItem(UNLOCALIZED_NAME,
		        LevelStorage.getAndIncrementCurrId()).getInt());
		this.setUnlocalizedName(UNLOCALIZED_NAME);
		this.setMaxDamage(27);
		this.setNoRepair();
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			this.setCreativeTab(ClientProxy.getCreativeTab("IC2"));
		}
		this.setMaxStackSize(1);
	}

	public static void addCraftingRecipe() {
		Property p = LevelStorage.configuration.get(
		        Configuration.CATEGORY_GENERAL,
		        "enableWirelessChargerCraftingRecipe", true);
		p.comment = "Determines whether or not crafting recipe is enabled";
		if (p.getBoolean(true)) {
			Recipes.advRecipes.addRecipe(new ItemStack(
			        ModItems.instance.itemWirelessCharger), "ccc", "ebe",
			        "eoe", Character.valueOf('c'), IC2Items.ADV_CIRCUIT,
			        Character.valueOf('e'), new ItemStack(Item.enderPearl),
			        Character.valueOf('b'), new ItemStack(
			                ModBlocks.instance.blockWlessPowerSync), Character.valueOf('o'), IC2Items.ENERGY_CRYSTAL);
		}
	}

	/*
	 * public static Integer[] getElectricItems(InventoryPlayer inv) {
	 * ArrayList<Integer> ints = new ArrayList<Integer>(); for (int index = 0;
	 * index < inv.mainInventory.length; index++) { if (inv.mainInventory[index]
	 * != null) if (inv.mainInventory[index].getItem() instanceof IElectricItem)
	 * if (stackDemandsEnergy(inv.mainInventory[index])) ints.add(index); }
	 * return (Integer[]) ints.toArray(new Integer[ints.size()]); }
	 * 
	 * public static boolean stackDemandsEnergy(ItemStack stack) { return
	 * ElectricItem.manager.getCharge(stack) < ((IElectricItem) stack
	 * .getItem()).getMaxCharge(stack); }
	 * 
	 * @Override public void onUpdate(ItemStack par1ItemStack, World par2World,
	 * Entity par3Entity, int par4, boolean par5) { if (!par2World.isRemote) {
	 * if (par3Entity instanceof EntityPlayer) { EntityPlayer player =
	 * (EntityPlayer) par3Entity; WChargerEntry entry = new
	 * WChargerEntry(player, par1ItemStack, 0);
	 * WirelessPowerSynchronizerRegistry.instance.registryChargers .add(entry);
	 * } } }
	 * 
	 * public static int chargeItems(InventoryPlayer pl, int energy) { Integer[]
	 * indexes = getElectricItems(pl); int energyNotUsed = 0; for (int index :
	 * indexes) { energyNotUsed += energy -
	 * ElectricItem.manager.charge(pl.mainInventory[index], energy, 3, true,
	 * false); break; } return energyNotUsed; }
	 * 
	 * public static int acceptEnergy(int amount, EntityPlayer player, ItemStack
	 * stack) { if (!player.worldObj.isRemote) { if
	 * (getElectricItems(player.inventory).length > 0) { return
	 * chargeItems(player.inventory, amount); } else { return amount; } //
	 * System.out.println("Accept energy"); // System.out.println("Amount: " +
	 * amount); // System.out.println("Player: " + player); //
	 * System.out.println("Stack: " + stack); //
	 * player.attackEntityFrom(DamageSource.causePlayerDamage(player), // 1); }
	 * return amount; }
	 */

	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,
	        EntityPlayer par3EntityPlayer) {
		if (!par2World.isRemote) {
			boolean changedSomething = false;
			NBTHelper.checkNBT(par1ItemStack);
			if (!NBTHelper.verifyKey(par1ItemStack, FREQUENCY_NBT))
				NBTHelper.setInteger(par1ItemStack, FREQUENCY_NBT, 0);
			if (Cooldownable.use(par1ItemStack, FREQUENCY_CHANGE_COOLDOWN)) {
				if (par3EntityPlayer.isSneaking()
				        && (NBTHelper.getInteger(par1ItemStack, FREQUENCY_NBT) > 0)) {
					NBTHelper.decreaseIntegerIgnoreZero(par1ItemStack,
					        FREQUENCY_NBT, 1);
					changedSomething = true;
				} else {
					NBTHelper.decreaseIntegerIgnoreZero(par1ItemStack,
					        FREQUENCY_NBT, -1);
					changedSomething = true;
				}
			}
			if (changedSomething)
				LevelStorage.proxy.messagePlayer(
				        par3EntityPlayer,
				        "\2472New frequency: "
				                + NBTHelper.getInteger(par1ItemStack,
				                        FREQUENCY_NBT), new Object[0]);
		}
		return par1ItemStack;
	}

	public void addInformation(ItemStack par1ItemStack,
	        EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		NBTHelper.checkNBT(par1ItemStack);
		if (!NBTHelper.verifyKey(par1ItemStack, FREQUENCY_NBT))
			NBTHelper.setInteger(par1ItemStack, FREQUENCY_NBT, 0);
		par3List.add("\2472Frequency: "
		        + NBTHelper.getInteger(par1ItemStack, FREQUENCY_NBT));
	}

	public static ItemStack[] getElectricItems(InventoryPlayer inv) {
		ArrayList<ItemStack> ints = new ArrayList<ItemStack>();
		for (int index = 0; index < inv.mainInventory.length; index++) {
			if (inv.mainInventory[index] != null)
				if (inv.mainInventory[index].getItem() instanceof IElectricItem)
					if (stackDemandsEnergy(inv.mainInventory[index]))
						if (!(inv.mainInventory[index].getItem() instanceof ItemWirelessCharger))
							ints.add(inv.mainInventory[index]);
		}
		for (int index = 0; index < inv.armorInventory.length; index++) {
			if (inv.armorInventory[index] != null)
				if (inv.armorInventory[index].getItem() instanceof IElectricItem)
					if (stackDemandsEnergy(inv.armorInventory[index]))
						ints.add(inv.armorInventory[index]);
		}
		for (int index = 0; index < inv.mainInventory.length; index++) {
			if (inv.mainInventory[index] != null)
				if (inv.mainInventory[index].getItem() instanceof IElectricItem)
					if (stackDemandsEnergy(inv.mainInventory[index]))
						if (inv.mainInventory[index].getItem() instanceof ItemWirelessCharger)
							ints.add(inv.mainInventory[index]);
		}
		return (ItemStack[]) ints.toArray(new ItemStack[ints.size()]);
	}

	public static boolean stackDemandsEnergy(ItemStack stack) {
		return ElectricItem.manager.getCharge(stack) < ((IElectricItem) stack
		        .getItem()).getMaxCharge(stack);
	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World,
	        Entity par3Entity, int par4, boolean par5) {
		if (!par2World.isRemote) {
			Cooldownable.onUpdate(par1ItemStack, FREQUENCY_CHANGE_COOLDOWN);
			NBTHelper.checkNBT(par1ItemStack);
			if (!NBTHelper.verifyKey(par1ItemStack, FREQUENCY_NBT))
				NBTHelper.setInteger(par1ItemStack, FREQUENCY_NBT, 0);
			if (par3Entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) par3Entity;
				WChargerEntry entry = new WChargerEntry(player, par1ItemStack,
				        NBTHelper.getInteger(par1ItemStack, FREQUENCY_NBT));
				WirelessPowerSynchronizerRegistry.instance.registryChargers
				        .add(entry);
			}
		}
	}

	public static int chargeItems(InventoryPlayer pl, int energy) {
		ItemStack[] indexes = getElectricItems(pl);
		int energyNotUsed = 0;
		for (ItemStack index : indexes) {
			energyNotUsed += energy
			        - ElectricItem.manager
			                .charge(index, energy, 3, true, false);
			break;
		}
		return energyNotUsed;
	}

	public static int acceptEnergy(int amount, EntityPlayer player,
	        ItemStack stack) {
		if (!player.worldObj.isRemote) {
			if (getElectricItems(player.inventory).length > 0) {
				int energyLoose = (int) (amount * 0.05f);
				if (ElectricItem.manager.canUse(stack, energyLoose)) {
					ElectricItem.manager.use(stack, energyLoose, player);
					return chargeItems(player.inventory, amount);
				}
			} else {
				return amount;
			}
		}
		return amount;
	}

	@Override
	public boolean canProvideEnergy(ItemStack itemStack) {
		return false;
	}

	@Override
	public int getChargedItemId(ItemStack itemStack) {
		return this.itemID;
	}

	@Override
	public int getEmptyItemId(ItemStack itemStack) {
		return this.itemID;
	}

	@Override
	public int getMaxCharge(ItemStack itemStack) {
		return STORAGE;
	}

	@Override
	public int getTier(ItemStack itemStack) {
		return TIER;
	}

	@Override
	public int getTransferLimit(ItemStack itemStack) {
		return 1000;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
		        .registerIcon(ClientProxy.WIRELESS_CHARGER_TEXTURE);
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs,
	        List par3List) {
		ItemStack var4 = new ItemStack(this, 1);
		ElectricItem.manager.charge(var4, Integer.MAX_VALUE, Integer.MAX_VALUE,
		        true, false);
		par3List.add(var4);
		par3List.add(new ItemStack(this, 1, this.getMaxDamage()));

	}

}
