package makmods.levelstorage.block;

import java.util.Random;

import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockChromiteOre extends Block {

	public BlockChromiteOre(int id) {
		super(id, Material.rock);
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(LSCreativeTab.instance);
		}
		this.setStepSound(Block.soundStoneFootstep);
		this.setHardness(3.0F);
		OreDictionary.registerOre("oreChromite", this);
	}

	public void addCraftingRecipe() {
		// --- No crafting recipe ---
	}

	public int quantityDropped(Random par1Random) {
		return 1;
	}
	
	@Override
	public int idDropped(int par1, Random par2Random, int par3) {
		return blockID;
	}

	@Override
	public int damageDropped(int par1) {
		return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		this.blockIcon = iconRegister
				.registerIcon(ClientProxy.CHROMITE_ORE_TEXTURE);
	}

}