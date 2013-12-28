package makmods.levelstorage.item;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.init.ModUniversalInitializer;
import makmods.levelstorage.proxy.ClientProxy;
import makmods.levelstorage.tileentity.TileEntityIVGenerator;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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

	public static interface ITooltipSensitive {
		public List<String> getTooltip(ItemStack is);
	}

	public void addInformation(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		int meta = par1ItemStack.getItemDamage();
		ITooltipSensitive tooltip = SimpleItemShortcut.values()[meta].tooltipHandler;
		if (tooltip != null) {
			List<String> t = tooltip.getTooltip(par1ItemStack);
			for (String s : t)
				par3List.add(s);
		}
	}

	public enum SimpleItemShortcut {
		DUST_TINY_OSMIUM(0, "dustTinyOsmium", EnumHackyRarity.uncommon, false), // 0
		DUST_OSMIUM(1, "dustOsmium", EnumHackyRarity.rare, false), // 1
		OSMIRIDIUM_ALLOY(2, "itemOsmiridiumAlloy", EnumHackyRarity.rare, false), // 2
		OSMIRIDIUM_PLATE(4, "itemOsmiridiumPlate", EnumHackyRarity.epic, false), // 3
		INGOT_OSMIUM(8, "ingotOsmium", EnumHackyRarity.rare, false), // 4
		INGOT_IRIDIUM(16, "ingotIridium", EnumHackyRarity.uncommon, false), // 5
		ULTIMATE_CIRCUIT(32, "itemUltimateCircuit", EnumHackyRarity.rare, false), // 6
		ENERGIZED_NETHER_STAR(64, "itemEnergizedStar", EnumHackyRarity.epic,
				true), // 7
		ANTIMATTER_MOLECULE(128, "itemAntimatterMolecule",
				EnumHackyRarity.rare, false), // 8
		ANTMATTER_TINY_PILE(192, "itemAntimatterTinyPile",
				EnumHackyRarity.epic, false), // 9
		ANTIMATTER_GLOB(256, "itemAntimatterGlob", EnumHackyRarity.epic, false), // 10
		JETPACK_ACCELERATOR(384, "itemJetpackAccelerator",
				EnumHackyRarity.uncommon, false), // 11
		DUST_TINY_CHROME(512, "itemDustTinyChrome", EnumHackyRarity.common,
				false), // 13
		DUST_CHROME(513, "itemDustChrome", EnumHackyRarity.common, false), // 14
		CRUSHED_CHROME_ORE(514, "crushedChromiteOre", EnumHackyRarity.common,
				false), // 15
		PURIFIED_CHROME_ORE(515, "purifiedCrushedChromiteOre",
				EnumHackyRarity.common, false), // 16
		INGOT_CHROME(516, "ingotChrome", EnumHackyRarity.common, false), // 17
		PLATE_CHROME(517, "plateChrome", EnumHackyRarity.common, false), TINY_IRIDIUM_DUST(
				600, "dustTinyIridium", EnumHackyRarity.common, false), // 18
		PLATE_ANTIMATTER_IRIDIUM(518, "plateAntimatterIridium",
				EnumHackyRarity.epic, false), IV_GENERATOR_UPGRADE(700,
				"craftingUpgradeIVGenerator", EnumHackyRarity.epic, false,
				new ITooltipSensitive() {
					@Override
					public List<String> getTooltip(ItemStack is) {
						return Arrays
								.asList("Increases IV Generator's speed by "
										+ (TileEntityIVGenerator.BUFF_IV_T * is.stackSize)
										+ " IV/t.");
					}
				});
		final String name;
		final boolean hasEffect;
		final EnumHackyRarity rarity;
		final int metadata;
		ITooltipSensitive tooltipHandler;

		private SimpleItemShortcut(int metadata, String name,
				EnumHackyRarity rarity, boolean hasEffect) {
			this.name = name;
			this.rarity = rarity;
			this.metadata = metadata;
			this.hasEffect = hasEffect;
			tooltipHandler = null;
		}

		private SimpleItemShortcut(int metadata, String name,
				EnumHackyRarity rarity, boolean hasEffect,
				ITooltipSensitive tooltipHandler) {
			this(metadata, name, rarity, hasEffect);
			this.tooltipHandler = tooltipHandler;
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
			return metadata;
		}

		public ItemStack getItemStack() {
			return new ItemStack(SimpleItems.instance.itemID, 1, metadata)
					.copy();
		}
	}

	public void initItems() {
		SimpleItemShortcut[] vals = SimpleItemShortcut.values();
		for (SimpleItemShortcut val : vals)
			addItem(val.metadata, val.getName(), val.getRarity(),
					val.hasEffect());
	}

	// Meta <==> SimpleItemData
	private Map<Integer, SimpleItemData> mapping = Maps.newHashMap();

	private static class SimpleItemData {
		public Icon icon;
		public String unlocalizedName;
		public EnumHackyRarity rarity;
		public boolean hasEffect;
	}

	public void addItem(int meta, String name, EnumHackyRarity rarity,
			boolean hasEffect) {
		if (mapping.containsKey(meta))
			throw new RuntimeException(
					"SimpleItems: addItem(): mapping already contains item with metadata ("
							+ name + ")");
		SimpleItemData data = new SimpleItemData();
		data.rarity = rarity;
		data.unlocalizedName = name;
		data.hasEffect = hasEffect;
		mapping.put(meta, data);
		ItemStack currStack = new ItemStack(this.itemID, 1, meta);
		OreDictionary.registerOre(name, currStack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack par1ItemStack, int pass) {
		int meta = par1ItemStack.getItemDamage();
		if (!mapping.containsKey(meta))
			return false;
		return mapping.get(meta).hasEffect;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack par1ItemStack) {
		int meta = par1ItemStack.getItemDamage();
		if (!mapping.containsKey(meta))
			return EnumRarity.common;
		return mapping.get(meta).rarity.toEnumRarity();
	}

	public String getUnlocalizedName(ItemStack stack) {
		int meta = stack.getItemDamage();
		return mapping.get(meta).unlocalizedName;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister iconRegister) {
		for (Entry<Integer, SimpleItemData> entry : mapping.entrySet()) {
			String name = entry.getValue().unlocalizedName;
			entry.getValue().icon = iconRegister.registerIcon(ClientProxy
					.getTexturePathFor(name));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Icon getIconFromDamage(int meta) {
		if (!mapping.containsKey(meta))
			return null;
		try {
			return (Icon) mapping.get(meta).icon;
		} catch (Throwable t) {
			return null;
		}
	}

	public void getSubItems(int par1, CreativeTabs par2CreativeTabs,
			List par3List) {
		for (Entry<Integer, SimpleItemData> entry : mapping.entrySet()) {
			par3List.add(new ItemStack(par1, 1, entry.getKey()));
		}
	}

}
