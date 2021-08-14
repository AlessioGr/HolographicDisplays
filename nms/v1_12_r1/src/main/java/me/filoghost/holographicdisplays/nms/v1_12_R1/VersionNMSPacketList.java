/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.nms.v1_12_R1;

import me.filoghost.holographicdisplays.common.nms.AbstractNMSPacketList;
import me.filoghost.holographicdisplays.common.nms.EntityID;
import me.filoghost.holographicdisplays.common.nms.IndividualCustomName;
import me.filoghost.holographicdisplays.common.nms.IndividualNMSPacket;
import org.bukkit.inventory.ItemStack;

class VersionNMSPacketList extends AbstractNMSPacketList {

    @Override
    public void addArmorStandSpawnPackets(EntityID entityID, double positionX, double positionY, double positionZ) {
        System.out.println("Sending armor stand spawn packet");
        add(new EntitySpawnNMSPacket(entityID, EntityTypeID.ARMOR_STAND, positionX, positionY, positionZ));
        add(EntityMetadataNMSPacket.builder(entityID)
                .setArmorStandMarker()
                .build()
        );
    }

    @Override
    public void addArmorStandSpawnPackets(EntityID entityID, double positionX, double positionY, double positionZ, String customName) {
        System.out.println("Sending armor stand spawn packet");
        add(new EntitySpawnNMSPacket(entityID, EntityTypeID.ARMOR_STAND, positionX, positionY, positionZ));
        add(EntityMetadataNMSPacket.builder(entityID)
                .setArmorStandMarker()
                .setCustomName(customName)
                .build()
        );
    }

    @Override
    public void addArmorStandSpawnPackets(EntityID entityID, double positionX, double positionY, double positionZ, IndividualCustomName individualCustomName) {
        System.out.println("Sending armor stand spawn packet");
        add(new EntitySpawnNMSPacket(entityID, EntityTypeID.ARMOR_STAND, positionX, positionY, positionZ));
        add(new IndividualNMSPacket(player -> EntityMetadataNMSPacket.builder(entityID)
                .setArmorStandMarker()
                .setCustomName(individualCustomName.get(player))
                .build()
        ));
    }

    @Override
    public void addArmorStandNameChangePackets(EntityID entityID, String customName) {
        add(EntityMetadataNMSPacket.builder(entityID)
                .setCustomName(customName)
                .build()
        );
    }

    @Override
    public void addArmorStandNameChangePackets(EntityID entityID, IndividualCustomName individualCustomName) {
        add(new IndividualNMSPacket(player -> EntityMetadataNMSPacket.builder(entityID)
                .setCustomName(individualCustomName.get(player))
                .build()
        ));
    }

    @Override
    public void addItemSpawnPackets(EntityID entityID, double positionX, double positionY, double positionZ, ItemStack itemStack) {
        add(new EntitySpawnNMSPacket(entityID, EntityTypeID.ITEM, positionX, positionY, positionZ));
        add(EntityMetadataNMSPacket.builder(entityID)
                .setItemStack(itemStack)
                .build()
        );
    }

    @Override
    public void addItemStackChangePackets(EntityID entityID, ItemStack itemStack) {
        add(EntityMetadataNMSPacket.builder(entityID)
                .setItemStack(itemStack)
                .build()
        );
    }

    @Override
    public void addSlimeSpawnPackets(EntityID entityID, double positionX, double positionY, double positionZ) {
        add(EntityLivingSpawnNMSPacket.builder(entityID, EntityTypeID.SLIME, positionX, positionY, positionZ)
                .setInvisible()
                .setSlimeSmall() // Required for a correct client-side collision box
                .build()
        );
    }

    @Override
    public void addEntityDestroyPackets(EntityID... entityIDs) {
        System.out.println("Sending destroy packet");
        add(new EntityDestroyNMSPacket(entityIDs));
    }

    @Override
    public void addTeleportPackets(EntityID entityID, double positionX, double positionY, double positionZ) {
        add(new EntityTeleportNMSPacket(entityID, positionX, positionY, positionZ));
    }

    @Override
    public void addMountPackets(EntityID vehicleEntityID, EntityID passengerEntityID) {
        add(new EntityMountNMSPacket(vehicleEntityID, passengerEntityID));
    }

}
