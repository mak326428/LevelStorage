package makmods.levelstorage.armor;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IMetalArmor;
import ic2.api.item.Items;
import ic2.api.recipe.Recipes;
import ic2.api.util.Keys;

import java.util.List;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.lib.IC2Items;
import makmods.levelstorage.logic.util.CommonHelper;
import makmods.levelstorage.logic.util.NBTHelper;
import makmods.levelstorage.logic.util.NBTHelper.Cooldownable;
import makmods.levelstorage.proxy.ClientProxy;
import makmods.levelstorage.proxy.CommonProxy;
import makmods.levelstorage.proxy.LSKeyboard;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemArmorEnhancedNanoChestplate extends ItemArmor implements
		ISpecialArmor, IMetalArmor, IElectricItem {
	public static final int TIER = 3;
	public static final int STORAGE = CommonProxy.ENH_LAPPACK_STORAGE;
	public static final int ENERGY_PER_DAMAGE = 800;
	public static int RENDER_ID = 0;

	public ItemArmorEnhancedNanoChestplate(int id) {
		super(id, EnumArmorMaterial.DIAMOND, RENDER_ID, 1);

		this.setMaxDamage(27);
		this.setNoRepair();
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(LSCreativeTab.instance);
		}
		this.setMaxStackSize(1);
	}

	public static void addCraftingRecipe() {
		Recipes.advRecipes.addRecipe(new ItemStack(
				LSBlockItemList.itemArmorEnhancedNanoChestplate), "clc", "qnq",
				"aja", Character.valueOf('c'), IC2Items.CARBON_PLATE.copy(),
				Character.valueOf('n'), Items.getItem("nanoBodyarmor"),
				Character.valueOf('l'), new ItemStack(
						LSBlockItemList.itemEnhLappack),
				Character.valueOf('j'),
				Items.getItem("electricJetpack").copy(),
				Character.valueOf('a'), IC2Items.ADV_CIRCUIT.copy(), Character
						.valueOf('q'), "itemJetpackAccelerator");
	}

	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.rare;
	}

	public boolean useJetpack(EntityPlayer player, boolean hoverMode) {
		ItemStack jetpack = player.inventory.armorInventory[2];
		float power = 1.0F;
		float dropPercentage = 0.2F;
		dropPercentage = 0.05F;
		boolean boost = Keys.instance.isJumpKeyDown(player);

		if (Keys.instance.isForwardKeyDown(player)) {
			float retruster = 0.15F;

			if (hoverMode)
				retruster = 0.65F;

			float forwardpower = power * retruster * 2.0F;

			if (forwardpower > 0.0F) {
				player.moveFlying(0.0F, 0.4F * forwardpower, 0.11F);
			}
		}

		int worldHeight = 256;
		int maxFlightHeight = 256;

		double y = player.posY;

		if (y > maxFlightHeight - 25) {
			if (y > maxFlightHeight)
				y = maxFlightHeight;

			power = (float) (power * ((maxFlightHeight - y) / 25.0D));
		}

		double prevmotion = player.motionY;
		player.motionY = Math.min(player.motionY + power * 0.2F,
				0.6000000238418579D);

		if (hoverMode) {
			float maxHoverY = -0.3F;

			if (Keys.instance.isJumpKeyDown(player)) {
				maxHoverY = 0.2F;
			}

			if (player.motionY > maxHoverY) {
				player.motionY = maxHoverY;

				if (prevmotion > player.motionY)
					player.motionY = prevmotion;
			}
		}

		player.fallDistance = 0.0F;
		player.distanceWalkedModified = 0.0F;

		if (!(player instanceof EntityPlayerMP))
			return true;
		((EntityPlayerMP) player).playerNetServerHandler.ticksForFloatKick = 0;
		return true;
	}

	public static final int ENERGY_FLYING_PER_TICK = 25;
	public static final int COOLDOWN = 10;
	public static final String ON_NBT = "on";

	public void onArmorTickUpdate(World world, EntityPlayer player,
			ItemStack itemStack) {
		NBTHelper.checkNBT(itemStack);
		Cooldownable.onUpdate(itemStack, COOLDOWN);
		if (LevelStorage.isSimulating())
			if (LSKeyboard.getInstance().isKeyDown(player,
					LSKeyboard.JETPACK_SWITCH_KEY_NAME)) {
				if (Cooldownable.use(itemStack, COOLDOWN)) {
					NBTHelper.invertBoolean(itemStack, ON_NBT);
					CommonHelper
							.messagePlayer(
									player,
									EnumChatFormatting.GOLD
											+ StatCollector
													.translateToLocalFormatted(
															"jetpack.statusChange",
															NBTHelper
																	.getBoolean(
																			itemStack,
																			ON_NBT) ? StatCollector
																	.translateToLocal("jetpack.on")
																	: StatCollector
																			.translateToLocal("jetpack.off")));
				}
			}
		if (NBTHelper.getBoolean(itemStack, ON_NBT)) {
			boolean hoverMode = NBTHelper.getBoolean(itemStack, "hoverMode");
			byte toggleTimer = NBTHelper.getByte(itemStack, "toggleTimer");
			boolean jetpackUsed = false;

			if ((Keys.instance.isJumpKeyDown(player))
					&& (Keys.instance.isModeSwitchKeyDown(player))
					&& (toggleTimer == 0)) {
				toggleTimer = 10;
				hoverMode = !hoverMode;

				if (LevelStorage.isSimulating()) {
					NBTHelper.setBoolean(itemStack, "hoverMode", hoverMode);

					if (hoverMode)
						CommonHelper.messagePlayer(player,
								EnumChatFormatting.DARK_GREEN
										+ "Hover Mode enabled.");
					else {
						CommonHelper.messagePlayer(player,
								EnumChatFormatting.DARK_RED
										+ "Hover Mode disabled.");
					}
				}
			}

			if ((Keys.instance.isJumpKeyDown(player))
					|| ((hoverMode) && (player.motionY < -0.3499999940395355D))) {
				if (ElectricItem.manager.canUse(itemStack,
						ENERGY_FLYING_PER_TICK)) {
					jetpackUsed = useJetpack(player, hoverMode);
				}
			}

			if ((LevelStorage.isSimulating()) && (toggleTimer > 0)) {
				toggleTimer = (byte) (toggleTimer - 1);

				NBTHelper.setByte(itemStack, "toggleTimer", toggleTimer);
			}

			if (jetpackUsed) {
				ElectricItem.manager.use(itemStack, ENERGY_FLYING_PER_TICK,
						player);
				player.inventoryContainer.detectAndSendChanges();
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
				.registerIcon(ClientProxy.ENHANCED_NANO_CHESTPLATE_TEXTURE);
	}

	public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase player,
			ItemStack armor, DamageSource source, double damage, int slot) {
		if (source.isUnblockable())
			return new ISpecialArmor.ArmorProperties(0, 0.0D, 0);

		double absorptionRatio = getBaseAbsorptionRatio()
				* getDamageAbsorptionRatio();
		int energyPerDamage = ENERGY_PER_DAMAGE;

		int damageLimit = energyPerDamage > 0 ? 25
				* ElectricItem.manager.getCharge(armor) / energyPerDamage : 0;

		return new ISpecialArmor.ArmorProperties(0, absorptionRatio,
				damageLimit);
	}

	private double getBaseAbsorptionRatio() {
		return 0.4D;
	}

	public double getDamageAbsorptionRatio() {
		return 0.9D;
	}

	public void damageArmor(EntityLivingBase entity, ItemStack stack,
			DamageSource source, int damage, int slot) {
		ElectricItem.manager.discharge(stack, damage * ENERGY_PER_DAMAGE,
				2147483647, true, false);
	}

	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
		if (ElectricItem.manager.getCharge(armor) >= ENERGY_PER_DAMAGE) {
			return (int) Math.round(20.0D * getBaseAbsorptionRatio()
					* getDamageAbsorptionRatio());
		}
		return 0;
	}

	@Override
	public boolean canProvideEnergy(ItemStack itemStack) {
		return true;
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
		return 3000;
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

	@Override
	public boolean isMetalArmor(ItemStack itemstack, EntityPlayer player) {
		return true;
	}

}