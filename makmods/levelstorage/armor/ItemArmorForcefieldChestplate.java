package makmods.levelstorage.armor;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IMetalArmor;
import ic2.api.recipe.Recipes;

import java.util.List;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.ModItems;
import makmods.levelstorage.lib.IC2Items;
import makmods.levelstorage.logic.LSDamageSource;
import makmods.levelstorage.logic.NBTHelper;
import makmods.levelstorage.proxy.ClientProxy;
import makmods.levelstorage.proxy.CommonProxy;
import makmods.levelstorage.render.EnergyRayFX;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemArmorForcefieldChestplate extends ItemArmor implements
		ISpecialArmor, IMetalArmor, IElectricItem {

	public static final String UNLOCALIZED_NAME = "armorForcefieldChestplate";
	public static final String NAME = "Forcefield Chestplate";

	public static final int TIER = 3;
	public static final int STORAGE = CommonProxy.ARMOR_STORAGE;
	public static final int ENERGY_PER_DAMAGE = 900;
	public static final int ENTITY_MAX_DISTANCE = 16;
	public static final int ENERGY_PER_TICK_ENTITIES = 100;

	public ItemArmorForcefieldChestplate() {
		super(LevelStorage.configuration.getItem(UNLOCALIZED_NAME,
				LevelStorage.getAndIncrementCurrId()).getInt(),
				EnumArmorMaterial.DIAMOND, 5, 1);
		this.setUnlocalizedName(UNLOCALIZED_NAME);
		this.setMaxDamage(27);
		this.setNoRepair();
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			this.setCreativeTab(ClientProxy.getCreativeTab("IC2"));
		}
		this.setMaxStackSize(1);
		MinecraftForge.EVENT_BUS.register(this);
	}

	public static ItemStack playerGetArmor(EntityPlayer p) {
		InventoryPlayer inv = p.inventory;
		ItemStack found = null;

		for (ItemStack st : inv.armorInventory) {
			if (st != null
					&& st.getItem() instanceof ItemArmorForcefieldChestplate)
				found = st;
		}

		return found;
	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void onLivingUpdate(LivingUpdateEvent event) {
		// System.out.println(event.entityLiving.getClass().getCanonicalName());
		// System.out.println("motionX" + event.entityLiving.motionX);
		// System.out.println("motionY" + event.entityLiving.motionY);
		// System.out.println("motionZ" + event.entityLiving.motionZ);
		World w = event.entityLiving.worldObj;
		if (!w.isRemote) {
			if (event.entityLiving instanceof EntityPlayer)
				return;
			for (Object playerObj : w.playerEntities) {
				EntityPlayer p = (EntityPlayer) playerObj;
				ItemStack armor = playerGetArmor(p);
				if (armor != null) {
					double newPosX = event.entityLiving.posX
							+ event.entityLiving.motionX;
					double newPosY = event.entityLiving.posY
							+ event.entityLiving.motionY;
					double newPosZ = event.entityLiving.posZ
							+ event.entityLiving.motionZ;

					double distanceX = Math.abs(p.posX - newPosX);
					double distanceY = Math.abs(p.posY - newPosY);
					double distanceZ = Math.abs(p.posZ - newPosZ);
					double totalDistance = distanceX + distanceY + distanceZ;
					// if (totalDistance > ENTITY_MAX_DISTANCE) {
					if (totalDistance < ENTITY_MAX_DISTANCE) {
						if (ElectricItem.manager.canUse(armor,
								ENERGY_PER_TICK_ENTITIES)) {
							if (event.entityLiving instanceof EntityMob)
								event.entityLiving
										.attackEntityFrom(
												LSDamageSource.forcefieldArmorInstaKill,
												40);
							ElectricItem.manager.use(armor,
									ENERGY_PER_TICK_ENTITIES, p);
							// Nobody uses this, so...
							// event.entityLiving.motionX =
							// -(event.entityLiving.motionX);
							// event.entityLiving.motionY =
							// -(event.entityLiving.motionY - 0.05f);
							// event.entityLiving.motionZ =
							// -(event.entityLiving.motionZ);
						}
					}
				}
			}
		}
	}

	public static final String FORCEFIELD_NBT = "forcefield";
	public static int OMISSION = 0;
	
	static {
		Property p = LevelStorage.configuration.get(Configuration.CATEGORY_GENERAL,
				"forcefieldChestplateRayOmission", 1);
		p.comment = "Determines the omission of Rays when rendering forcefield (set to 361 to completely remove the forcefield rendering)";
		OMISSION = p.getInt(1);
	}

	@Override
	public void onArmorTickUpdate(World world, EntityPlayer player,
			ItemStack itemStack) {
		if (!world.isRemote) {
			player.extinguish();
		}
		if (LevelStorage.fancyGraphics) {
			if (!NBTHelper.verifyKey(itemStack, FORCEFIELD_NBT))
				NBTHelper.setInteger(itemStack, FORCEFIELD_NBT, 10);
			if (NBTHelper.getInteger(itemStack, FORCEFIELD_NBT) > 0)
				NBTHelper.decreaseInteger(itemStack, FORCEFIELD_NBT, 1);
			if (NBTHelper.getInteger(itemStack, FORCEFIELD_NBT) == 0) {
				NBTHelper.setInteger(itemStack, FORCEFIELD_NBT, 10);
				if (LevelStorage.getSide().isClient()) {
					for (int i = 0; i < 360; i += OMISSION) {
						double xC = player.posX + Math.cos(i)
								* ENTITY_MAX_DISTANCE;
						double zC = player.posZ + Math.sin(i)
								* ENTITY_MAX_DISTANCE;
						
						EnergyRayFX p = new EnergyRayFX(world, xC,
								player.posY + 32, zC, xC, player.posY - 32, zC,
								48, 141, 255, 15);
						world.spawnParticle("flame", xC,
								player.posY + 32, zC, 0.0F, 2.0F, 0.0F);
						ModLoader.getMinecraftInstance().effectRenderer
								.addEffect(p);
					}
					
				}
			}
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
				"enableForcefieldChestplateCraftingRecipe", true);
		p.comment = "Determines whether or not crafting recipe is enabled";
		if (p.getBoolean(true)) {
			Recipes.advRecipes.addRecipe(new ItemStack(
					ModItems.instance.itemArmorForcefieldChestplate), "ttt",
					"iqi", "lil", Character.valueOf('t'), IC2Items.TESLA_COIL,
					Character.valueOf('i'), IC2Items.IRIDIUM_PLATE, Character
							.valueOf('q'), IC2Items.QUANTUM_CHESTPLATE,
					Character.valueOf('l'), new ItemStack(
							ModItems.instance.itemStorageFourMillion));
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
				.registerIcon(ClientProxy.FORCEFIELD_CHESTPLATE_TEXTURE);
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
		return 1.1D;
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

}