package makmods.levelstorage.item;

import java.util.List;
import java.util.logging.Level;

import makmods.levelstorage.ExperienceRecipe;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.ModItems;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemLevelStorageBook extends Item {

	private int bookMaxStorage;
	private int cooldownTimer = 0;

	public static String STORED_XP_NBT = "storedXP";
	public static int XP_PER_INTERACTION = 100;
	
	public static final String UNLOCALIZED_NAME = "xpStorageBook";
	public static final String NAME = "XP Tome";
	

	public ItemLevelStorageBook() {
		super(LevelStorage.configuration.getItem(UNLOCALIZED_NAME,
				LevelStorage.getAndIncrementCurrId()).getInt());
		this.setUnlocalizedName(UNLOCALIZED_NAME);
		this.bookMaxStorage = LevelStorage.itemLevelStorageBookSpace;
		this.setMaxDamage(512);
		this.setNoRepair();
		this.setCreativeTab(CreativeTabs.tabTools);
		this.setMaxStackSize(1);
	}
	
	public static void addCraftingRecipe() {
		// Book
		ItemStack stackDepleted = new ItemStack(ModItems.instance.itemLevelStorageBook, 1, 0);
		stackDepleted.stackTagCompound = new NBTTagCompound();
		ItemStack stackBook = new ItemStack(Item.book);
		ItemStack stackGoldBlock = new ItemStack(Block.blockGold);
		ItemStack stackEnchTable = new ItemStack(Block.enchantmentTable);
		GameRegistry.addShapelessRecipe(stackDepleted, stackBook,
				stackGoldBlock, stackEnchTable);
		CraftingManager.getInstance().getRecipeList()
		.add(new ExperienceRecipe());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer) {
		if (!par2World.isRemote) {
			// It always happen that when you rightclick the item,
			// two or three, or four onItemRightClick(s) will happen immediately
			// Measure to prevent that.
			if (this.cooldownTimer != 0)
				return par1ItemStack;
			this.cooldownTimer = 3;
			// When player presses SHIFT
			// All his XP is drain into book he is holding
			// if he doesn't press SHIFT, he will get XP from book (if there's
			// any)
			if (par3EntityPlayer.isSneaking()) {
				if ((this.bookMaxStorage - getStoredXP(par1ItemStack)) > par3EntityPlayer.experienceTotal) {
					increaseStoredXP(par1ItemStack,
							par3EntityPlayer.experienceTotal);
					par3EntityPlayer.experienceTotal = 0;
					par3EntityPlayer.experienceLevel = 0;
					par3EntityPlayer.experience = 0;
					// par3EntityPlayer.setScore(0);
				}
			} else {
				XP_PER_INTERACTION = par3EntityPlayer.xpBarCap();
				if (getStoredXP(par1ItemStack) > XP_PER_INTERACTION) {
					par3EntityPlayer.addExperience(XP_PER_INTERACTION);
					increaseStoredXP(par1ItemStack, -XP_PER_INTERACTION);
					float f = 5 / 30.0F;
					par2World.playSoundAtEntity(par3EntityPlayer,
							"random.levelup", f * 0.75F, 1.0F);
				} else {
					if (getStoredXP(par1ItemStack) != 0) {
						par3EntityPlayer
								.addExperience(getStoredXP(par1ItemStack));
						par1ItemStack.stackTagCompound.setInteger(
								STORED_XP_NBT, 0);
						float f = 5 / 30.0F;
						par2World.playSoundAtEntity(par3EntityPlayer,
								"random.levelup", f * 0.75F, 1.0F);
					}
				}
			}

			// par1ItemStack.stackTagCompound.setInteger(STORED_XP_NBT,
			// this.stored);
		}
		return par1ItemStack;
	}

	// Just a shortcut for NBT. Will increase stored xp when number is positive,
	// otherwise decrease
	public static void increaseStoredXP(ItemStack stack, int amount) {
		verifyStack(stack);
		stack.stackTagCompound.setInteger(STORED_XP_NBT,
				stack.stackTagCompound.getInteger(STORED_XP_NBT) + amount);
	}

	// Simple NullPointerException protection.
	public static void verifyStack(ItemStack stack) {
		if (stack.stackTagCompound == null) {
			stack.stackTagCompound = new NBTTagCompound();
			FMLLog.log(Level.INFO, "Stack compound is null, setting up one");
		}
		if (!stack.stackTagCompound.hasKey(STORED_XP_NBT)) {
			stack.stackTagCompound.setInteger(STORED_XP_NBT, 0);
		}
	}

	// Simple NBT shortcut. Will get storedXP on given book
	public static int getStoredXP(ItemStack stack) {
		verifyStack(stack);
		return stack.stackTagCompound.getInteger(STORED_XP_NBT);
	}

	// Heart of our storedXP-Durability bridge. Every tick calculates durability
	// Basically, it's an awesome NBT-Durability bridge system, might be very
	// helpful
	// for some other mods
	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World,
			Entity par3Entity, int par4, boolean par5) {
		// .isRemote: When you work with anything that item does, this is a good
		// thing
		// Otherwise weird things will happen
		if (!par2World.isRemote) {
			// if (this.stored > (this.bookMaxStorage - 512))
			// this.stored = this.bookMaxStorage - 1;
			// NO NPE! NPE EVERY TICK WOULDN'T BE NICE!
			verifyStack(par1ItemStack);
			if (this.cooldownTimer > 0) {
				this.cooldownTimer--;
			}
			this.setDamage(par1ItemStack, calculateDurability(par1ItemStack));
		}
	}

	@Override
	public void addInformation(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		// Here we add our nice little tooltip
		super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
		try {
			par3List.add("Stored: "
					+ String.valueOf(getStoredXP(par1ItemStack)) + " XP points");
		} catch (Exception e) {
			par3List.add("Stored: "
					+ String.valueOf(getStoredXP(par1ItemStack)));
		}
	}

	// A method that caluclates durability. Percent based.
	public static int calculateDurability(ItemStack stack) {
		float percent = ((getStoredXP(stack) * 100.0f) / LevelStorage.itemLevelStorageBookSpace) / 100;
		int durability = stack.getMaxDamage()
				- (int) (stack.getMaxDamage() * percent);
		if (durability == 0) {
			durability = 1;
		}
		return durability;
	}

	@SideOnly(Side.CLIENT)
	@Override
	// This book is magical and relatively expensive. I even consider
	// EnumRarity.epic.
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.rare;
	}

	// For NEI and creative. Adds fully charged and discharged versions of book
	// (and yes, i know that "charge" is not the best word here, but whatever..)
	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs,
			List par3List) {
		ItemStack stackFull = new ItemStack(par1, 1, 1);
		stackFull.stackTagCompound = new NBTTagCompound();
		stackFull.stackTagCompound.setInteger(STORED_XP_NBT,
				this.bookMaxStorage -1);
		ItemStack stackDepleted = new ItemStack(par1, 1,
				this.getMaxDamage() - 1);
		stackDepleted.stackTagCompound = new NBTTagCompound();
		stackDepleted.stackTagCompound.setInteger(STORED_XP_NBT, 0);
		par3List.add(stackFull);
		par3List.add(stackDepleted);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon(ClientProxy.BOOK_TEXTURE);
	}
}
