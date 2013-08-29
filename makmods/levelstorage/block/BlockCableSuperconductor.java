package makmods.levelstorage.block;

import ic2.api.Direction;
import ic2.api.event.RetextureEvent;

import java.util.ArrayList;
import java.util.Random;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.logic.uumsystem.UUMHelper;
import makmods.levelstorage.proxy.ClientProxy;
import makmods.levelstorage.tileentity.TileEntitySuperconductorCable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCableSuperconductor extends BlockContainer {

	public BlockCableSuperconductor(int id) {
		super(id, Material.iron);
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(null);
		}
		this.setStepSound(Block.soundClothFootstep);
		this.setHardness(0.2F);
		MinecraftForge.EVENT_BUS.register(this);
	}

	public static void addCraftingRecipe() {

	}

	@Override
	public int idDropped(int par1, Random par2Random, int par3) {
		return LSBlockItemList.itemSuperconductor.itemID;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		this.texture = iconRegister
		        .registerIcon(ClientProxy.BLOCK_SUPERCONDUCTOR_TEXTURE);
	}

	public Icon texture;

	@SideOnly(Side.CLIENT)
	public Icon getBlockTexture(IBlockAccess iBlockAccess, int x, int y, int z,
	        int side) {
		TileEntity te = iBlockAccess.getBlockTileEntity(x, y, z);

		if ((te instanceof TileEntitySuperconductorCable)) {
			TileEntitySuperconductorCable cable = (TileEntitySuperconductorCable) te;

			// int index = Arrays.binarySearch(coloredMetas, cable.cableType);
			return this.texture;
		}

		return null;
	}

	@ForgeSubscribe
	public void onRetexture(RetextureEvent event) {
		TileEntity te = event.world.getBlockTileEntity(event.x, event.y,
		        event.z);

		if (((te instanceof TileEntitySuperconductorCable))
		        && (((TileEntitySuperconductorCable) te).retexture(event.side,
		                event.referencedBlockId, event.referencedMeta,
		                event.referencedSide)))
			event.applied = true;
	}

	public void onNeighborBlockChange(World world, int x, int y, int z,
	        int srcBlockId) {
		super.onNeighborBlockChange(world, x, y, z, srcBlockId);

		if (!world.isRemote) {
			TileEntity te = world.getBlockTileEntity(x, y, z);

			if ((te instanceof TileEntitySuperconductorCable))
				((TileEntitySuperconductorCable) te).onNeighborBlockChange();
		}
	}

	public boolean removeBlockByPlayer(World world, EntityPlayer entityPlayer,
	        int x, int y, int z) {
		TileEntity te = world.getBlockTileEntity(x, y, z);

		if ((te instanceof TileEntitySuperconductorCable)) {
			TileEntitySuperconductorCable cable = (TileEntitySuperconductorCable) te;
			world.notifyBlocksOfNeighborChange(x, y, z, this.blockID);
			// return true;
		}

		return world.setBlock(x, y, z, 0, 0, 3);
	}

	public MovingObjectPosition collisionRayTrace(World world, int x, int y,
	        int z, Vec3 origin, Vec3 absDirection) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (!(te instanceof TileEntitySuperconductorCable))
			return null;

		TileEntitySuperconductorCable tileEntityCable = (TileEntitySuperconductorCable) te;

		Vec3 direction = Vec3.createVectorHelper(absDirection.xCoord
		        - origin.xCoord, absDirection.yCoord - origin.yCoord,
		        absDirection.zCoord - origin.zCoord);

		double maxLength = direction.lengthVector();
		double halfThickness = tileEntityCable.getCableThickness() / 2.0D;
		boolean hit = false;

		Vec3 intersection = Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);
		Direction intersectingDirection = getIntersection(
		        origin,
		        direction,
		        AxisAlignedBB.getAABBPool().getAABB(x + 0.5D - halfThickness,
		                y + 0.5D - halfThickness, z + 0.5D - halfThickness,
		                x + 0.5D + halfThickness, y + 0.5D + halfThickness,
		                z + 0.5D + halfThickness), intersection);

		if ((intersectingDirection != null)
		        && (intersection.distanceTo(origin) <= maxLength)) {
			hit = true;
		} else if (halfThickness < 0.5D) {
			int mask = 1;
			Direction[] directions = Direction.values();
			for (Direction dir : directions) {
				if ((tileEntityCable.connectivity & mask) == 0) {
					mask *= 2;
				} else {
					mask *= 2;

					AxisAlignedBB bbox = null;
					// dir
					switch (UUMHelper.convertArrayIntoArrList(
					        Direction.values()).indexOf(dir) + 1) {
						case 1:
							bbox = AxisAlignedBB.getAABBPool().getAABB(x,
							        y + 0.5D - halfThickness,
							        z + 0.5D - halfThickness, x + 0.5D,
							        y + 0.5D + halfThickness,
							        z + 0.5D + halfThickness);

							break;
						case 2:
							bbox = AxisAlignedBB.getAABBPool().getAABB(
							        x + 0.5D, y + 0.5D - halfThickness,
							        z + 0.5D - halfThickness, x + 1.0D,
							        y + 0.5D + halfThickness,
							        z + 0.5D + halfThickness);

							break;
						case 3:
							bbox = AxisAlignedBB.getAABBPool().getAABB(
							        x + 0.5D - halfThickness, y,
							        z + 0.5D - halfThickness,
							        x + 0.5D + halfThickness, y + 0.5D,
							        z + 0.5D + halfThickness);

							break;
						case 4:
							bbox = AxisAlignedBB.getAABBPool().getAABB(
							        x + 0.5D - halfThickness, y + 0.5D,
							        z + 0.5D - halfThickness,
							        x + 0.5D + halfThickness, y + 1.0D,
							        z + 0.5D + halfThickness);

							break;
						case 5:
							bbox = AxisAlignedBB.getAABBPool().getAABB(
							        x + 0.5D - halfThickness,
							        y + 0.5D - halfThickness, z,
							        x + 0.5D + halfThickness, y + 0.5D,
							        z + 0.5D);

							break;
						case 6:
							bbox = AxisAlignedBB.getAABBPool().getAABB(
							        x + 0.5D - halfThickness,
							        y + 0.5D - halfThickness, z + 0.5D,
							        x + 0.5D + halfThickness,
							        y + 0.5D + halfThickness, z + 1.0D);
					}

					intersectingDirection = getIntersection(origin, direction,
					        bbox, intersection);

					if ((intersectingDirection != null)
					        && (intersection.distanceTo(origin) <= maxLength)) {
						hit = true;
						break;
					}
				}
			}
		}
		if (hit) {
			return new MovingObjectPosition(x, y, z,
			        intersectingDirection.toSideValue(), intersection);
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	public boolean renderAsNormalBlock() {
		return false;
	}

	@SideOnly(Side.CLIENT)
	public int getRenderType() {
		return ClientProxy.CABLE_RENDER_ID;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	private static double[] getRay(Vec3 origin, Vec3 direction) {
		double[] ret = new double[6];

		ret[0] = (origin.xCoord * direction.yCoord - direction.xCoord
		        * origin.yCoord);
		ret[1] = (origin.xCoord * direction.zCoord - direction.xCoord
		        * origin.zCoord);
		ret[2] = (-direction.xCoord);
		ret[3] = (origin.yCoord * direction.zCoord - direction.yCoord
		        * origin.zCoord);
		ret[4] = (-direction.zCoord);
		ret[5] = direction.yCoord;

		return ret;
	}

	public static Direction intersects(Vec3 origin, Vec3 direction,
	        AxisAlignedBB bbox) {
		double[] ray = getRay(origin, direction);

		if ((direction.xCoord < 0.0D) && (direction.yCoord < 0.0D)
		        && (direction.zCoord < 0.0D)) {
			if (origin.xCoord < bbox.minX)
				return null;
			if (origin.yCoord < bbox.minY)
				return null;
			if (origin.zCoord < bbox.minZ)
				return null;
			if (side(ray, getEdgeRay(Edge.EF, bbox)) > 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.EH, bbox)) < 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.DH, bbox)) > 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.DC, bbox)) < 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.BC, bbox)) > 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.BF, bbox)) < 0.0D)
				return null;

			if ((side(ray, getEdgeRay(Edge.HG, bbox)) > 0.0D)
			        && (side(ray, getEdgeRay(Edge.FG, bbox)) < 0.0D))
				return Direction.ZP;
			if (side(ray, getEdgeRay(Edge.CG, bbox)) < 0.0D) {
				return Direction.YP;
			}
			return Direction.XP;
		}
		if ((direction.xCoord < 0.0D) && (direction.yCoord < 0.0D)
		        && (direction.zCoord >= 0.0D)) {
			if (origin.xCoord < bbox.minX)
				return null;
			if (origin.yCoord < bbox.minY)
				return null;
			if (origin.zCoord > bbox.maxZ)
				return null;
			if (side(ray, getEdgeRay(Edge.HG, bbox)) > 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.DH, bbox)) > 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.AD, bbox)) > 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.AB, bbox)) < 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.BF, bbox)) < 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.FG, bbox)) < 0.0D)
				return null;

			if ((side(ray, getEdgeRay(Edge.DC, bbox)) > 0.0D)
			        && (side(ray, getEdgeRay(Edge.CG, bbox)) > 0.0D))
				return Direction.XP;
			if (side(ray, getEdgeRay(Edge.BC, bbox)) < 0.0D) {
				return Direction.YP;
			}
			return Direction.ZN;
		}
		if ((direction.xCoord < 0.0D) && (direction.yCoord >= 0.0D)
		        && (direction.zCoord < 0.0D)) {
			if (origin.xCoord < bbox.minX)
				return null;
			if (origin.yCoord > bbox.maxY)
				return null;
			if (origin.zCoord < bbox.minZ)
				return null;
			if (side(ray, getEdgeRay(Edge.FG, bbox)) > 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.EF, bbox)) > 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.AE, bbox)) > 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.AD, bbox)) < 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.DC, bbox)) < 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.CG, bbox)) < 0.0D)
				return null;

			if ((side(ray, getEdgeRay(Edge.EH, bbox)) > 0.0D)
			        && (side(ray, getEdgeRay(Edge.HG, bbox)) > 0.0D))
				return Direction.ZP;
			if (side(ray, getEdgeRay(Edge.DH, bbox)) < 0.0D) {
				return Direction.XP;
			}
			return Direction.YN;
		}
		if ((direction.xCoord < 0.0D) && (direction.yCoord >= 0.0D)
		        && (direction.zCoord >= 0.0D)) {
			if (origin.xCoord < bbox.minX)
				return null;
			if (origin.yCoord > bbox.maxY)
				return null;
			if (origin.zCoord > bbox.maxZ)
				return null;
			if (side(ray, getEdgeRay(Edge.EH, bbox)) > 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.AE, bbox)) > 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.AB, bbox)) < 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.BC, bbox)) < 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.CG, bbox)) < 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.HG, bbox)) > 0.0D)
				return null;

			if ((side(ray, getEdgeRay(Edge.AD, bbox)) > 0.0D)
			        && (side(ray, getEdgeRay(Edge.DH, bbox)) > 0.0D))
				return Direction.YN;
			if (side(ray, getEdgeRay(Edge.DC, bbox)) < 0.0D) {
				return Direction.ZN;
			}
			return Direction.XP;
		}
		if ((direction.xCoord >= 0.0D) && (direction.yCoord < 0.0D)
		        && (direction.zCoord < 0.0D)) {
			if (origin.xCoord > bbox.maxX)
				return null;
			if (origin.yCoord < bbox.minY)
				return null;
			if (origin.zCoord < bbox.minZ)
				return null;
			if (side(ray, getEdgeRay(Edge.AB, bbox)) > 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.AE, bbox)) < 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.EH, bbox)) < 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.HG, bbox)) < 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.CG, bbox)) > 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.BC, bbox)) > 0.0D)
				return null;

			if ((side(ray, getEdgeRay(Edge.EF, bbox)) > 0.0D)
			        && (side(ray, getEdgeRay(Edge.BF, bbox)) < 0.0D))
				return Direction.XN;
			if (side(ray, getEdgeRay(Edge.FG, bbox)) < 0.0D) {
				return Direction.ZP;
			}
			return Direction.YP;
		}
		if ((direction.xCoord >= 0.0D) && (direction.yCoord < 0.0D)
		        && (direction.zCoord >= 0.0D)) {
			if (origin.xCoord > bbox.maxX)
				return null;
			if (origin.yCoord < bbox.minY)
				return null;
			if (origin.zCoord > bbox.maxZ)
				return null;
			if (side(ray, getEdgeRay(Edge.DC, bbox)) > 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.AD, bbox)) > 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.AE, bbox)) < 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.EF, bbox)) < 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.FG, bbox)) < 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.CG, bbox)) > 0.0D)
				return null;

			if ((side(ray, getEdgeRay(Edge.AB, bbox)) > 0.0D)
			        && (side(ray, getEdgeRay(Edge.BC, bbox)) > 0.0D))
				return Direction.ZN;
			if (side(ray, getEdgeRay(Edge.BF, bbox)) < 0.0D) {
				return Direction.XN;
			}
			return Direction.YP;
		}
		if ((direction.xCoord >= 0.0D) && (direction.yCoord >= 0.0D)
		        && (direction.zCoord < 0.0D)) {
			if (origin.xCoord > bbox.maxX)
				return null;
			if (origin.yCoord > bbox.maxY)
				return null;
			if (origin.zCoord < bbox.minZ)
				return null;
			if (side(ray, getEdgeRay(Edge.BF, bbox)) > 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.AB, bbox)) > 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.AD, bbox)) < 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.DH, bbox)) < 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.HG, bbox)) < 0.0D)
				return null;
			if (side(ray, getEdgeRay(Edge.FG, bbox)) > 0.0D)
				return null;

			if ((side(ray, getEdgeRay(Edge.AE, bbox)) > 0.0D)
			        && (side(ray, getEdgeRay(Edge.EF, bbox)) > 0.0D))
				return Direction.XN;
			if (side(ray, getEdgeRay(Edge.EH, bbox)) < 0.0D) {
				return Direction.YN;
			}
			return Direction.ZP;
		}

		if (origin.xCoord > bbox.maxX)
			return null;
		if (origin.yCoord > bbox.maxY)
			return null;
		if (origin.zCoord > bbox.maxZ)
			return null;
		if (side(ray, getEdgeRay(Edge.EF, bbox)) < 0.0D)
			return null;
		if (side(ray, getEdgeRay(Edge.EH, bbox)) > 0.0D)
			return null;
		if (side(ray, getEdgeRay(Edge.DH, bbox)) < 0.0D)
			return null;
		if (side(ray, getEdgeRay(Edge.DC, bbox)) > 0.0D)
			return null;
		if (side(ray, getEdgeRay(Edge.BC, bbox)) < 0.0D)
			return null;
		if (side(ray, getEdgeRay(Edge.BF, bbox)) > 0.0D)
			return null;

		if ((side(ray, getEdgeRay(Edge.AB, bbox)) < 0.0D)
		        && (side(ray, getEdgeRay(Edge.AE, bbox)) > 0.0D))
			return Direction.XN;
		if (side(ray, getEdgeRay(Edge.AD, bbox)) < 0.0D) {
			return Direction.ZN;
		}
		return Direction.YN;
	}

	private static double side(double[] ray1, double[] ray2) {
		return ray1[2] * ray2[3] + ray1[5] * ray2[1] + ray1[4] * ray2[0]
		        + ray1[1] * ray2[5] + ray1[0] * ray2[4] + ray1[3] * ray2[2];
	}

	public static <T> ArrayList<T> convertArrayIntoArrList(T[] arr) {
		ArrayList<T> arrList = new ArrayList<T>();
		for (T entry : arr) {
			arrList.add(entry);
		}
		return arrList;
	}

	private static double[] getEdgeRay(Edge edge, AxisAlignedBB bbox) {
		switch (convertArrayIntoArrList(edge.values()).indexOf(edge) + 1) {
			case 1:
				return new double[] { -bbox.minY, -bbox.minZ, -1.0D, 0.0D,
				        0.0D, 0.0D };
			case 2:
				return new double[] { bbox.minX, 0.0D, 0.0D, -bbox.minZ, 0.0D,
				        1.0D };
			case 3:
				return new double[] { 0.0D, bbox.minX, 0.0D, bbox.minY, -1.0D,
				        0.0D };
			case 4:
				return new double[] { bbox.maxX, 0.0D, 0.0D, -bbox.minZ, 0.0D,
				        1.0D };
			case 5:
				return new double[] { 0.0D, bbox.maxX, 0.0D, bbox.minY, -1.0D,
				        0.0D };
			case 6:
				return new double[] { -bbox.maxY, -bbox.minZ, -1.0D, 0.0D,
				        0.0D, 0.0D };
			case 7:
				return new double[] { 0.0D, bbox.minX, 0.0D, bbox.maxY, -1.0D,
				        0.0D };
			case 8:
				return new double[] { -bbox.minY, -bbox.maxZ, -1.0D, 0.0D,
				        0.0D, 0.0D };
			case 9:
				return new double[] { bbox.minX, 0.0D, 0.0D, -bbox.maxZ, 0.0D,
				        1.0D };
			case 10:
				return new double[] { 0.0D, bbox.maxX, 0.0D, bbox.maxY, -1.0D,
				        0.0D };
			case 11:
				return new double[] { -bbox.maxY, -bbox.maxZ, -1.0D, 0.0D,
				        0.0D, 0.0D };
			case 12:
				return new double[] { bbox.maxX, 0.0D, 0.0D, -bbox.maxZ, 0.0D,
				        1.0D };
		}
		return new double[0];
	}

	static enum Edge {
		AD, AB, AE, DC, DH, BC, BF, EH, EF, CG, FG, HG;
	}

	public static Direction getIntersection(Vec3 origin, Vec3 direction,
	        AxisAlignedBB bbox, Vec3 intersection) {
		double length = direction.lengthVector();

		Vec3 normalizedDirection = Vec3.createVectorHelper(direction.xCoord
		        / length, direction.yCoord / length, direction.zCoord / length);

		Direction intersectingDirection = intersects(origin,
		        normalizedDirection, bbox);
		if (intersectingDirection == null)
			return null;
		Vec3 planeOrigin;
		// Vec3 planeOrigin;
		if ((normalizedDirection.xCoord < 0.0D)
		        && (normalizedDirection.yCoord < 0.0D)
		        && (normalizedDirection.zCoord < 0.0D)) {
			planeOrigin = Vec3.createVectorHelper(bbox.maxX, bbox.maxY,
			        bbox.maxZ);
		} else {
			// Vec3 planeOrigin;
			if ((normalizedDirection.xCoord < 0.0D)
			        && (normalizedDirection.yCoord < 0.0D)
			        && (normalizedDirection.zCoord >= 0.0D)) {
				planeOrigin = Vec3.createVectorHelper(bbox.maxX, bbox.maxY,
				        bbox.minZ);
			} else {
				// Vec3 planeOrigin;
				if ((normalizedDirection.xCoord < 0.0D)
				        && (normalizedDirection.yCoord >= 0.0D)
				        && (normalizedDirection.zCoord < 0.0D)) {
					planeOrigin = Vec3.createVectorHelper(bbox.maxX, bbox.minY,
					        bbox.maxZ);
				} else {
					// Vec3 planeOrigin;
					if ((normalizedDirection.xCoord < 0.0D)
					        && (normalizedDirection.yCoord >= 0.0D)
					        && (normalizedDirection.zCoord >= 0.0D)) {
						planeOrigin = Vec3.createVectorHelper(bbox.maxX,
						        bbox.minY, bbox.minZ);
					} else {
						// Vec3 planeOrigin;
						if ((normalizedDirection.xCoord >= 0.0D)
						        && (normalizedDirection.yCoord < 0.0D)
						        && (normalizedDirection.zCoord < 0.0D)) {
							planeOrigin = Vec3.createVectorHelper(bbox.minX,
							        bbox.maxY, bbox.maxZ);
						} else {
							// Vec3 planeOrigin;
							if ((normalizedDirection.xCoord >= 0.0D)
							        && (normalizedDirection.yCoord < 0.0D)
							        && (normalizedDirection.zCoord >= 0.0D)) {
								planeOrigin = Vec3.createVectorHelper(
								        bbox.minX, bbox.maxY, bbox.minZ);
							} else {
								// Vec3 planeOrigin;
								if ((normalizedDirection.xCoord >= 0.0D)
								        && (normalizedDirection.yCoord >= 0.0D)
								        && (normalizedDirection.zCoord < 0.0D))
									planeOrigin = Vec3.createVectorHelper(
									        bbox.minX, bbox.minY, bbox.maxZ);
								else
									planeOrigin = Vec3.createVectorHelper(
									        bbox.minX, bbox.minY, bbox.minZ);
							}
						}
					}
				}
			}
		}
		Vec3 planeNormalVector = null;
		// intersectingDirection
		switch (convertArrayIntoArrList(Direction.values()).indexOf(
		        intersectingDirection) + 1) {
			case 1:
			case 2:
				planeNormalVector = Vec3.createVectorHelper(1.0D, 0.0D, 0.0D);
				break;
			case 3:
			case 4:
				planeNormalVector = Vec3.createVectorHelper(0.0D, 1.0D, 0.0D);
				break;
			case 5:
			case 6:
				planeNormalVector = Vec3.createVectorHelper(0.0D, 0.0D, 1.0D);
		}

		Vec3 newIntersection = getIntersectionWithPlane(origin,
		        normalizedDirection, planeOrigin, planeNormalVector);

		intersection.xCoord = newIntersection.xCoord;
		intersection.yCoord = newIntersection.yCoord;
		intersection.zCoord = newIntersection.zCoord;

		return intersectingDirection;
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x,
	        int y, int z, int meta) {
		double halfThickness = TileEntitySuperconductorCable
		        .getCableThickness(0);

		return AxisAlignedBB.getAABBPool().getAABB(x + 0.5D - halfThickness,
		        y + 0.5D - halfThickness, z + 0.5D - halfThickness,
		        x + 0.5D + halfThickness, y + 0.5D + halfThickness,
		        z + 0.5D + halfThickness);
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x,
	        int y, int z) {
		return getCommonBoundingBoxFromPool(world, x, y, z, false);
	}

	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x,
	        int y, int z) {
		return getCommonBoundingBoxFromPool(world, x, y, z, true);
	}

	public AxisAlignedBB getCommonBoundingBoxFromPool(World world, int x,
	        int y, int z, boolean selectionBoundingBox) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (!(te instanceof TileEntitySuperconductorCable))
			return getCollisionBoundingBoxFromPool(world, x, y, z, 3);

		TileEntitySuperconductorCable cable = (TileEntitySuperconductorCable) te;

		double halfThickness = cable.getCableThickness() / 2.0D;

		double minX = x + 0.5D - halfThickness;
		double minY = y + 0.5D - halfThickness;
		double minZ = z + 0.5D - halfThickness;
		double maxX = x + 0.5D + halfThickness;
		double maxY = y + 0.5D + halfThickness;
		double maxZ = z + 0.5D + halfThickness;

		if ((cable.connectivity & 0x1) != 0)
			minX = x;
		if ((cable.connectivity & 0x4) != 0)
			minY = y;
		if ((cable.connectivity & 0x10) != 0)
			minZ = z;
		if ((cable.connectivity & 0x2) != 0)
			maxX = x + 1;
		if ((cable.connectivity & 0x8) != 0)
			maxY = y + 1;
		if ((cable.connectivity & 0x20) != 0)
			maxZ = z + 1;

		return AxisAlignedBB.getAABBPool().getAABB(minX, minY, minZ, maxX,
		        maxY, maxZ);
	}

	public boolean isBlockNormalCube(World world, int x, int y, int z) {
		return false;
	}

	private static Vec3 getIntersectionWithPlane(Vec3 origin, Vec3 direction,
	        Vec3 planeOrigin, Vec3 planeNormalVector) {
		double distance = getDistanceToPlane(origin, direction, planeOrigin,
		        planeNormalVector);

		return Vec3.createVectorHelper(origin.xCoord + direction.xCoord
		        * distance, origin.yCoord + direction.yCoord * distance,
		        origin.zCoord + direction.zCoord * distance);
	}

	private static double getDistanceToPlane(Vec3 origin, Vec3 direction,
	        Vec3 planeOrigin, Vec3 planeNormalVector) {
		Vec3 base = Vec3.createVectorHelper(planeOrigin.xCoord - origin.xCoord,
		        planeOrigin.yCoord - origin.yCoord, planeOrigin.zCoord
		                - origin.zCoord);

		return dotProduct(base, planeNormalVector)
		        / dotProduct(direction, planeNormalVector);
	}

	private static double dotProduct(Vec3 a, Vec3 b) {
		return a.xCoord * b.xCoord + a.yCoord * b.yCoord + a.zCoord * b.zCoord;
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntitySuperconductorCable();
	}

}