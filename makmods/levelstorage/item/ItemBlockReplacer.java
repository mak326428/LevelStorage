package makmods.levelstorage.item;

import java.util.EnumSet;
import java.util.List;

import makmods.levelstorage.logic.util.NBTHelper;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockReplacer extends ItemBasicElectric {

	public static class RenderHandler implements ITickHandler {

		@Override
		public void tickStart(EnumSet<TickType> type, Object... tickData) {
			;
		}

		@Override
		public void tickEnd(EnumSet<TickType> type, Object... tickData) {
			// Tessellator t = Tessellator.instance;
			// t.startDrawingQuads();
			// t.draw();
			/*GL11.glPushMatrix();
			System.out.println(Minecraft.getMinecraft().displayWidth);
			System.out.println(Minecraft.getMinecraft().displayHeight);
			
			int xToDraw = Minecraft.getMinecraft().displayWidth / 4;
			int yToDraw = Minecraft.getMinecraft().displayHeight / 2 / 3;
			ItemBlockReplacer.renderRotatingBlockIntoGUI(Minecraft
					.getMinecraft().fontRenderer, new ItemStack(
					Block.blockDiamond, 1), xToDraw, yToDraw, -1, 1.3F);
			GL11.glPopMatrix();*/
		}

		@Override
		public EnumSet<TickType> ticks() {
			return EnumSet.of(TickType.RENDER);
		}

		@Override
		public String getLabel() {
			return "BlockReplacerTickHandler";
		}

	}

	public static final String TARGET_BLOCK_ID = "targetBlockID";
	public static final String TARGET_BLOCK_META = "targetBlockMeta";

	public ItemBlockReplacer(int id) {
		super(id, 2, 100000, 1000, 250);
		if (FMLCommonHandler.instance().getSide().isClient())
			TickRegistry.registerTickHandler(new RenderHandler(), Side.CLIENT);
	}

	@Override
	public boolean onBlockClick(ItemStack item, World world,
			EntityPlayer player, int x, int y, int z, int side) {
		if (player.isSneaking()) {
			if (world.isAirBlock(x, y, z))
				return false;
			int blockID = world.getBlockId(x, y, z);
			int blockMeta = world.getBlockMetadata(x, y, z);
			NBTHelper.setInteger(item, TARGET_BLOCK_ID, blockID);
			NBTHelper.setInteger(item, TARGET_BLOCK_META, blockMeta);
			return false;
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemTexture() {
		return ClientProxy.ITEM_BLOCK_REPLACER_TEXTURE;
	}

	public void addInformation(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		NBTHelper.checkNBT(par1ItemStack);
		int blockID = NBTHelper.getInteger(par1ItemStack, TARGET_BLOCK_ID);
		int blockMeta = NBTHelper.getInteger(par1ItemStack, TARGET_BLOCK_META);
		try {
			Block bl = Block.blocksList[blockID];
			if (bl == null) {
				par3List.add("");
			} else {

			}
		} catch (Throwable t) {
			par3List.add(EnumChatFormatting.DARK_RED + "Error.");
		}
	}

	private static int rotationAngle = 0;

	public static void renderRotatingBlockIntoGUI(FontRenderer fontRenderer,
			ItemStack stack, int x, int y, float zLevel, float scale) {

		RenderBlocks renderBlocks = new RenderBlocks();

		Block block = Block.blocksList[stack.itemID];
		FMLClientHandler.instance().getClient().renderEngine
				.bindTexture(TextureMap.locationBlocksTexture);
		GL11.glPushMatrix();
		GL11.glTranslatef(x - 2, y + 3, -3.0F + zLevel);
		GL11.glScalef(10.0F, 10.0F, 10.0F);
		GL11.glTranslatef(1.0F, 0.5F, 1.0F);
		GL11.glScalef(1.0F * scale, 1.0F * scale, -1.0F);
		GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(0F + 1 * rotationAngle, 0.0F, 1.0F, 0.0F);
		rotationAngle = (rotationAngle + 1) % 360;

		int var10 = Item.itemsList[stack.itemID]
				.getColorFromItemStack(stack, 0);
		float var16 = (var10 >> 16 & 255) / 255.0F;
		float var12 = (var10 >> 8 & 255) / 255.0F;
		float var13 = (var10 & 255) / 255.0F;

		GL11.glColor4f(var16, var12, var13, 1.0F);

		GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
		renderBlocks.useInventoryTint = true;
		renderBlocks.renderBlockAsItem(block, stack.getItemDamage(), 1.0F);
		renderBlocks.useInventoryTint = true;
		GL11.glPopMatrix();
	}

	@Override
	public boolean onRightClick(ItemStack item, World world, EntityPlayer player) {
		return false;
	}

}
