package makmods.levelstorage.compat.nei;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import makmods.levelstorage.iv.IVRegistry;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import codechicken.nei.forge.IContainerTooltipHandler;

import com.google.common.collect.Maps;

public class IVRegistryHandler implements IContainerTooltipHandler {
	
	public Map<List<Integer>, Integer> cache = Maps.newHashMap();

	@Override
    public List<String> handleTooltipFirst(GuiContainer gui, int mousex,
            int mousey, List<String> currenttip) {
	    return currenttip;
    }

	@Override
    public List<String> handleItemTooltip(GuiContainer gui,
            ItemStack itemstack, List<String> currenttip) {
		List<String> tip = currenttip;
		
		List<Integer> key = Arrays.asList(itemstack.itemID, itemstack.getItemDamage());
		if (!cache.containsKey(key))
			cache.put(key, IVRegistry.instance.getValueFor(itemstack));
		int value = cache.get(key);
		if (value != IVRegistry.instance.NOT_FOUND) {
			tip.add("IV Value: " + value);
		}
	    return tip;
    }
}
