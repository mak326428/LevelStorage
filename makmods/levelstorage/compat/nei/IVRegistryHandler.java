package makmods.levelstorage.compat.nei;

import java.util.List;

import makmods.levelstorage.api.XPStack;
import makmods.levelstorage.iv.IVRegistry;
import makmods.levelstorage.registry.XPStackRegistry;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import codechicken.nei.forge.IContainerTooltipHandler;

public class IVRegistryHandler implements IContainerTooltipHandler {

	@Override
    public List<String> handleTooltipFirst(GuiContainer gui, int mousex,
            int mousey, List<String> currenttip) {
	    return currenttip;
    }

	@Override
    public List<String> handleItemTooltip(GuiContainer gui,
            ItemStack itemstack, List<String> currenttip) {
		List<String> tip = currenttip;
		int valueFor = IVRegistry.instance.getValueFor(itemstack);
		if (valueFor != IVRegistry.instance.NOT_FOUND) {
			tip.add("IV Value: " + valueFor);
		}
	    return tip;
    }
}
