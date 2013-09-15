package makmods.levelstorage.compat.nei;

import java.util.List;

import makmods.levelstorage.api.XPStack;
import makmods.levelstorage.registry.XPStackRegistry;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import codechicken.nei.forge.IContainerTooltipHandler;

public class XPRegistryHandler implements IContainerTooltipHandler {

	@Override
    public List<String> handleTooltipFirst(GuiContainer gui, int mousex,
            int mousey, List<String> currenttip) {
	    return currenttip;
    }

	@Override
    public List<String> handleItemTooltip(GuiContainer gui,
            ItemStack itemstack, List<String> currenttip) {
		List<String> tip = currenttip;
		for (XPStack stack : XPStackRegistry.instance.entries) {
			ItemStack xpstack = stack.stack;
			if (xpstack.itemID == itemstack.itemID && xpstack.getItemDamage() == itemstack.getItemDamage()) {
				tip.add(StatCollector.translateToLocal("tooltip.xp") + " " + stack.value);
			}
		}
	    return tip;
    }
}
