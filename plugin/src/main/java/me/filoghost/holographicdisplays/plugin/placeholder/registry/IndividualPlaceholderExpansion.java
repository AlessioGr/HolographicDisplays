/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.plugin.placeholder.registry;

import me.filoghost.holographicdisplays.api.placeholder.individual.IndividualPlaceholder;
import me.filoghost.holographicdisplays.api.placeholder.individual.IndividualPlaceholderFactory;
import me.filoghost.holographicdisplays.plugin.placeholder.PlaceholderException;
import me.filoghost.holographicdisplays.plugin.placeholder.StandardPlaceholder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class IndividualPlaceholderExpansion extends PlaceholderExpansion {

    private final IndividualPlaceholderFactory placeholderFactory;

    IndividualPlaceholderExpansion(Plugin plugin, String identifier, IndividualPlaceholderFactory placeholderFactory) {
        super(plugin, identifier);
        this.placeholderFactory = placeholderFactory;
    }

    @Override
    public boolean isIndividual() {
        return true;
    }

    @Override
    public @Nullable StandardPlaceholder createPlaceholder(String argument) throws PlaceholderException {
        IndividualPlaceholder placeholder;
        try {
            placeholder = placeholderFactory.getPlaceholder(argument);
        } catch (Throwable t) {
            throw new PlaceholderException(t, this);
        }

        if (placeholder != null) {
            return new IndividualStandardPlaceholder(placeholder, this);
        } else {
            return null;
        }
    }


    private static class IndividualStandardPlaceholder extends StandardPlaceholder {

        private final @NotNull IndividualPlaceholder placeholder;

        IndividualStandardPlaceholder(@NotNull IndividualPlaceholder placeholder, @NotNull IndividualPlaceholderExpansion source) {
            super(source);
            this.placeholder = placeholder;
        }

        @Override
        protected boolean externalRequiresUpdate(long currentTick, long lastUpdateTick) {
            return placeholder.requiresUpdate(currentTick, lastUpdateTick);
        }

        @Override
        protected @Nullable String externalGetReplacement(Player player, @Nullable String argument) {
            return placeholder.getReplacement(player, argument);
        }

    }

}
