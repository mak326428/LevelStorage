package makmods.levelstorage.block.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockRockDesintegrator extends ItemBlock {

	public ItemBlockRockDesintegrator(int par1) {
		super(par1);
	}
	
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
    	par3List.add("Maximum input: 512 EU/t");
    	par3List.add("1 Cobblestone = 64 EU");
    }
}
