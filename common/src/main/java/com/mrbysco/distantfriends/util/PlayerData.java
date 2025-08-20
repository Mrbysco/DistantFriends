package com.mrbysco.distantfriends.util;

import org.jetbrains.annotations.Nullable;

public record PlayerData(String name, @Nullable String texture) {
	public PlayerData(String name) {
		this(name, null);
	}
}
