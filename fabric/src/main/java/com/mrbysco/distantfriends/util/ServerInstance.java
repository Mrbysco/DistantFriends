package com.mrbysco.distantfriends.util;

import net.minecraft.server.MinecraftServer;

public class ServerInstance {
	private static MinecraftServer server = null;

	public static MinecraftServer getServer() {
		return ServerInstance.server;
	}

	public static void setServer(MinecraftServer server) {
		ServerInstance.server = server;
	}
}
