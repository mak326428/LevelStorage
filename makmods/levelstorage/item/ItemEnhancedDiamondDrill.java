package makmods.levelstorage.item;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;

import java.util.ArrayList;
import java.util.List;

import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.logic.BlockLocation;
import makmods.levelstorage.logic.OreDictHelper;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemEnhancedDiamondDrill extends ItemTool implements IElectricItem {

	public static final String UNLOCALIZED_NAME = "enhancedDDrill";
	public static final String NAME = "Enhanced Diamond Drill";

	public static final float SPEED = 20.0F;
	public static final int TIER = 1;
	public static final int STORAGE = 10000;
	public static final int ENERGY_PER_USE = 100;

	public Icon iconPass1;
	public Icon iconPass2;

	public ItemEnhancedDiamondDrill() {
		super(LevelStorage.configuration.getItem(UNLOCALIZED_NAME,
				LevelStorage.getAndIncrementCurrId()).getInt(), 3,
				EnumToolMaterial.EMERALD, (Block[]) mineableBlocks
						.toArray(new Block[mineableBlocks.size()]));
		this.setUnlocalizedName(UNLOCALIZED_NAME);
		this.setMaxDamage(27);
		this.setNoRepair();
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			this.setCreativeTab(ClientProxy.getCreativeTab("IC2"));
		}
		this.setMaxStackSize(1);
		MinecraftForge.setToolClass(this, "pickaxe", 3);
		// MinecraftForge.setToolClass(this, "shovel", 3);
		this.efficiencyOnProperMaterial = SPEED;
	}

	public static void addCraftingRecipe() {

	}

	public static ArrayList<Block> mineableBlocks = new ArrayList<Block>();

	static {
		mineableBlocks.add(Block.cobblestone);
		mineableBlocks.add(Block.stoneSingleSlab);
		mineableBlocks.add(Block.stoneDoubleSlab);
		mineableBlocks.add(Block.stairsCobblestone);
		mineableBlocks.add(Block.stone);
		mineableBlocks.add(Block.sandStone);
		mineableBlocks.add(Block.stairsSandStone);
		mineableBlocks.add(Block.cobblestoneMossy);
		mineableBlocks.add(Block.oreIron);
		mineableBlocks.add(Block.blockIron);
		mineableBlocks.add(Block.oreCoal);
		mineableBlocks.add(Block.blockGold);
		mineableBlocks.add(Block.oreGold);
		mineableBlocks.add(Block.oreDiamond);
		mineableBlocks.add(Block.blockDiamond);
		mineableBlocks.add(Block.ice);
		mineableBlocks.add(Block.netherrack);
		mineableBlocks.add(Block.oreLapis);
		mineableBlocks.add(Block.blockLapis);
		mineableBlocks.add(Block.oreRedstone);
		mineableBlocks.add(Block.oreRedstoneGlowing);
		mineableBlocks.add(Block.brick);
		mineableBlocks.add(Block.stairsBrick);
		mineableBlocks.add(Block.glowStone);
		mineableBlocks.add(Block.grass);
		mineableBlocks.add(Block.dirt);
		mineableBlocks.add(Block.mycelium);
		mineableBlocks.add(Block.sand);
		mineableBlocks.add(Block.gravel);
		mineableBlocks.add(Block.snow);
		mineableBlocks.add(Block.blockSnow);
		mineableBlocks.add(Block.blockClay);
		mineableBlocks.add(Block.tilledField);
		mineableBlocks.add(Block.stoneBrick);
		mineableBlocks.add(Block.stairsStoneBrick);
		mineableBlocks.add(Block.netherBrick);
		mineableBlocks.add(Block.stairsNetherBrick);
		mineableBlocks.add(Block.slowSand);
		mineableBlocks.add(Block.anvil);
		mineableBlocks.add(Block.oreNetherQuartz);
	}

	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	public Icon getIconFromDamageForRenderPass(int par1, int par2) {
		switch (par2) {
		case 0:
			return iconPass1;
		case 1:
			return iconPass2;
		}
		return null;
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
		return 200;
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

	public int getItemEnchantability() {
		return 0;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack par1ItemStack, World par2World,
			int par3, int par4, int par5, int par6,
			EntityLivingBase par7EntityLivingBase) {
		// if ()
		if (!par2World.isRemote) {
			if (ElectricItem.manager.canUse(par1ItemStack, ENERGY_PER_USE)) {
				ElectricItem.manager.use(par1ItemStack, ENERGY_PER_USE,
						par7EntityLivingBase);
			}
			Block bl = Block.blocksList[par3];
			if (OreDictHelper.getOreName(new ItemStack(bl)).startsWith("ore")) {
				OreFinder finder = new OreFinder();
				finder.aimBlockId = bl.blockID;
				finder.aimBlockMeta = par2World.getBlockMetadata(par4, par5,
						par6);
				finder.world = par2World;
				finder.calibrate(par4, par5, par6);
				System.out.println();
				System.out.println();
				System.out.println("Found ore: ");
				for (BlockLocation loc : finder.foundOre) {
					System.out.println();
					System.out.println("X: " + loc.getX());
					System.out.println("Y: " + loc.getY());
					System.out.println("Z: " + loc.getZ());
					System.out.println(loc.getBlockName());
				}
			}
		}
		return true;
	}

	public class OreFinder {
		public ArrayList<BlockLocation> foundOre = new ArrayList<BlockLocation>();

		public int initialX;
		public int initialY;
		public int initialZ;

		public int aimBlockId;
		public int aimBlockMeta;

		// Unused, but what the heck, let it be here.
		public World world;

		public void calibrate(int x, int y, int z) {
			initialX = x;
			initialY = y;
			initialZ = z;
			BlockLocation initialBlock = new BlockLocation(
					world.provider.dimensionId, x, y, z);
			// foundOre.add(initialBlock);
			findContinuation(initialBlock);
		}

		public void findContinuation(BlockLocation loc) {
			boolean found = false;
			if (loc != null) {
				for (BlockLocation locat : foundOre) {
					if (locat.equals(loc))
						found = true;
				}
				if (!found)
					foundOre.add(loc);
			}
			if (!found) {
				for (int dir = 0; dir < 6; dir++) {
					BlockLocation newTh = loc.move(
							ForgeDirection.values()[dir], 1);
					int currX = newTh.getX();
					int currY = newTh.getY();
					int currZ = newTh.getZ();

					Block currBlock = Block.blocksList[this.world.getBlockId(
							currX, currY, currZ)];
					if (currBlock != null) {
						if (currBlock.blockID == aimBlockId) {
							int currMeta = this.world.getBlockMetadata(currX,
									currY, currZ);
							if (currMeta == aimBlockMeta) {
								// Recursion, very dangerous, but i hope nobody
								// will
								// use
								// this on stone...
								findContinuation(new BlockLocation(
										this.world.provider.dimensionId, currX,
										currY, currZ));
							}
						}
					}
				}
			}
		}
	}

	public float getStrVsBlock(ItemStack itemstack, Block block, int md) {
		if (ElectricItem.manager.canUse(itemstack, ENERGY_PER_USE)) {
			return SPEED;
		} else {
			return 0.1f;
		}
		// return super.getStrVsBlock(itemstack, block, md);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.iconPass1 = par1IconRegister
				.registerIcon(ClientProxy.ITEM_ENHANCED_DIAMOND_DRILL_PASS_ONE);
		this.iconPass2 = par1IconRegister
				.registerIcon(ClientProxy.ITEM_ENHANCED_DIAMOND_DRILL_PASS_TWO);
	}

}
