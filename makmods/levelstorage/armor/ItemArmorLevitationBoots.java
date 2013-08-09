package makmods.levelstorage.armor;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IMetalArmor;
import ic2.api.item.Items;
import ic2.api.recipe.Recipes;

import java.util.HashMap;
import java.util.List;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.ModItems;
import makmods.levelstorage.api.IFlyArmor;
import makmods.levelstorage.logic.IC2Access;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemArmorLevitationBoots extends ItemArmor implements
		ISpecialArmor, IMetalArmor, IElectricItem, IFlyArmor {

	public static final String UNLOCALIZED_NAME = "armorLevitationBoots";
	public static final String NAME = "Levitation Boots";

	public static final int TIER = 3;
	public static final int STORAGE = 3 * 1000 * 1000;
	public static final int ENERGY_PER_DAMAGE = 900;
	public static final int FLYING_ENERGY_PER_TICK = 512;

	public ItemArmorLevitationBoots() {
		super(LevelStorage.configuration.getItem(UNLOCALIZED_NAME,
				LevelStorage.getAndIncrementCurrId()).getInt(),
				EnumArmorMaterial.DIAMOND, 5, 3);
		this.setUnlocalizedName(UNLOCALIZED_NAME);
		this.setMaxDamage(27);
		this.setNoRepair();
		MinecraftForge.EVENT_BUS.register(this);
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			this.setCreativeTab(ClientProxy.getCreativeTab("IC2"));
		}
		this.setMaxStackSize(1);
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

	public static HashMap<EntityPlayer, Boolean> onGroundMap = new HashMap<EntityPlayer, Boolean>();
	private float jumpCharge;

	public static void checkPlayer(EntityPlayer p) {
		if (p.capabilities.isCreativeMode)
			return;
		InventoryPlayer inv = p.inventory;
		boolean isFound = false;

		for (ItemStack st : inv.armorInventory) {
			if (st != null && st.getItem() instanceof IFlyArmor)
				isFound = true;
		}

		if (!isFound) {
			p.capabilities.allowFlying = false;
			p.capabilities.isFlying = false;
		}
	}

	@Override
	public void onArmorTickUpdate(World world, EntityPlayer player,
			ItemStack itemStack) {
		boolean boostKey = IC2Access.instance.isKeyDown("Boost", player);
		// QUANTUM SUIT ABILITIES
		if (!onGroundMap.containsKey(player))
			onGroundMap.put(player, true);
		if (!world.isRemote) {
			boolean wasOnGround = onGroundMap.containsKey(player) ? ((Boolean) onGroundMap
					.get(player)).booleanValue() : true;

			if ((wasOnGround) && (!player.onGround)
					&& (IC2Access.instance.isKeyDown("Jump", player))
					&& (IC2Access.instance.isKeyDown("Boost", player))) {
				ElectricItem.manager.use(itemStack, 4000, null);
			}
			onGroundMap.remove(player);
			onGroundMap.put(player, Boolean.valueOf(player.onGround));
		} else {
			if ((ElectricItem.manager.canUse(itemStack, 4000))
					&& (player.onGround))
				this.jumpCharge = 2.0F;

			if ((player.motionY >= 0.0D) && (this.jumpCharge > 0.0F)
					&& (!player.isInWater())) {
				if ((IC2Access.instance.isKeyDown("Jump", player) && (IC2Access.instance
						.isKeyDown("Boost", player)))) {
					if (this.jumpCharge == 2.0F) {
						player.motionX *= 3.5D;
						player.motionZ *= 3.5D;
					}

					player.motionY += this.jumpCharge * 0.3F;
					this.jumpCharge = ((float) (this.jumpCharge * 0.75D));
				} else if (this.jumpCharge < 1.0F) {
					this.jumpCharge = 0.0F;
				}

			}// END OF QUANTUM SUIT ABILITIES

		}
		if (ElectricItem.manager.canUse(itemStack, FLYING_ENERGY_PER_TICK)) {
			player.capabilities.allowFlying = true;
			if (player.capabilities.isFlying) {
				if (boostKey) {
					float boost = 0.44f;
					player.moveFlying(0.0F, 1.0F, boost);
					if (!world.isRemote) {
						ElectricItem.manager.use(itemStack,
								FLYING_ENERGY_PER_TICK * 3, player);
					}
				}
				if (!world.isRemote) {
					ElectricItem.manager.use(itemStack, FLYING_ENERGY_PER_TICK,
							player);
				}
			}
		} else {
			player.capabilities.allowFlying = false;
			if (player.capabilities.isFlying)
				player.capabilities.isFlying = false;
		}

	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity entity, int slot,
			int layer) {
		return ClientProxy.ARMOR_LEVITATION_BOOTS_TEXTURE;
	}

	public static void addCraftingRecipe() {
		Property p = LevelStorage.configuration.get(
				Configuration.CATEGORY_GENERAL,
				"enableLevitationBootsCraftingRecipe", true);
		p.comment = "Determines whether or not crafting recipe is enabled";
		if (p.getBoolean(true)) {
			Recipes.advRecipes.addRecipe(new ItemStack(
					ModItems.instance.itemLevitationBoots), "iii", "iqi",
					"lll", Character.valueOf('i'), Items
							.getItem("iridiumPlate"), Character.valueOf('q'),
					Items.getItem("quantumBoots"), Character.valueOf('l'),
					Items.getItem("lapotronCrystal"));
		}
	}

	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.epic;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
				.registerIcon(ClientProxy.LEVITATION_BOOTS_TEXTURE);
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
		return 0.15D;
	}

	public double getDamageAbsorptionRatio() {
		return 1.0D;
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
		return 10000;
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

	@Override
	public boolean isFlyArmor() {
		return true;
	}

}
