package makmods.levelstorage.compat.nei;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.lib.Reference;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.forge.GuiContainerManager;

public class NEILSConfig implements IConfigureNEI {

	@Override
    public void loadConfig() {
		GuiContainerManager.addTooltipHandler(new XPRegistryHandler());
		API.hideItem(LSBlockItemList.blockSuperconductor.blockID);
		//API.registerRecipeHandler(new XPGeneratorHandler());
		API.registerUsageHandler(new XPGeneratorHandler());
    }

	@Override
    public String getName() {
	    return Reference.MOD_ID;
    }

	@Override
    public String getVersion() {
	    return Reference.VERSION;
    }

}
