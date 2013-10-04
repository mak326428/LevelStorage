package makmods.levelstorage.logic.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet41EntityEffect;
import net.minecraft.network.packet.Packet43Experience;
import net.minecraft.network.packet.Packet9Respawn;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class TranslocationHelper {
	public static Entity teleportEntity(World newworld, Entity entity,
			int dimension, ChunkCoordinates spawn, float yaw) {
		Entity mount = entity.ridingEntity;
		if (entity.ridingEntity != null) {
			entity.mountEntity(null);
			mount = teleportEntity(newworld, mount, dimension, spawn, yaw);
		}
		boolean changingworlds = entity.worldObj != newworld;
		entity.worldObj.updateEntityWithOptionalForce(entity, false);
		if ((entity instanceof EntityPlayerMP)) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			player.closeScreen();
			if (changingworlds) {
				player.dimension = dimension;
				player.playerNetServerHandler
						.sendPacketToPlayer(new Packet9Respawn(
								player.dimension,
								(byte) player.worldObj.difficultySetting,
								newworld.getWorldInfo().getTerrainType(),
								newworld.getHeight(),
								player.theItemInWorldManager.getGameType()));
				((WorldServer) entity.worldObj).getPlayerManager()
						.removePlayer(player);
			}
		}
		if (changingworlds) {
			removeEntityFromWorld(entity.worldObj, entity);
		}

		entity.setLocationAndAngles(spawn.posX + 0.5D, spawn.posY,
				spawn.posZ + 0.5D, yaw, entity.rotationPitch);
		((WorldServer) newworld).theChunkProviderServer.loadChunk(
				spawn.posX >> 4, spawn.posZ >> 4);
		while (getCollidingWorldGeometry(newworld, entity.boundingBox, entity)
				.size() != 0) {
			spawn.posY += 1;
			entity.setPosition(spawn.posX + 0.5D, spawn.posY, spawn.posZ + 0.5D);
		}
		if (changingworlds) {
			if (!(entity instanceof EntityPlayer)) {
				NBTTagCompound entityNBT = new NBTTagCompound();
				entity.isDead = false;
				// entity.addEntityID(entityNBT);
				entity.isDead = true;
				entity = EntityList.createEntityFromNBT(entityNBT, newworld);
				if (entity == null)
					return null;
				entity.dimension = newworld.provider.dimensionId;
			}
			newworld.spawnEntityInWorld(entity);
			entity.setWorld(newworld);
		}
		entity.setLocationAndAngles(spawn.posX + 0.5D, spawn.posY,
				spawn.posZ + 0.5D, yaw, entity.rotationPitch);
		newworld.updateEntityWithOptionalForce(entity, false);
		entity.setLocationAndAngles(spawn.posX + 0.5D, spawn.posY,
				spawn.posZ + 0.5D, yaw, entity.rotationPitch);
		if ((entity instanceof EntityPlayerMP)) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			if (changingworlds)
				player.mcServer.getConfigurationManager().func_72375_a(player,
						(WorldServer) newworld);
			player.playerNetServerHandler.setPlayerLocation(spawn.posX + 0.5D,
					spawn.posY, spawn.posZ + 0.5D, player.rotationYaw,
					player.rotationPitch);
		}
		newworld.updateEntityWithOptionalForce(entity, false);
		if (((entity instanceof EntityPlayerMP)) && (changingworlds)) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			player.theItemInWorldManager.setWorld((WorldServer) newworld);
			player.mcServer.getConfigurationManager()
					.updateTimeAndWeatherForPlayer(player,
							(WorldServer) newworld);
			player.mcServer.getConfigurationManager().syncPlayerInventory(
					player);
			Iterator iter = player.getActivePotionEffects().iterator();

			while (iter.hasNext()) {
				PotionEffect effect = (PotionEffect) iter.next();
				player.playerNetServerHandler
						.sendPacketToPlayer(new Packet41EntityEffect(
								player.entityId, effect));
			}
			player.playerNetServerHandler
					.sendPacketToPlayer(new Packet43Experience(
							player.experience, player.experienceTotal,
							player.experienceLevel));
		}
		entity.setLocationAndAngles(spawn.posX + 0.5D, spawn.posY,
				spawn.posZ + 0.5D, yaw, entity.rotationPitch);
		if (mount != null) {
			if ((entity instanceof EntityPlayerMP)) {
				newworld.updateEntityWithOptionalForce(entity, true);
			}
			entity.mountEntity(mount);
		}
		return entity;
	}

	private static void removeEntityFromWorld(World world, Entity entity) {
		if ((entity instanceof EntityPlayer)) {
			EntityPlayer player = (EntityPlayer) entity;
			player.closeScreen();
			world.playerEntities.remove(player);
			world.updateAllPlayersSleepingFlag();
			int i = entity.chunkCoordX;
			int j = entity.chunkCoordZ;
			if ((entity.addedToChunk)
					&& (world.getChunkProvider().chunkExists(i, j))) {
				world.getChunkFromChunkCoords(i, j).removeEntity(entity);
				world.getChunkFromChunkCoords(i, j).isModified = true;
			}
			world.loadedEntityList.remove(entity);
			world.onEntityRemoved(entity);
		}
		entity.isDead = false;
	}

	private static List getCollidingWorldGeometry(World world,
			AxisAlignedBB axisalignedbb, Entity entity) {
		ArrayList collidingBoundingBoxes = new ArrayList();
		int i = MathHelper.floor_double(axisalignedbb.minX);
		int j = MathHelper.floor_double(axisalignedbb.maxX + 1.0D);
		int k = MathHelper.floor_double(axisalignedbb.minY);
		int l = MathHelper.floor_double(axisalignedbb.maxY + 1.0D);
		int i1 = MathHelper.floor_double(axisalignedbb.minZ);
		int j1 = MathHelper.floor_double(axisalignedbb.maxZ + 1.0D);
		for (int k1 = i; k1 < j; k1++) {
			for (int l1 = i1; l1 < j1; l1++) {
				if (world.blockExists(k1, 64, l1)) {
					for (int i2 = k - 1; i2 < l; i2++) {
						Block block = Block.blocksList[world.getBlockId(k1, i2,
								l1)];
						if (block != null)
							block.addCollisionBoxesToList(world, k1, i2, l1,
									axisalignedbb, collidingBoundingBoxes,
									entity);
					}
				}
			}
		}
		return collidingBoundingBoxes;
	}
}
