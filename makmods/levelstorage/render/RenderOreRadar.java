package makmods.levelstorage.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import makmods.levelstorage.armor.ItemArmorTeslaHelmet;
import makmods.levelstorage.logic.BlockLocation;
import makmods.levelstorage.logic.OreDictHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.event.ForgeSubscribe;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderOreRadar {

	public static final int RENDER_DISTANCE = 16;
	public static final int REFRESH_RATE = 40;
	public static ArrayList<BlockLocation> oreLoc = new ArrayList<BlockLocation>();

	public static int currFresh = 0;

	@ForgeSubscribe
	public void radarRender(DrawBlockHighlightEvent event) {
		currFresh++;
		if (ItemArmorTeslaHelmet.playerGetArmor(event.player) != null) {
			// REFRESHING
			if (currFresh > REFRESH_RATE) {
				currFresh = 0;
				oreLoc.clear();
				int initialPosX = (int) event.player.posX;
				int initialPosY = (int) event.player.posY;
				int initialPosZ = (int) event.player.posZ;

				for (int x = -RENDER_DISTANCE; x < RENDER_DISTANCE; x++) {
					for (int y = -RENDER_DISTANCE; y < RENDER_DISTANCE; y++) {
						for (int z = -RENDER_DISTANCE; z < RENDER_DISTANCE; z++) {
							try {
								World w = event.player.worldObj;
								int currX = initialPosX + x;
								int currY = initialPosY + y;
								int currZ = initialPosZ + z;
								if (!w.isAirBlock(currX, currY, currZ)) {
									int blockId = w.getBlockId(currX, currY,
											currZ);
									int meta = w.getBlockMetadata(currX, currY,
											currZ);
									Block bl = Block.blocksList[blockId];
									if (OreDictHelper.getOreName(
											new ItemStack(bl))
											.startsWith("ore")) {
										oreLoc.add(new BlockLocation(
												w.provider.dimensionId, currX,
												currY, currZ));
									}
								}
							} catch (Exception e) {

							}
						}
					}
				}
			}
			// END OF REFRESHING
			// BlockID : Amount
			HashMap<Integer, Integer> ores = new HashMap<Integer, Integer>();

			for (BlockLocation bl : oreLoc) {
				int blockId = event.player.worldObj.getBlockId(bl.getX(),
						bl.getY(), bl.getZ());
				if (!ores.containsKey(blockId)) {
					ores.put(blockId, 1);
				} else {
					int prevAmount = ores.get(blockId);
					ores.remove(blockId);
					ores.put(blockId, prevAmount + 1);
				}
			}
			
			
			
			for (Entry<Integer, Integer> entry : ores.entrySet()) {
				
			}
		}

	}

}
