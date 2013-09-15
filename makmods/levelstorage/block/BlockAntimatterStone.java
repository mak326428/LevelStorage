package makmods.levelstorage.block;

import java.util.Random;

import makmods.levelstorage.LevelStorageCreativeTab;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockAntimatterStone extends Block {

	public BlockAntimatterStone(int id) {
		super(id, Material.rock);
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(LevelStorageCreativeTab.instance);
		}
		this.setStepSound(Block.soundStoneFootstep);
		this.setHardness(6.0F);
	}

	/**
	 * The more number the less drop
	 */
	public static final int DROPRATE_RARITY = 500;

	public static void addCraftingRecipe() {
		// --- No crafting recipe ---
	}

	public int quantityDropped(Random par1Random) {
		return par1Random.nextInt(DROPRATE_RARITY) == 0 ? 1 : 0;
	}
	
	public int dropID = OreDictionary.getOres("itemAntimatterMolecule").get(0).itemID;
	public int dropMeta = OreDictionary.getOres("itemAntimatterMolecule").get(0).getItemDamage();
	

	@Override
	public int idDropped(int par1, Random par2Random, int par3) {
		return dropID;
	}

	@Override
	public int damageDropped(int par1) {
		return dropMeta;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		this.blockIcon = iconRegister
				.registerIcon(ClientProxy.ANTIMATTER_STONE_TEXTURE);
	}

}