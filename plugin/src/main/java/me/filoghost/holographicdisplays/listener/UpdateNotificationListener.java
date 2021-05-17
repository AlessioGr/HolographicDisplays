/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.listener;

import me.filoghost.fcommons.logging.Log;
import me.filoghost.holographicdisplays.Colors;
import me.filoghost.holographicdisplays.HolographicDisplays;
import me.filoghost.holographicdisplays.Permissions;
import me.filoghost.holographicdisplays.disk.Configuration;
import me.filoghost.updatechecker.UpdateChecker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateNotificationListener implements Listener {

    // The new version found by the updater, null if there is no new version
    private String newVersion;

    public void runAsyncUpdateCheck() {
        if (Configuration.updateNotification) {
            UpdateChecker.run(HolographicDisplays.getInstance(), 75097, newVersion -> {
                this.newVersion = newVersion;
                Log.info("Found a new version available: " + newVersion);
                Log.info("Download it on Bukkit Dev:");
                Log.info("dev.bukkit.org/projects/holographic-displays");
            });
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (Configuration.updateNotification && newVersion != null) {
            Player player = event.getPlayer();
            
            if (player.hasPermission(Permissions.COMMAND_BASE + "update")) {
                player.sendMessage(Colors.PRIMARY_SHADOW + "[HolographicDisplays] " 
                        + Colors.PRIMARY + "Found an update: " + newVersion + ". Download:");
                player.sendMessage(Colors.PRIMARY_SHADOW + ">> " 
                        + Colors.PRIMARY + "https://dev.bukkit.org/projects/holographic-displays");
            }
        }
    }

}
