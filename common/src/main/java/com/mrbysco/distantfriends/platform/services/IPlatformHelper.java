package com.mrbysco.distantfriends.platform.services;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.server.MinecraftServer;

import java.util.List;
import java.util.Optional;

public interface IPlatformHelper {

	/**
	 * Gets the Game Profile Serializer
	 * @return The Game Profile Serializer
	 */
	EntityDataSerializer<Optional<GameProfile>> getGameProfileSerializer();

	/**
	 * Get configured friends
	 * @return A list of users who can be chosen when it spawns a distant friend
	 */
	List<? extends String> getFriends();

	/**
	 * Get configured value for addWhitelistPlayers
	 * @return Add the players from the whitelist to the Friends list
	 */
	boolean addWhitelistPlayers();

	/**
	 * Get configured value for playerMobsCompat
	 * @return Add players from a Player Mobs whitelist to the Friends list
	 */
	boolean playerMobsCompat();

	/**
	 * Get configured playerMobsNameLinks
	 * @return The player mobs Name Links
	 */
	List<? extends String> getPlayerMobsNameLinks();

	/**
	 * Get the server instance
	 * @return The server instance
	 */
	MinecraftServer getServer();
}
