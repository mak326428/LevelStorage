package makmods.levelstorage.client;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ElectricHUD implements ITickHandler {

	public ElectricHUD() {
		TickRegistry.registerTickHandler(this, Side.CLIENT);
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		;
	}

	private static class SimpleEItemsData {
		private int capacity;
		private int charge;

		public SimpleEItemsData(int maxCap, int ch) {
			this.capacity = maxCap;
			this.charge = ch;
		}

		public int getCapacity() {
			return capacity;
		}

		public void setCapacity(int capacity) {
			this.capacity = capacity;
		}

		public int getCharge() {
			return charge;
		}

		public void setCharge(int charge) {
			this.charge = charge;
		}
	}

	public SimpleEItemsData calculateEItemsData(ItemStack[] inv) {
		int capacity = 0;
		int charged = 0;
		for (ItemStack stack : inv) {
			if (stack == null)
				continue;
			if (stack.getItem() instanceof IElectricItem) {
				IElectricItem electricItem = (IElectricItem) stack.getItem();
				capacity += electricItem.getMaxCharge(stack);
				charged += ElectricItem.manager.getCharge(stack);
			}
		}
		return new SimpleEItemsData(capacity, charged);
	}

	public void renderHUD() {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (player != null && Minecraft.getMinecraft().currentScreen == null
				&& !Minecraft.getMinecraft().gameSettings.showDebugInfo) {
			// ARMOR CALCULATIONS
			int percentArmor;
			EnumChatFormatting color = EnumChatFormatting.WHITE;
			FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
			SimpleEItemsData armorData = calculateEItemsData(player.inventory.armorInventory);
			if (armorData.capacity != 0)
				percentArmor = (int) ((armorData.charge * 100.0f) / armorData.capacity);
			else
				percentArmor = 0;

			if (percentArmor > 0 && percentArmor <= 10)
				color = EnumChatFormatting.RED;
			else if (percentArmor > 10 && percentArmor <= 20)
				color = EnumChatFormatting.DARK_RED;
			else if (percentArmor > 20 && percentArmor <= 40)
				color = EnumChatFormatting.GOLD;
			else if (percentArmor > 40 && percentArmor <= 60)
				color = EnumChatFormatting.YELLOW;
			else
				color = EnumChatFormatting.WHITE;

			// ACTUAL RENDERING
			if (percentArmor > 0) {
				FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
				fr.drawStringWithShadow(
						color
								+ StatCollector
										.translateToLocal("other.energyHUD")
								+ " " + percentArmor + "%", 2, 2, 0);
			}
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		renderHUD();
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.RENDER);
	}

	@Override
	public String getLabel() {
		return "ElectricItemHUD";
	}

}
