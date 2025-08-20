package com.mrbysco.distantfriends;

import org.jetbrains.annotations.Nullable;

public record PlayerData(String name, @Nullable String texture) {
	public PlayerData(String name) {
		this(name, null);
	}
}
