package makmods.levelstorage.item;

import java.util.List;

import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.dimension.LSDimensions;
import makmods.levelstorage.dimension.WorldProviderAntimatterUniverse;
import makmods.levelstorage.init.ModUniversalInitializer;
import makmods.levelstorage.logic.util.TranslocationHelper;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 
 * @author mak326428
 * 
 */
public class SimpleItems extends Item {
	/**
	 * A hacky version of EnumRarity (since it does really bad things on
	 * servers)
	 * 
	 * @author mak326428
	 * 
	 */
	private static enum EnumHackyRarity {
		common(15, "Common"), uncommon(14, "Uncommon"), rare(11, "Rare"), epic(
				13, "Epic");

		/**
		 * A decimal representation of the hex color codes of a the color
		 * assigned to this rarity type. (13 becomes d as in \247d which is
		 * light purple)
		 */
		public final int rarityColor;

		/** Rarity name. */
		public final String rarityName;

		private EnumHackyRarity(int par3, String par4Str) {
			this.rarityColor = par3;
			this.rarityName = par4Str;
		}

		@SideOnly(Side.CLIENT)
		public EnumRarity toEnumRarity() {
			return EnumRarity.values()[this.ordinal()];
		}
	}

	public static SimpleItems instance = null;

	public SimpleItems() {
		super(LevelStorage.configuration.getItem("simpleItems",
				ModUniversalInitializer.instance.getNextItemID()).getInt());
		this.setHasSubtypes(true);
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(LSCreativeTab.instance);
		}
		initItems();
	}

	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer) {
		if (par2World.isRemote)
			return par1ItemStack;
		if (par1ItemStack.getItemDamage() == SimpleItemShortcut.ANTIMATTER_GLOB
				.getMetadata()) {
			if (par2World.provider instanceof WorldProviderAntimatterUniverse) {
				TranslocationHelper.teleportEntity(
						DimensionManager.getWorld(0), par3EntityPlayer, 0,
						new ChunkCoordinates(0, 80, 0), 0.0F);
			} else {
				TranslocationHelper
						.teleportEntity(
								DimensionManager
										.getWorld(LSDimensions.ANTIMATTER_UNIVERSE_DIMENSION_ID),
								par3EntityPlayer,
								LSDimensions.ANTIMATTER_UNIVERSE_DIMENSION_ID,
								new ChunkCoordinates(0, 80, 0), 0.0F);
			}
			//par2World.createExplosion(null, , par4, par6, par8, par9)
			par1ItemStack.stackSize--;
		}
		return par1ItemStack;
	}

	public enum SimpleItemShortcut {
		DUST_TINY_OSMIUM("dustTinyOsmium", EnumHackyRarity.uncommon, false), // 0
		DUST_OSMIUM("dustOsmium", EnumHackyRarity.rare, false), // 1
		OSMIRIDIUM_ALLOY("itemOsmiridiumAlloy", EnumHackyRarity.rare, false), // 2
		OSMIRIDIUM_PLATE("itemOsmiridiumPlate", EnumHackyRarity.epic, false), // 3
		INGOT_OSMIUM("ingotOsmium", EnumHackyRarity.rare, false), // 4
		INGOT_IRIDIUM("ingotIridium", EnumHackyRarity.uncommon, false), // 5
		ULTIMATE_CIRCUIT("itemUltimateCircuit", EnumHackyRarity.rare, false), // 6
		ENERGIZED_NETHER_STAR("itemEnergizedStar", EnumHackyRarity.epic, true), // 7
		ANTIMATTER_MOLECULE("itemAntimatterMolecule", EnumHackyRarity.rare,
				false), // 8
		ANTMATTER_TINY_PILE("itemAntimatterTinyPile", EnumHackyRarity.epic,
				false), // 9
		ANTIMATTER_GLOB("itemAntimatterGlob", EnumHackyRarity.epic, false), // 10
		JETPACK_ACCELERATOR("itemJetpackAccelerator", EnumHackyRarity.uncommon,
				false), // 11

		// TODO: migrate into substances
		DUST_TINY_CHROME("itemDustTinyChrome", EnumHackyRarity.common, false), // 13
		DUST_CHROME("itemDustChrome", EnumHackyRarity.common, false), // 14
		CRUSHED_CHROME_ORE("crushedChromiteOre", EnumHackyRarity.common, false), // 15
		PURIFIED_CHROME_ORE("purifiedCrushedChromiteOre",
				EnumHackyRarity.common, false), // 16
		INGOT_CHROME("ingotChrome", EnumHackyRarity.common, false), // 17
		PLATE_CHROME("plateChrome", EnumHackyRarity.common, false), TINY_IRIDIUM_DUST(
				"dustTinyIridium", EnumHackyRarity.common, false), // 18
		PLATE_ANTIMATTER_IRIDIUM("plateAntimatterIridium", EnumHackyRarity.epic, false);
		final String name;
		final boolean hasEffect;
		final EnumHackyRarity rarity;

		private SimpleItemShortcut(String name, EnumHackyRarity rarity,
				boolean hasEffect) {
			this.name = name;
			this.rarity = rarity;
			this.hasEffect = hasEffect;
		}

		public String getName() {
			return name;
		}

		public boolean hasEffect() {
			return hasEffect;
		}

		public EnumHackyRarity getRarity() {
			return rarity;
		}

		public String toString() {
			return getName();
		}

		public int getMetadata() {
			return ordinal();
		}

		public ItemStack getItemStack() {
			return new ItemStack(SimpleItems.instance.itemID, 1, ordinal())
					.copy();
		}
	}

	public void initItems() {
		SimpleItemShortcut[] vals = SimpleItemShortcut.values();
		for (SimpleItemShortcut val : vals) {
			addItem(val.getName(), val.getRarity(), val.hasEffect());
		}
	}

	public ItemStack getItemForName(String name) {
		if (name == null)
			return null;
		try {
			return new ItemStack(this.itemID, 1, itemNames.indexOf(name));
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets the ingredient
	 * 
	 * @param metadata
	 *            Metadata of item you want to get
	 * @return ItemStack containing item you requested, if not found, null
	 */
	public ItemStack getIngredient(int metadata) {
		if (metadata < 0)
			return null;
		if (metadata >= itemNames.size())
			return null;
		return new ItemStack(this.itemID, 1, metadata);
	}

	private List<Icon> itemIcons = Lists.newArrayList();
	public List<String> itemNames = Lists.newArrayList();
	private List<EnumHackyRarity> itemRarities = Lists.newArrayList();
	private List<Boolean> itemsHaveEffect = Lists.newArrayList();

	public void addItem(String name, EnumHackyRarity rarity, boolean hasEffect) {
		itemNames.add(name);
		itemRarities.add(rarity);
		itemsHaveEffect.add(hasEffect);
		ItemStack currStack = new ItemStack(this.itemID, 1,
				itemNames.size() - 1);
		// LanguageRegistry.instance().addStringLocalization(name + ".name",
		// localizedName);
		OreDictionary.registerOre(name, currStack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack par1ItemStack, int pass) {
		try {
			return itemsHaveEffect.get(par1ItemStack.getItemDamage());
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack par1ItemStack) {
		try {
			return itemRarities.get(par1ItemStack.getItemDamage())
					.toEnumRarity();
		} catch (Throwable t) {
			return EnumRarity.common;
		}
	}

	public String getUnlocalizedName(ItemStack stack) {
		try {
			return (String) itemNames.get(stack.getItemDamage());
		} catch (Throwable t) {
			return null;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister iconRegister) {
		for (String name : itemNames) {
			itemIcons.add(iconRegister.registerIcon(ClientProxy
					.getTexturePathFor(name)));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Icon getIconFromDamage(int par1) {
		try {
			return (Icon) itemIcons.get(par1);
		} catch (Throwable t) {
			return null;
		}
	}

	public void getSubItems(int par1, CreativeTabs par2CreativeTabs,
			List par3List) {
		for (int i = 0; i < itemNames.size(); i++) {
			par3List.add(new ItemStack(par1, 1, i));
		}
	}

}
