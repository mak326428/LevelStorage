package makmods.levelstorage.item;

import java.util.List;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.LevelStorageCreativeTab;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Very, very convenient System for automatic dummyitems-adding. <br />
 * It automatically does all the stuff for you <br />
 * The only thing you need to do is add something like:
 * 
 * <pre>
 * 	<code>
 * 		addItem("textureName", "Name of your item that will be displayed-ingame");
 *  </code>
 * </pre>
 * 
 * to the initItems() method and draw a texture. In order to access created
 * items, just call {@link ItemCraftingIngredients.getIngredient()} passing meta
 * of the item you wanna get. (or via OreDict if you want to)
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
				LevelStorage.getAndIncrementCurrId()).getInt());
		this.setHasSubtypes(true);
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(LevelStorageCreativeTab.instance);
		}
		initItems();
	}

	public void initItems() {
		addItem("dustTinyOsmium", EnumHackyRarity.uncommon, false); // 0
		addItem("dustOsmium", EnumHackyRarity.rare, false); // 1
		addItem("itemOsmiridiumAlloy", EnumHackyRarity.rare, false); // 2
		addItem("itemOsmiridiumPlate", EnumHackyRarity.epic, false); // 3
		addItem("ingotOsmium", EnumHackyRarity.rare, false); // 4
		addItem("ingotIridium", EnumHackyRarity.uncommon, false); // 5
		addItem("itemUltimateCircuit", EnumHackyRarity.rare, false); // 6
		addItem("itemEnergizedStar", EnumHackyRarity.epic, true); // 7
		addItem("itemAntimatterMolecule", EnumHackyRarity.rare, false); // 8
		addItem("itemAntimatterTinyPile", EnumHackyRarity.epic, false); // 9
		addItem("itemAntimatterGlob", EnumHackyRarity.epic, true); // 10
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
