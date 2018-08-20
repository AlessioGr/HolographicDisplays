package com.gmail.filoghost.holographicdisplays.nms.v1_7_R3;

import java.lang.reflect.Method;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftEntity;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;

import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import com.gmail.filoghost.holographicdisplays.nms.interfaces.FancyMessage;
import com.gmail.filoghost.holographicdisplays.nms.interfaces.ItemPickupManager;
import com.gmail.filoghost.holographicdisplays.nms.interfaces.NMSManager;
import com.gmail.filoghost.holographicdisplays.nms.interfaces.entity.NMSArmorStand;
import com.gmail.filoghost.holographicdisplays.nms.interfaces.entity.NMSEntityBase;
import com.gmail.filoghost.holographicdisplays.nms.interfaces.entity.NMSHorse;
import com.gmail.filoghost.holographicdisplays.nms.interfaces.entity.NMSItem;
import com.gmail.filoghost.holographicdisplays.nms.interfaces.entity.NMSWitherSkull;
import com.gmail.filoghost.holographicdisplays.util.DebugHandler;
import com.gmail.filoghost.holographicdisplays.util.ReflectionUtils;
import com.gmail.filoghost.holographicdisplays.util.Validator;
import com.gmail.filoghost.holographicdisplays.util.VersionUtils;

import net.minecraft.server.v1_7_R3.Entity;
import net.minecraft.server.v1_7_R3.EntityTypes;
import net.minecraft.server.v1_7_R3.MathHelper;
import net.minecraft.server.v1_7_R3.World;
import net.minecraft.server.v1_7_R3.WorldServer;

public class NmsManagerImpl implements NMSManager {

	private Method validateEntityMethod;

	@Override
	public void setup() throws Exception {
		registerCustomEntity(EntityNMSHorse.class, "EntityHorse", 100);
		registerCustomEntity(EntityNMSWitherSkull.class, "WitherSkull", 19);
		registerCustomEntity(EntityNMSItem.class, "Item", 1);
		registerCustomEntity(EntityNMSSlime.class, "Slime", 55);
		
		if (!VersionUtils.isForgeServer()) {
			validateEntityMethod = World.class.getDeclaredMethod("a", Entity.class);
			validateEntityMethod.setAccessible(true);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void registerCustomEntity(Class entityClass, String name, int id) throws Exception {
		if (VersionUtils.isForgeServer()) {
			// Forge entity registration.
			Class<?> entityTypesClass = Class.forName("net.minecraft.server.v1_7_R3.EntityTypes");
			ReflectionUtils.putInPrivateStaticMap(entityTypesClass, "field_75626_c", entityClass, name);
			ReflectionUtils.putInPrivateStaticMap(entityTypesClass, "field_75624_e", entityClass, Integer.valueOf(id));
		} else {
			// Normal entity registration.
			ReflectionUtils.putInPrivateStaticMap(EntityTypes.class, "d", entityClass, name);
			ReflectionUtils.putInPrivateStaticMap(EntityTypes.class, "f", entityClass, Integer.valueOf(id));
		}
	}
	
	@Override
	public NMSHorse spawnNMSHorse(org.bukkit.World world, double x, double y, double z, HologramLine parentPiece) {
		WorldServer nmsWorld = ((CraftWorld) world).getHandle();
		EntityNMSHorse invisibleHorse = new EntityNMSHorse(nmsWorld, parentPiece);
		invisibleHorse.setLocationNMS(x, y, z);
		if (!addEntityToWorld(nmsWorld, invisibleHorse)) {
			DebugHandler.handleSpawnFail(parentPiece);
		}
		return invisibleHorse;
	}
	
	@Override
	public NMSWitherSkull spawnNMSWitherSkull(org.bukkit.World bukkitWorld, double x, double y, double z, HologramLine parentPiece) {
		WorldServer nmsWorld = ((CraftWorld) bukkitWorld).getHandle();
		EntityNMSWitherSkull staticWitherSkull = new EntityNMSWitherSkull(nmsWorld, parentPiece);
		staticWitherSkull.setLocationNMS(x, y, z);
		if (!addEntityToWorld(nmsWorld, staticWitherSkull)) {
			DebugHandler.handleSpawnFail(parentPiece);
		}
		return staticWitherSkull;
	}
	
	@Override
	public NMSItem spawnNMSItem(org.bukkit.World bukkitWorld, double x, double y, double z, ItemLine parentPiece, ItemStack stack, ItemPickupManager itemPickupManager) {
		WorldServer nmsWorld = ((CraftWorld) bukkitWorld).getHandle();
		EntityNMSItem customItem = new EntityNMSItem(nmsWorld, parentPiece, itemPickupManager);
		customItem.setLocationNMS(x, y, z);
		customItem.setItemStackNMS(stack);
		if (!addEntityToWorld(nmsWorld, customItem)) {
			DebugHandler.handleSpawnFail(parentPiece);
		}
		return customItem;
	}
	
	@Override
	public EntityNMSSlime spawnNMSSlime(org.bukkit.World bukkitWorld, double x, double y, double z, HologramLine parentPiece) {
		WorldServer nmsWorld = ((CraftWorld) bukkitWorld).getHandle();
		EntityNMSSlime touchSlime = new EntityNMSSlime(nmsWorld, parentPiece);
		touchSlime.setLocationNMS(x, y, z);
		if (!addEntityToWorld(nmsWorld, touchSlime)) {
			DebugHandler.handleSpawnFail(parentPiece);
		}
		return touchSlime;
	}
	
	@SuppressWarnings("unchecked")
	private boolean addEntityToWorld(WorldServer nmsWorld, Entity nmsEntity) {
		Validator.isTrue(Bukkit.isPrimaryThread(), "Async entity add");
		
		if (validateEntityMethod == null) {
			return nmsWorld.addEntity(nmsEntity, SpawnReason.CUSTOM);
		}
		
        final int chunkX = MathHelper.floor(nmsEntity.locX / 16.0);
        final int chunkZ = MathHelper.floor(nmsEntity.locZ / 16.0);
        
        if (!nmsWorld.chunkProviderServer.isChunkLoaded(chunkX, chunkZ)) {
        	// This should never happen
            nmsEntity.dead = true;
            return false;
        }
        
        nmsWorld.getChunkAt(chunkX, chunkZ).a(nmsEntity);
        nmsWorld.entityList.add(nmsEntity);
        
        try {
			validateEntityMethod.invoke(nmsWorld, nmsEntity);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
        return true;
    }
	
	@Override
	public boolean isNMSEntityBase(org.bukkit.entity.Entity bukkitEntity) {
		return ((CraftEntity) bukkitEntity).getHandle() instanceof NMSEntityBase;
	}

	@Override
	public NMSEntityBase getNMSEntityBase(org.bukkit.entity.Entity bukkitEntity) {
		
		Entity nmsEntity = ((CraftEntity) bukkitEntity).getHandle();
		if (nmsEntity instanceof NMSEntityBase) {
			return ((NMSEntityBase) nmsEntity);
		}

		return null;
	}

	@Override
	public FancyMessage newFancyMessage(String text) {
		return new FancyMessageImpl(text);
	}

	@Override
	public NMSArmorStand spawnNMSArmorStand(org.bukkit.World world, double x, double y, double z, HologramLine parentPiece) {
		throw new NotImplementedException("Method can only be used on 1.8 or higher");
	}

	@Override
	public boolean isUnloadUnsure(Chunk bukkitChunk) {
		return bukkitChunk.getWorld().isChunkInUse(bukkitChunk.getX(), bukkitChunk.getZ());
	}
	
}