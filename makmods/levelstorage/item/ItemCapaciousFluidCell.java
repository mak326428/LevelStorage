package makmods.levelstorage.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import makmods.levelstorage.LevelStorageCreativeTab;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCapaciousFluidCell extends Item {

	public ItemCapaciousFluidCell(int id) {
		super(id);
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(LevelStorageCreativeTab.instance);
		}
		this.setMaxStackSize(64);
		this.setHasSubtypes(true);
		fillMetaListWithFluids();
	}

	public Icon icon_pass_2;

	public ArrayList<Fluid> fluids = Lists.newArrayList();

	public void fillMetaListWithFluids() {
		for (Map.Entry<String, Fluid> entry : FluidRegistry
		        .getRegisteredFluids().entrySet()) {
			fluids.add(entry.getValue());
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		icon_pass_2 = par1IconRegister
		        .registerIcon(ClientProxy.CAPACIOUS_FLUID_CELL_TEXTURE);
	}

	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Icon getIconFromDamageForRenderPass(int par1, int par2) {
		try {

			switch (par2) {
				case 0: {
					Icon ic = fluids.get(par1).getIcon();
					if (ic != null)
						return ic;
					else
						return icon_pass_2;
				}
				case 1:
					return icon_pass_2;
				default:
					return icon_pass_2;
			}
		} catch (Exception e) {
			return icon_pass_2;
		}
	}

	public void getSubItems(int par1, CreativeTabs par2CreativeTabs,
	        List par3List) {
		for (int i = 0; i < fluids.size(); i++) {
			System.out.println(fluids.get(i).getName());
			par3List.add(new ItemStack(par1, 1, i));
		}
	}

}
