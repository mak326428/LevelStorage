package makmods.levelstorage.proxy;

import makmods.levelstorage.gui.ContainerPowerSync;
import makmods.levelstorage.gui.ContainerWirelessCharger;
import makmods.levelstorage.gui.ContainerWirelessConductor;
import makmods.levelstorage.gui.ContainerXpCharger;
import makmods.levelstorage.gui.ContainerXpGenerator;
import makmods.levelstorage.gui.GuiWirelessCharger;
import makmods.levelstorage.gui.GuiWirelessConductor;
import makmods.levelstorage.gui.GuiWirelessPowerSync;
import makmods.levelstorage.gui.GuiXpCharger;
import makmods.levelstorage.gui.GuiXpGenerator;
import makmods.levelstorage.item.ItemWirelessCharger;
import makmods.levelstorage.logic.NBTInventory;
import makmods.levelstorage.tileentity.TileEntityWirelessConductor;
import makmods.levelstorage.tileentity.TileEntityWirelessPowerSynchronizer;
import makmods.levelstorage.tileentity.TileEntityXpCharger;
import makmods.levelstorage.tileentity.TileEntityXpGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	// returns an instance of the Container you made earlier
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityXpGenerator)
			return new ContainerXpGenerator(player.inventory,
					(TileEntityXpGenerator) tileEntity);
		if (tileEntity instanceof TileEntityXpCharger)
			return new ContainerXpCharger(player.inventory,
					(TileEntityXpCharger) tileEntity);
		if (tileEntity instanceof TileEntityWirelessConductor)
			return new ContainerWirelessConductor(player.inventory,
					(TileEntityWirelessConductor) tileEntity);
		if (tileEntity instanceof TileEntityWirelessPowerSynchronizer)
			return new ContainerPowerSync(player.inventory,
					(TileEntityWirelessPowerSynchronizer) tileEntity);
		/*
		 * if ((id - CommonProxy.WIRELESS_CHARGER_GUI_PLUS) > 0) { if (x != 0) {
		 * try { int offset = id - CommonProxy.WIRELESS_CHARGER_GUI_PLUS - 1;
		 * int pId = x - 1; // X in this case = player id in world.pEntities
		 * EntityPlayer p = (EntityPlayer) world.playerEntities .get(pId); if (p
		 * != null) { ItemStack neededStack = p.inventory.mainInventory[offset];
		 * 
		 * } } catch (Exception e) { e.printStackTrace(); } } }
		 */
		if (player != null) {
			try {
				if ((id - CommonProxy.WIRELESS_CHARGER_GUI_PLUS) > 0) {
					int offset = id - CommonProxy.WIRELESS_CHARGER_GUI_PLUS - 1;
					ItemStack neededStack = player.inventory.mainInventory[offset];
					if (neededStack != null) {
						return new ContainerWirelessCharger(player.inventory,
								neededStack);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	// returns an instance of the Gui you made earlier
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityXpGenerator)
			return new GuiXpGenerator(player.inventory,
					(TileEntityXpGenerator) tileEntity);
		if (tileEntity instanceof TileEntityXpCharger)
			return new GuiXpCharger(player.inventory,
					(TileEntityXpCharger) tileEntity);
		if (tileEntity instanceof TileEntityWirelessConductor)
			return new GuiWirelessConductor(player.inventory,
					(TileEntityWirelessConductor) tileEntity);
		if (tileEntity instanceof TileEntityWirelessPowerSynchronizer)
			return new GuiWirelessPowerSync(player.inventory,
					(TileEntityWirelessPowerSynchronizer) tileEntity);

		if (player != null) {
			try {
				if ((id - CommonProxy.WIRELESS_CHARGER_GUI_PLUS) > 0) {
					int offset = id - CommonProxy.WIRELESS_CHARGER_GUI_PLUS - 1;
					ItemStack neededStack = player.inventory.mainInventory[offset];
					if (neededStack != null) {
						return new GuiWirelessCharger(player.inventory,
								neededStack);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;

	}
}
