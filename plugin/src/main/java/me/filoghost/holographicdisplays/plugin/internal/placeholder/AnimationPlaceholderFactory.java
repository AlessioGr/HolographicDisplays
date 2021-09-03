/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.plugin.internal.placeholder;

import me.filoghost.fcommons.collection.CaseInsensitiveHashMap;
import me.filoghost.fcommons.collection.CaseInsensitiveMap;
import me.filoghost.holographicdisplays.api.placeholder.global.GlobalPlaceholder;
import me.filoghost.holographicdisplays.api.placeholder.global.GlobalPlaceholderFactory;

import java.util.Map;

public class AnimationPlaceholderFactory implements GlobalPlaceholderFactory {

    private final CaseInsensitiveMap<GlobalPlaceholder> animationsByFileName;

    public AnimationPlaceholderFactory(Map<String, AnimationPlaceholder> animationsByFileName) {
        this.animationsByFileName = new CaseInsensitiveHashMap<>();
        this.animationsByFileName.putAllString(animationsByFileName);
    }

    @Override
    public GlobalPlaceholder getPlaceholder(String fileNameArgument) {
        GlobalPlaceholder placeholder = animationsByFileName.get(fileNameArgument);
        if (placeholder != null) {
            return placeholder;
        } else {
            return new ImmutablePlaceholder("[Animation not found: " + fileNameArgument + "]");
        }
    }

}
