package makmods.levelstorage.render;

import makmods.levelstorage.proxy.ClientProxy;
import makmods.levelstorage.tileentity.TileEntitySuperconductorCable;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderSuperconductorCable implements ISimpleBlockRenderingHandler {
	public void renderInventoryBlock(Block block, int metadata, int modelID,
			RenderBlocks renderer) {
	}

	public boolean renderWorldBlock(IBlockAccess blockAccess, int x, int y,
			int z, Block block, int modelId, RenderBlocks renderblocks) {
		//super.renderWorldBlock(blockAccess, x, y, z, block, modelId,
		//		renderblocks);

		TileEntity te = blockAccess.getBlockTileEntity(x, y, z);
		if (!(te instanceof TileEntitySuperconductorCable))
			return true;

		TileEntitySuperconductorCable cable = (TileEntitySuperconductorCable) te;

		float th = cable.getCableThickness();
		float sp = (1.0F - th) / 2.0F;

		int connectivity = cable.connectivity;
		int renderSide = cable.renderSide;

		Icon[] textures = new Icon[6];

		for (int side = 0; side < 6; side++) {
			Icon icon = block.getBlockTexture(blockAccess, x, y, z, side);

			if (icon != null)
				textures[side] = icon;
			else {
				textures[side] = null;
			}
		}

		Tessellator tessellator = Tessellator.instance;
		double xD = x;
		double yD = y;
		double zD = z;

		tessellator.setBrightness(block.getMixedBrightnessForBlock(blockAccess,
				x, y, z));

		if (connectivity == 0) {
			block.setBlockBounds(sp, sp, sp, sp + th, sp + th, sp + th);
			renderblocks.setRenderBoundsFromBlock(block);

			tessellator.setColorOpaque_F(0.5F, 0.5F, 0.5F);
			renderblocks.renderFaceYNeg(block, xD, yD, zD, textures[0]);
			tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
			renderblocks.renderFaceYPos(block, xD, yD, zD, textures[1]);
			tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
			renderblocks.renderFaceZNeg(block, xD, yD, zD, textures[2]);
			renderblocks.renderFaceZPos(block, xD, y, zD, textures[3]);
			tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
			renderblocks.renderFaceXNeg(block, xD, yD, zD, textures[4]);
			renderblocks.renderFaceXPos(block, xD, yD, zD, textures[5]);
		} else if (connectivity == 3) {
			block.setBlockBounds(0.0F, sp, sp, 1.0F, sp + th, sp + th);
			renderblocks.setRenderBoundsFromBlock(block);

			tessellator.setColorOpaque_F(0.5F, 0.5F, 0.5F);
			renderblocks.renderFaceYNeg(block, xD, yD, zD, textures[0]);
			tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
			renderblocks.renderFaceYPos(block, xD, yD, zD, textures[1]);
			tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
			renderblocks.renderFaceZNeg(block, xD, yD, zD, textures[2]);
			renderblocks.renderFaceZPos(block, xD, y, zD, textures[3]);

			if ((renderSide & 0x1) != 0) {
				tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
				renderblocks.renderFaceXNeg(block, xD, yD, zD, textures[4]);
			}

			if ((renderSide & 0x2) != 0) {
				tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
				renderblocks.renderFaceXPos(block, xD, yD, zD, textures[5]);
			}
		} else if (connectivity == 12) {
			block.setBlockBounds(sp, 0.0F, sp, sp + th, 1.0F, sp + th);
			renderblocks.setRenderBoundsFromBlock(block);

			tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
			renderblocks.renderFaceZNeg(block, xD, yD, zD, textures[2]);
			renderblocks.renderFaceZPos(block, xD, y, zD, textures[3]);
			tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
			renderblocks.renderFaceXNeg(block, xD, yD, zD, textures[4]);
			renderblocks.renderFaceXPos(block, xD, yD, zD, textures[5]);

			if ((renderSide & 0x4) != 0) {
				tessellator.setColorOpaque_F(0.5F, 0.5F, 0.5F);
				renderblocks.renderFaceYNeg(block, xD, yD, zD, textures[0]);
			}

			if ((renderSide & 0x8) != 0) {
				tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
				renderblocks.renderFaceYPos(block, xD, yD, zD, textures[1]);
			}
		} else if (connectivity == 48) {
			block.setBlockBounds(sp, sp, 0.0F, sp + th, sp + th, 1.0F);
			renderblocks.setRenderBoundsFromBlock(block);

			tessellator.setColorOpaque_F(0.5F, 0.5F, 0.5F);
			renderblocks.renderFaceYNeg(block, xD, yD, zD, textures[0]);
			tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
			renderblocks.renderFaceYPos(block, xD, yD, zD, textures[1]);
			tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
			renderblocks.renderFaceXNeg(block, xD, yD, zD, textures[4]);
			renderblocks.renderFaceXPos(block, xD, yD, zD, textures[5]);

			if ((renderSide & 0x10) != 0) {
				tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
				renderblocks.renderFaceZNeg(block, xD, y, zD, textures[2]);
			}

			if ((renderSide & 0x20) != 0) {
				tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
				renderblocks.renderFaceZPos(block, xD, yD, zD, textures[3]);
			}
		} else {
			if ((connectivity & 0x1) == 0) {
				block.setBlockBounds(sp, sp, sp, sp + th, sp + th, sp + th);
				renderblocks.setRenderBoundsFromBlock(block);

				tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
				renderblocks.renderFaceXNeg(block, xD, yD, zD, textures[4]);
			} else {
				block.setBlockBounds(0.0F, sp, sp, sp, sp + th, sp + th);
				renderblocks.setRenderBoundsFromBlock(block);

				tessellator.setColorOpaque_F(0.5F, 0.5F, 0.5F);
				renderblocks.renderFaceYNeg(block, xD, yD, zD, textures[0]);
				tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
				renderblocks.renderFaceYPos(block, xD, yD, zD, textures[1]);
				tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
				renderblocks.renderFaceZNeg(block, xD, yD, zD, textures[2]);
				renderblocks.renderFaceZPos(block, xD, y, zD, textures[3]);

				if ((renderSide & 0x1) != 0) {
					tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
					renderblocks.renderFaceXNeg(block, xD, yD, zD, textures[4]);
				}

			}

			if ((connectivity & 0x2) == 0) {
				block.setBlockBounds(sp, sp, sp, sp + th, sp + th, sp + th);
				renderblocks.setRenderBoundsFromBlock(block);

				tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
				renderblocks.renderFaceXPos(block, xD, yD, zD, textures[5]);
			} else {
				block.setBlockBounds(sp + th, sp, sp, 1.0F, sp + th, sp + th);
				renderblocks.setRenderBoundsFromBlock(block);

				tessellator.setColorOpaque_F(0.5F, 0.5F, 0.5F);
				renderblocks.renderFaceYNeg(block, xD, yD, zD, textures[0]);
				tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
				renderblocks.renderFaceYPos(block, xD, yD, zD, textures[1]);
				tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
				renderblocks.renderFaceZNeg(block, xD, yD, zD, textures[2]);
				renderblocks.renderFaceZPos(block, xD, y, zD, textures[3]);

				if ((renderSide & 0x2) != 0) {
					tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
					renderblocks.renderFaceXPos(block, xD, yD, zD, textures[5]);
				}

			}

			if ((connectivity & 0x4) == 0) {
				block.setBlockBounds(sp, sp, sp, sp + th, sp + th, sp + th);
				renderblocks.setRenderBoundsFromBlock(block);

				tessellator.setColorOpaque_F(0.5F, 0.5F, 0.5F);
				renderblocks.renderFaceYNeg(block, xD, yD, zD, textures[0]);
			} else {
				block.setBlockBounds(sp, 0.0F, sp, sp + th, sp, sp + th);
				renderblocks.setRenderBoundsFromBlock(block);

				tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
				renderblocks.renderFaceZNeg(block, xD, yD, zD, textures[2]);
				renderblocks.renderFaceZPos(block, xD, y, zD, textures[3]);
				tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
				renderblocks.renderFaceXNeg(block, xD, yD, zD, textures[4]);
				renderblocks.renderFaceXPos(block, xD, yD, zD, textures[5]);

				if ((renderSide & 0x4) != 0) {
					tessellator.setColorOpaque_F(0.5F, 0.5F, 0.5F);
					renderblocks.renderFaceYNeg(block, xD, yD, zD, textures[0]);
				}

			}

			if ((connectivity & 0x8) == 0) {
				block.setBlockBounds(sp, sp, sp, sp + th, sp + th, sp + th);
				renderblocks.setRenderBoundsFromBlock(block);

				tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
				renderblocks.renderFaceYPos(block, xD, yD, zD, textures[1]);
			} else {
				block.setBlockBounds(sp, sp + th, sp, sp + th, 1.0F, sp + th);
				renderblocks.setRenderBoundsFromBlock(block);

				tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
				renderblocks.renderFaceZNeg(block, xD, yD, zD, textures[2]);
				renderblocks.renderFaceZPos(block, xD, y, zD, textures[3]);
				tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
				renderblocks.renderFaceXNeg(block, xD, yD, zD, textures[4]);
				renderblocks.renderFaceXPos(block, xD, yD, zD, textures[5]);

				if ((renderSide & 0x8) != 0) {
					tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
					renderblocks.renderFaceYPos(block, xD, yD, zD, textures[1]);
				}

			}

			if ((connectivity & 0x10) == 0) {
				block.setBlockBounds(sp, sp, sp, sp + th, sp + th, sp + th);
				renderblocks.setRenderBoundsFromBlock(block);

				tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
				renderblocks.renderFaceZNeg(block, xD, y, zD, textures[2]);
			} else {
				block.setBlockBounds(sp, sp, 0.0F, sp + th, sp + th, sp);
				renderblocks.setRenderBoundsFromBlock(block);

				tessellator.setColorOpaque_F(0.5F, 0.5F, 0.5F);
				renderblocks.renderFaceYNeg(block, xD, yD, zD, textures[0]);
				tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
				renderblocks.renderFaceYPos(block, xD, yD, zD, textures[1]);
				tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
				renderblocks.renderFaceXNeg(block, xD, yD, zD, textures[4]);
				renderblocks.renderFaceXPos(block, xD, yD, zD, textures[5]);

				if ((renderSide & 0x10) != 0) {
					tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
					renderblocks.renderFaceZNeg(block, xD, y, zD, textures[2]);
				}

			}

			if ((connectivity & 0x20) == 0) {
				block.setBlockBounds(sp, sp, sp, sp + th, sp + th, sp + th);
				renderblocks.setRenderBoundsFromBlock(block);

				tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
				renderblocks.renderFaceZPos(block, xD, yD, zD, textures[3]);
			} else {
				block.setBlockBounds(sp, sp, sp + th, sp + th, sp + th, 1.0F);
				renderblocks.setRenderBoundsFromBlock(block);

				tessellator.setColorOpaque_F(0.5F, 0.5F, 0.5F);
				renderblocks.renderFaceYNeg(block, xD, yD, zD, textures[0]);
				tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
				renderblocks.renderFaceYPos(block, xD, yD, zD, textures[1]);
				tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
				renderblocks.renderFaceXNeg(block, xD, yD, zD, textures[4]);
				renderblocks.renderFaceXPos(block, xD, yD, zD, textures[5]);

				if ((renderSide & 0x20) != 0) {
					tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
					renderblocks.renderFaceZPos(block, xD, yD, zD, textures[3]);
				}
			}
		}

		block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		renderblocks.setRenderBoundsFromBlock(block);

		return true;
	}

	public boolean shouldRender3DInInventory() {
		return false;
	}

	@Override
	public int getRenderId() {
		return ClientProxy.CABLE_RENDER_ID;
	}
}