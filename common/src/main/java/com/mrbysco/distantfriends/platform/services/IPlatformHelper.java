package com.mrbysco.distantfriends.platform.services;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.decoration.Mannequin;

import java.util.List;

public interface IPlatformHelper {

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

}
