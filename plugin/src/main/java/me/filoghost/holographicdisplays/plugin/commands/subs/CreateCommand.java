/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.plugin.commands.subs;

import me.filoghost.fcommons.Strings;
import me.filoghost.fcommons.command.sub.SubCommandContext;
import me.filoghost.fcommons.command.validation.CommandException;
import me.filoghost.fcommons.command.validation.CommandValidate;
import me.filoghost.holographicdisplays.plugin.Colors;
import me.filoghost.holographicdisplays.plugin.commands.HologramCommandValidate;
import me.filoghost.holographicdisplays.plugin.commands.HologramSubCommand;
import me.filoghost.holographicdisplays.plugin.disk.ConfigManager;
import me.filoghost.holographicdisplays.plugin.object.internal.InternalHologram;
import me.filoghost.holographicdisplays.plugin.object.internal.InternalHologramLine;
import me.filoghost.holographicdisplays.plugin.object.internal.InternalHologramManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class CreateCommand extends HologramSubCommand {

    private final InternalHologramManager internalHologramManager;
    private final ConfigManager configManager;

    public CreateCommand(InternalHologramManager internalHologramManager, ConfigManager configManager) {
        super("create");
        setMinArgs(1);
        setUsageArgs("<hologramName> [text]");
        setDescription(
                "Creates a new hologram with the given name, that must",
                "be alphanumeric. The name will be used as reference to",
                "that hologram for editing commands.");

        this.internalHologramManager = internalHologramManager;
        this.configManager = configManager;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void execute(CommandSender sender, String[] args, SubCommandContext context) throws CommandException {
        Player player = CommandValidate.getPlayerSender(sender);
        String hologramName = args[0];

        CommandValidate.check(hologramName.matches("[a-zA-Z0-9_\\-]+"), 
                "The name must contain only alphanumeric chars, underscores and hyphens.");
        CommandValidate.check(!internalHologramManager.isExistingHologram(hologramName), 
                "A hologram with that name already exists.");

        Location spawnLoc = player.getLocation();
        boolean moveUp = player.isOnGround();

        if (moveUp) {
            spawnLoc.add(0.0, 1.2, 0.0);
        }
        
        InternalHologram hologram = internalHologramManager.createHologram(spawnLoc, hologramName);
        InternalHologramLine line;

        if (args.length > 1) {
            String text = Strings.joinFrom(" ", args, 1);
            CommandValidate.check(!text.equalsIgnoreCase("{empty}"), "The first line should not be empty.");
            
            line = HologramCommandValidate.parseHologramLine(hologram, text);
            player.sendMessage(Colors.SECONDARY_SHADOW + "(Change the lines with /" + context.getRootLabel() 
                    + " edit " + hologram.getName() + ")");
        } else {
            String defaultText = "Default hologram. Change it with " 
                    + Colors.PRIMARY + "/" + context.getRootLabel() + " edit " + hologram.getName();
            line = hologram.createTextLine(defaultText, defaultText.replace(ChatColor.COLOR_CHAR, '&'));
        }
        
        hologram.addLine(line);
        
        configManager.saveHologramDatabase(internalHologramManager);
        Location look = player.getLocation();
        look.setPitch(90);
        player.teleport(look, TeleportCause.PLUGIN);
        player.sendMessage(Colors.PRIMARY + "You created a hologram named '" + hologram.getName() + "'.");

        if (moveUp) {
            player.sendMessage(Colors.SECONDARY_SHADOW + "(You were on the ground," 
                    + " the hologram was automatically moved up." 
                    + " If you use /" + context.getRootLabel() + " movehere " + hologram.getName() + "," 
                    + " the hologram will be moved to your feet)");
        }
    }
    
}