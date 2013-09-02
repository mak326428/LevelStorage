package makmods.levelstorage.registry;

import ic2.api.recipe.IMachineRecipeManager;
import makmods.levelstorage.api.XPStack;
import net.minecraft.item.ItemStack;

public class IC2MachineRecipeParser implements ISimpleRecipeParser {

	public IMachineRecipeManager recManager;

	public IC2MachineRecipeParser(IMachineRecipeManager recManager) {
		this.recManager = recManager;
	}

	@Override
	public String getName() {
		return recManager.getClass().getSimpleName();
	}

	@Override
	public int parse() {
		int parsed = 0;
		XPStack[] stacks = (XPStack[]) XPStackRegistry.instance.entries
		        .toArray(new XPStack[XPStackRegistry.instance.entries.size()]);
		for (XPStack xps : stacks) {
			Object out = recManager.getOutputFor(xps.stack, false);
			if (out instanceof ItemStack) {
				ItemStack stack = (ItemStack) out;
				if (XPStackRegistry.instance.containsStack(stack))
					continue;
				int value = xps.value;
				XPStackRegistry.instance.pushToRegistry(new XPStack(stack
				        .copy(), value));
			}
		}
		return parsed;
	}
}
