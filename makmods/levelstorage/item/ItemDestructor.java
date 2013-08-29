package makmods.levelstorage.item;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.Items;
import ic2.api.recipe.Recipes;

import java.util.List;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.api.IChargeable;
import makmods.levelstorage.lib.IC2Items;
import makmods.levelstorage.logic.IC2Access;
import makmods.levelstorage.logic.LSDamageSource;
import makmods.levelstorage.logic.util.BlockLocation;
import makmods.levelstorage.logic.util.Helper;
import makmods.levelstorage.logic.util.NBTHelper;
import makmods.levelstorage.logic.util.NBTHelper.Cooldownable;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.ForgeDirection;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemDestructor extends Item implements IElectricItem, IChargeable {

	public static final String UNLOCALIZED_NAME = "atomicDisassembler";
	public static final String NAME = "Atomic Disassembler";

	public static final int TIER = 2;
	public static final int STORAGE = 200000;
	public static final int COOLDOWN_USE = 20;
	public static final int ENERGY_USE_BASE = 80;

	public ItemDestructor(int id) {
		super(id);

		this.setMaxDamage(27);
		this.setNoRepair();
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			this.setCreativeTab(ClientProxy.getCreativeTab("IC2"));
		}
		this.setMaxStackSize(1);
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

	public boolean hitEntity(ItemStack par1ItemStack,
	        EntityLivingBase par2EntityLivingBase,
	        EntityLivingBase par3EntityLivingBase) {
		// if (!LevelStorage.isSimulating()) {
		if (DEAL_DAMAGE_TO_OTHERS) {
			int energy = ENERGY_USE_BASE * 200; // ~16 thousand Eu
			if (ElectricItem.manager.canUse(par1ItemStack, energy)) {
				ElectricItem.manager.use(par1ItemStack, energy,
				        par3EntityLivingBase);
				par2EntityLivingBase.attackEntityFrom(
				        LSDamageSource.disassembled, 100);
			}
		}
		// }
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
		        .registerIcon(ClientProxy.DESTRUCTOR_TEXTURE);
	}

	public static boolean DEAL_DAMAGE;
	public static boolean DEAL_DAMAGE_TO_OTHERS;

	static {
		DEAL_DAMAGE = LevelStorage.configuration.get(
		        Configuration.CATEGORY_GENERAL,
		        "atomicDisassemblersEnableDamage", true).getBoolean(true);
		DEAL_DAMAGE_TO_OTHERS = LevelStorage.configuration.get(
		        Configuration.CATEGORY_GENERAL,
		        "atomicDisassemblersEnableDamageToOthers", true).getBoolean(
		        true);
	}

	public boolean isNumberNegative(int number) {
		return number < 0;
	}

	public void changeCharge(ItemStack itemStack, World world,
	        EntityPlayer player) {
		int initialCharge = getChargeFor(itemStack);
		if (player.isSneaking()) {
			if (Cooldownable.use(itemStack, COOLDOWN_USE)) {
				setChargeFor(itemStack, getChargeFor(itemStack) - 1);
			}
		} else {
			if (Cooldownable.use(itemStack, COOLDOWN_USE)) {
				setChargeFor(itemStack, getChargeFor(itemStack) + 1);
			}
		}
		if (initialCharge != getChargeFor(itemStack))
			LevelStorage.proxy
			        .messagePlayer(
			                player,
			                "Tunnel length: "
			                        + (int) Math
			                                .pow(2, getChargeFor(itemStack)),
			                new Object[0]);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world,
	        EntityPlayer player) {
		if (LevelStorage.isSimulating()) {
			if (IC2Access.instance.isKeyDown("ModeSwitch", player)) {
				changeCharge(itemStack, world, player);
			}
		}
		return itemStack;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
	        int x, int y, int z, int side, float par8, float par9, float par10) {
		// TODO: check this, if everything goes the way it should,
		// leave it as is, if not then fallback to !world.isRemote
		if (LevelStorage.isSimulating()) {
			if (IC2Access.instance.isKeyDown("ModeSwitch", player)) {
				changeCharge(stack, world, player);
				return true;
			}
			ForgeDirection hitFrom = ForgeDirection.VALID_DIRECTIONS[side];
			int length = (int) Math.pow(2, getChargeFor(stack));
			int maxDamage = (int) (length / 4 * 1.5);
			int damage = player.worldObj.rand.nextInt(maxDamage + 1);
			if (DEAL_DAMAGE)
				player.attackEntityFrom(LSDamageSource.disassembled, damage);

			for (int curr = 0; curr < length; curr++) {
				BlockLocation loc = new BlockLocation(x, y, z).move(
				        hitFrom.getOpposite(), curr);
				mineThreeByThree(stack, hitFrom, loc, world, player);
			}

			return true;
		}
		return true;
	}

	public static List<Integer> bulkItemsToDelete = Lists.newArrayList();

	static {
		bulkItemsToDelete.add(Block.cobblestone.blockID);
		bulkItemsToDelete.add(Block.dirt.blockID);
		bulkItemsToDelete.add(Block.gravel.blockID);
	}

	public void mineThreeByThree(ItemStack device, ForgeDirection hitFrom,
	        BlockLocation currBlock, World par2World, EntityPlayer player) {
		BlockLocation[] removeBlocks = new BlockLocation[13];
		int fortune = 0;
		switch (hitFrom) {
			case DOWN: {
				removeBlocks[0] = currBlock.move(ForgeDirection.NORTH, 1);
				removeBlocks[1] = currBlock.move(ForgeDirection.WEST, 1);
				removeBlocks[2] = currBlock.move(ForgeDirection.SOUTH, 1);
				removeBlocks[3] = currBlock.move(ForgeDirection.EAST, 1);

				removeBlocks[4] = currBlock.move(ForgeDirection.NORTH, 1).move(
				        ForgeDirection.EAST, 1);
				removeBlocks[5] = currBlock.move(ForgeDirection.WEST, 1).move(
				        ForgeDirection.NORTH, 1);
				removeBlocks[6] = currBlock.move(ForgeDirection.NORTH, 1).move(
				        ForgeDirection.WEST, 1);
				removeBlocks[7] = currBlock.move(ForgeDirection.WEST, 1).move(
				        ForgeDirection.SOUTH, 1);
				removeBlocks[8] = currBlock.move(ForgeDirection.SOUTH, 1).move(
				        ForgeDirection.EAST, 1);
				break;
			}
			case UP: {
				removeBlocks[0] = currBlock.move(ForgeDirection.NORTH, 1);
				removeBlocks[1] = currBlock.move(ForgeDirection.WEST, 1);
				removeBlocks[2] = currBlock.move(ForgeDirection.SOUTH, 1);
				removeBlocks[3] = currBlock.move(ForgeDirection.EAST, 1);

				removeBlocks[4] = currBlock.move(ForgeDirection.NORTH, 1).move(
				        ForgeDirection.EAST, 1);
				removeBlocks[5] = currBlock.move(ForgeDirection.WEST, 1).move(
				        ForgeDirection.NORTH, 1);
				removeBlocks[6] = currBlock.move(ForgeDirection.NORTH, 1).move(
				        ForgeDirection.WEST, 1);
				removeBlocks[7] = currBlock.move(ForgeDirection.WEST, 1).move(
				        ForgeDirection.SOUTH, 1);
				removeBlocks[8] = currBlock.move(ForgeDirection.SOUTH, 1).move(
				        ForgeDirection.EAST, 1);
				break;
			}
			case NORTH: {
				// Up & down
				removeBlocks[0] = currBlock.move(ForgeDirection.UP, 1);
				removeBlocks[1] = currBlock.move(ForgeDirection.DOWN, 1);
				// West & east
				removeBlocks[2] = currBlock.move(ForgeDirection.WEST, 1);
				removeBlocks[3] = currBlock.move(ForgeDirection.EAST, 1);
				// Up-west & Down-west
				removeBlocks[4] = currBlock.move(ForgeDirection.DOWN, 1).move(
				        ForgeDirection.WEST, 1);
				removeBlocks[5] = currBlock.move(ForgeDirection.UP, 1).move(
				        ForgeDirection.EAST, 1);

				removeBlocks[6] = currBlock.move(ForgeDirection.UP, 1).move(
				        ForgeDirection.WEST, 1);
				removeBlocks[7] = currBlock.move(ForgeDirection.DOWN, 1).move(
				        ForgeDirection.EAST, 1);

				removeBlocks[8] = currBlock.move(ForgeDirection.UP, 1).move(
				        ForgeDirection.EAST, 1);
				removeBlocks[9] = currBlock.move(ForgeDirection.DOWN, 1).move(
				        ForgeDirection.WEST, 1);

				break;
				// South up & north down
			}
			case WEST: {
				removeBlocks[0] = currBlock.move(ForgeDirection.UP, 1);
				removeBlocks[1] = currBlock.move(ForgeDirection.DOWN, 1);
				// West & east
				removeBlocks[2] = currBlock.move(ForgeDirection.NORTH, 1);
				removeBlocks[3] = currBlock.move(ForgeDirection.SOUTH, 1);

				removeBlocks[4] = currBlock.move(ForgeDirection.UP, 1).move(
				        ForgeDirection.NORTH, 1);
				removeBlocks[5] = currBlock.move(ForgeDirection.DOWN, 1).move(
				        ForgeDirection.SOUTH, 1);

				removeBlocks[6] = currBlock.move(ForgeDirection.UP, 1).move(
				        ForgeDirection.NORTH, 1);
				removeBlocks[7] = currBlock.move(ForgeDirection.DOWN, 1).move(
				        ForgeDirection.SOUTH, 1);
				removeBlocks[10] = currBlock.move(ForgeDirection.SOUTH, 1)
				        .move(ForgeDirection.UP, 1);
				removeBlocks[11] = currBlock.move(ForgeDirection.NORTH, 1)
				        .move(ForgeDirection.DOWN, 1);
				break;
			}
			case EAST: {
				removeBlocks[0] = currBlock.move(ForgeDirection.UP, 1);
				removeBlocks[1] = currBlock.move(ForgeDirection.DOWN, 1);
				// West & east
				removeBlocks[2] = currBlock.move(ForgeDirection.NORTH, 1);
				removeBlocks[3] = currBlock.move(ForgeDirection.SOUTH, 1);

				removeBlocks[4] = currBlock.move(ForgeDirection.UP, 1).move(
				        ForgeDirection.NORTH, 1);
				removeBlocks[5] = currBlock.move(ForgeDirection.DOWN, 1).move(
				        ForgeDirection.SOUTH, 1);

				removeBlocks[6] = currBlock.move(ForgeDirection.UP, 1).move(
				        ForgeDirection.NORTH, 1);
				removeBlocks[7] = currBlock.move(ForgeDirection.DOWN, 1).move(
				        ForgeDirection.SOUTH, 1);
				removeBlocks[10] = currBlock.move(ForgeDirection.SOUTH, 1)
				        .move(ForgeDirection.UP, 1);
				removeBlocks[11] = currBlock.move(ForgeDirection.NORTH, 1)
				        .move(ForgeDirection.DOWN, 1);
				break;
			}
			case SOUTH: {
				removeBlocks[0] = currBlock.move(ForgeDirection.UP, 1);
				removeBlocks[1] = currBlock.move(ForgeDirection.DOWN, 1);
				// West & east
				removeBlocks[2] = currBlock.move(ForgeDirection.WEST, 1);
				removeBlocks[3] = currBlock.move(ForgeDirection.EAST, 1);
				// Up-west & Down-west
				removeBlocks[4] = currBlock.move(ForgeDirection.DOWN, 1).move(
				        ForgeDirection.WEST, 1);
				removeBlocks[5] = currBlock.move(ForgeDirection.UP, 1).move(
				        ForgeDirection.EAST, 1);

				removeBlocks[6] = currBlock.move(ForgeDirection.UP, 1).move(
				        ForgeDirection.WEST, 1);
				removeBlocks[7] = currBlock.move(ForgeDirection.DOWN, 1).move(
				        ForgeDirection.EAST, 1);

				removeBlocks[8] = currBlock.move(ForgeDirection.UP, 1).move(
				        ForgeDirection.EAST, 1);
				removeBlocks[9] = currBlock.move(ForgeDirection.DOWN, 1).move(
				        ForgeDirection.WEST, 1);
				break;
			}
			case UNKNOWN:
				break;
		}
		removeBlocks[12] = currBlock.copy();
		if (!ElectricItem.manager.canUse(device, ENERGY_USE_BASE))
			return;
		for (BlockLocation blockLoc : removeBlocks) {
			if (blockLoc != null) {
				Block b = Block.blocksList[par2World.getBlockId(
				        blockLoc.getX(), blockLoc.getY(), blockLoc.getZ())];
				int aimBlockMeta = par2World.getBlockMetadata(blockLoc.getX(),
				        blockLoc.getY(), blockLoc.getZ());
				if (b != null) {
					if (b.getBlockHardness(par2World, blockLoc.getX(),
					        blockLoc.getY(), blockLoc.getZ()) > 0.0F) {
						if (b.removeBlockByPlayer(par2World, player,
						        blockLoc.getX(), blockLoc.getY(),
						        blockLoc.getZ())) {
							List<ItemStack> drops = b.getBlockDropped(
							        par2World, blockLoc.getX(),
							        blockLoc.getY(), blockLoc.getZ(),
							        aimBlockMeta, 0);
							for (ItemStack drop : drops) {
								if (!bulkItemsToDelete.contains(drop.itemID)) {
									Helper.dropBlockInWorld_exact(par2World,
									        player.posX, player.posY + 1.6f,
									        player.posZ, drop);
								}
								if (Items.getItem("uraniumDrop").itemID == drop.itemID) {
									par2World.createExplosion(null,
									        blockLoc.getX(), blockLoc.getY(),
									        blockLoc.getZ(), 6F, true);
								}

							}
							// b.dropBlockAsItem(par2World, blockLoc.getX(),
							// blockLoc.getY(), blockLoc.getZ(),
							// aimBlockMeta, 0);
							// That should make explosions semi-rare ocurrance
						}
					}
					if (ElectricItem.manager.canUse(device, ENERGY_USE_BASE)) {
						ElectricItem.manager.use(device, ENERGY_USE_BASE,
						        player);
					} else {
						break;
					}
				}
			}
		}
	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World,
	        Entity par3Entity, int par4, boolean par5) {
		if (!par2World.isRemote) {
			if (!NBTHelper.verifyKey(par1ItemStack, IChargeable.CHARGE_NBT))
				NBTHelper.setInteger(par1ItemStack, IChargeable.CHARGE_NBT, 0);
			Cooldownable.onUpdate(par1ItemStack, COOLDOWN_USE);
		}
	}

	@Override
	public void addInformation(ItemStack par1ItemStack,
	        EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		par3List.add("\247cIt hurts.");
		par3List.add("\247cDon't say you weren't warned before!");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.rare;
	}

	public static void addCraftingRecipe() {

		Recipes.advRecipes.addRecipe(new ItemStack(
		        LSBlockItemList.itemDestructor), "cee", "ccd", "ccc", Character
		        .valueOf('c'), IC2Items.CARBON_PLATE, Character.valueOf('e'),
		        IC2Items.ENERGY_CRYSTAL, Character.valueOf('d'), new ItemStack(
		                LSBlockItemList.itemEnhDiamondDrill));

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
	public int getChargeFor(ItemStack stack) {
		return NBTHelper.getInteger(stack, IChargeable.CHARGE_NBT);
	}

	@Override
	public void setChargeFor(ItemStack stack, int charge) {
		if (charge > getMaxCharge()) {
			NBTHelper.setInteger(stack, IChargeable.CHARGE_NBT, getMaxCharge());
			return;
		} else if (charge < 0) {
			NBTHelper.setInteger(stack, IChargeable.CHARGE_NBT, 0);
			return;
		}
		NBTHelper.setInteger(stack, IChargeable.CHARGE_NBT, charge);
	}

	@Override
	public int getMaxCharge() {
		// 2 to the power of 6
		return 6;
	}
}
