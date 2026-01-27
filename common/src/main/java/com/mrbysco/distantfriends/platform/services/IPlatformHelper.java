package com.mrbysco.distantfriends.platform.services;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.decoration.Mannequin;

import java.util.List;

public interface IPlatformHelper {

	/**
	 * Get configured friends
	 *
	 * @return A list of users who can be chosen when it spawns a distant friend
	 */
	List<? extends String> getFriends();

	/**
	 * Get configured value for addWhitelistPlayers
	 *
	 * @return Add the players from the whitelist to the Friends list
	 */
	boolean addWhitelistPlayers();

	/**
	 * Get configured value for playerMobsCompat
	 *
	 * @return Add players from a Player Mobs whitelist to the Friends list
	 */
	boolean playerMobsCompat();

	/**
	 * Get configured playerMobsNameLinks
	 *
	 * @return The player mobs Name Links
	 */
	List<? extends String> getPlayerMobsNameLinks();

	/**
	 * Get configured value for showName
	 *
	 * @return Show the name of the friend above their head
	 */
	boolean showName();

	/**
	 * Get the server instance
	 *
	 * @return The server instance
	 */
	MinecraftServer getServer();

	/**
	 * Attach data to the spawned friend to identify it as a distant friend
	 *
	 * @param friend The spawned friend
	 */
	void attachFriendData(Mannequin friend);

	/**
	 * Check if there is a friend nearby within the given range
	 *
	 * @param serverLevel     the server level
	 * @param pos             the position to check around
	 * @param horizontalRange the horizontal range to check
	 * @param verticalRange   the vertical range to check
	 * @return A list of nearby friends within the given range
	 */
	List<? extends Mannequin> getNearbyFriends(ServerLevel serverLevel, BlockPos pos, int horizontalRange, int verticalRange);

	/**
	 * Check if the dimension is allowed for spawning friends
	 *
	 * @param dimension the dimension to check
	 * @return true if the dimension is allowed, false otherwise
	 */
	boolean isDimensionAllowed(Identifier dimension);
}
