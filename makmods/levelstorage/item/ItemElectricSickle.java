package makmods.levelstorage.item;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.recipe.Recipes;

import java.util.ArrayList;
import java.util.List;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.lib.IC2Items;
import makmods.levelstorage.logic.util.BlockLocation;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemElectricSickle extends ItemTool implements IElectricItem {

	public static final String UNLOCALIZED_NAME = "itemElectricSickle";
	public static final String NAME = "Electric Sickle";

	public static final int TIER = 1;
	public static final int STORAGE = 10000;
	public static final int ENERGY_PER_BLOCK = 50;
	public static final int RADIUS = 10;
	public static final int RADIUS_LEAVES = 6;

	public ItemElectricSickle(int id) {
		super(id, 0, EnumToolMaterial.IRON, new Block[] { Block.leaves,
		        Block.tallGrass });

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

	public float getStrVsBlock(ItemStack par1ItemStack, Block par2Block) {
		if (par2Block != null) {
			if (par2Block.isLeaves(null, 0, 0, 0)
			        || par2Block instanceof IPlantable) {
				return EnumToolMaterial.IRON.getEfficiencyOnProperMaterial();
			}
		}
		return 1.0F;
	}

	public boolean onBlockDestroyed(ItemStack par1ItemStack, World par2World,
	        int blockId, int x, int y, int z,
	        EntityLivingBase par7EntityLivingBase) {
		if (!par2World.isRemote) {
			if (ElectricItem.manager.canUse(par1ItemStack, ENERGY_PER_BLOCK)) {
				ElectricItem.manager.use(par1ItemStack, ENERGY_PER_BLOCK,
				        par7EntityLivingBase);
				if (par7EntityLivingBase instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) par7EntityLivingBase;
					Block blockBroken = Block.blocksList[blockId];
					if (blockBroken != null) {
						if (blockBroken instanceof IPlantable) {
							// System.out
							// .println("blockBroken instanceof Blocktallgrass");
							ArrayList<BlockLocation> blocksToBreak = new ArrayList<BlockLocation>();
							for (int xCurr = -(RADIUS / 2); xCurr < (RADIUS / 2); xCurr++) {
								for (int zCurr = -(RADIUS / 2); zCurr < (RADIUS / 2); zCurr++) {
									// System.out.println("adding!");
									blocksToBreak
									        .add(new BlockLocation(
									                player.worldObj.provider.dimensionId,
									                x + xCurr, y, z + zCurr));
								}
							}
							for (BlockLocation curr : blocksToBreak) {
								// System.out.println("breakingblock");
								Block currBlock = Block.blocksList[par2World
								        .getBlockId(curr.getX(), curr.getY(),
								                curr.getZ())];
								if (currBlock != null) {
									// System.out.println(currBlock.getClass()
									// .getName());
									if (currBlock instanceof BlockTallGrass
									        || currBlock instanceof IPlantable) {
										// System.out.println("breakingblock");
										if (currBlock
										        .removeBlockByPlayer(
										                par2World,
										                (EntityPlayer) par7EntityLivingBase,
										                curr.getX(),
										                curr.getY(),
										                curr.getZ())) {
											if (ElectricItem.manager.canUse(
											        par1ItemStack,
											        ENERGY_PER_BLOCK))
												ElectricItem.manager.use(
												        par1ItemStack,
												        ENERGY_PER_BLOCK,
												        player);
											else {
												break;
											}
											currBlock.dropBlockAsItem(
											        par2World, curr.getX(),
											        curr.getY(), curr.getZ(),
											        par2World.getBlockMetadata(
											                curr.getX(),
											                curr.getY(),
											                curr.getZ()), 2);
										}
									}
								}
							}
						}

						if (blockBroken.isLeaves(par2World, x, y, z)) {
							ArrayList<BlockLocation> blocksToBreak = new ArrayList<BlockLocation>();
							for (int yCurr = -(RADIUS_LEAVES / 2); yCurr < (RADIUS_LEAVES / 2); yCurr++) {
								for (int xCurr = -(RADIUS_LEAVES / 2); xCurr < (RADIUS_LEAVES / 2); xCurr++) {
									for (int zCurr = -(RADIUS_LEAVES / 2); zCurr < (RADIUS_LEAVES / 2); zCurr++) {
										// System.out.println("adding!");
										blocksToBreak
										        .add(new BlockLocation(
										                player.worldObj.provider.dimensionId,
										                x + xCurr, y + yCurr, z
										                        + zCurr));
									}
								}
							}
							for (BlockLocation curr : blocksToBreak) {
								Block currBlock = Block.blocksList[par2World
								        .getBlockId(curr.getX(), curr.getY(),
								                curr.getZ())];
								if (currBlock != null) {
									if (currBlock.isLeaves(par2World,
									        curr.getX(), curr.getY(),
									        curr.getZ())) {
										if (currBlock
										        .removeBlockByPlayer(
										                par2World,
										                (EntityPlayer) par7EntityLivingBase,
										                curr.getX(),
										                curr.getY(),
										                curr.getZ())) {
											if (ElectricItem.manager.canUse(
											        par1ItemStack,
											        ENERGY_PER_BLOCK))
												ElectricItem.manager.use(
												        par1ItemStack,
												        ENERGY_PER_BLOCK,
												        player);
											else {
												break;
											}
											currBlock.dropBlockAsItem(
											        par2World, curr.getX(),
											        curr.getY(), curr.getZ(),
											        par2World.getBlockMetadata(
											                curr.getX(),
											                curr.getY(),
											                curr.getZ()), 2);
										}
									}
								}
							}
						}

					}
				}
			}
		}
		return true;
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
		return 100;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
		        .registerIcon(ClientProxy.ELECTRIC_SICKLE_TEXTURE);
	}

	@Override
	public void addInformation(ItemStack par1ItemStack,
	        EntityPlayer par2EntityPlayer, List par3List, boolean par4) {

	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.common;
	}

	public static void addCraftingRecipe() {

		Recipes.advRecipes.addRecipe(new ItemStack(
		        LSBlockItemList.itemElectricSickle), "  i", "eii", "r  ",
		        Character.valueOf('i'), IC2Items.REFINED_IRON, Character
		                .valueOf('e'), IC2Items.BASIC_CIRCUIT, Character
		                .valueOf('r'), IC2Items.RE_BATTERY);
		Recipes.advRecipes.addRecipe(new ItemStack(
		        LSBlockItemList.itemElectricSickle), "  i", "eii", "r  ",
		        Character.valueOf('i'), IC2Items.REFINED_IRON, Character
		                .valueOf('e'), IC2Items.BASIC_CIRCUIT, Character
		                .valueOf('r'), IC2Items.RE_BATTERY_CHARHED);

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
