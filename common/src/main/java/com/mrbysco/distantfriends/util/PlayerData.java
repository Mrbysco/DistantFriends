package com.mrbysco.distantfriends.util;

import net.minecraft.world.entity.player.PlayerModelType;
import org.jetbrains.annotations.Nullable;

public record PlayerData(String name, @Nullable String texture, @Nullable PlayerModelType bodyType) {
	public PlayerData(String name) {
		this(name, null, null);
	}
}
