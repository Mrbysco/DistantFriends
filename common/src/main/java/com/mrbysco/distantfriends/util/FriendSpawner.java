package com.mrbysco.distantfriends.util;

import com.mrbysco.distantfriends.CommonClass;
import com.mrbysco.distantfriends.config.FriendConfig;
import com.mrbysco.distantfriends.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.decoration.Mannequin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Custom spawner for Distant Friends (Mannequins)
 */
public class FriendSpawner implements CustomSpawner {
	private static final int NUMBER_OF_SPAWN_ATTEMPTS = 5;
	private static final int TICK_DELAY = 1200;
	private int nextTick;

	@Override
	public void tick(ServerLevel level, boolean spawnEnemies) {
		this.nextTick--;
		if (this.nextTick <= 0) {
			this.nextTick = TICK_DELAY;
			Player player = level.getRandomPlayer();
			if (player != null && FriendConfig.COMMON.spawnDimensions.get().contains(level.dimension().identifier().toDebugFileName())) {
				RandomSource randomsource = level.getRandom();
				BlockPos playerPos = player.blockPosition();
				for (int attempt = 0; attempt < NUMBER_OF_SPAWN_ATTEMPTS; attempt++) {
					int x = playerPos.getX() + randomsource.nextInt(48 * 2) - 48;
					int z = playerPos.getZ() + randomsource.nextInt(48 * 2) - 48;
					int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
					BlockPos blockpos = new BlockPos(x, y, z);
					if (SpawnPlacementTypes.ON_GROUND.isSpawnPositionOk(level, blockpos, EntityTypes.MANNEQUIN) && noneNearby(level, blockpos) &&
							isDarkEnoughToSpawn(level, blockpos, randomsource)) {
						this.spawnFriend(blockpos, level);
					}
				}
			}
		}
	}

	/**
	 * Check if the light level is low enough to spawn a Distant Friend
	 *
	 * @param levelAccessor the level
	 * @param pos           the position to check
	 * @param random        the random source
	 * @return true if the light level is low enough, false otherwise
	 */
	private boolean isDarkEnoughToSpawn(ServerLevelAccessor levelAccessor, BlockPos pos, RandomSource random) {
		if (levelAccessor.getBrightness(LightLayer.SKY, pos) > random.nextInt(32)) {
			return false;
		} else if (levelAccessor.getBrightness(LightLayer.BLOCK, pos) > 0) {
			return false;
		} else {
			int i = levelAccessor.getLevel().isThundering() ? levelAccessor.getMaxLocalRawBrightness(pos, 10) : levelAccessor.getMaxLocalRawBrightness(pos);
			return i <= random.nextInt(8);
		}
	}

	/**
	 * Check if there are no other Distant Friends nearby within a 64 block radius
	 *
	 * @param level the level
	 * @param pos   the position to check
	 * @return true if no other Distant Friends are nearby, false otherwise
	 */
	private boolean noneNearby(ServerLevel level, BlockPos pos) {
		return Services.PLATFORM.getNearbyFriends(level, pos, 64, 8).isEmpty();
	}

	/**
	 * Spawn a Distant Friend at the given position
	 *
	 * @param pos   the position to spawn at
	 * @param level the level to spawn in
	 */
	private void spawnFriend(BlockPos pos, ServerLevel level) {
		if (!level.isLoaded(pos)) return;

		Mannequin friend = EntityTypes.MANNEQUIN.create(level, EntitySpawnReason.NATURAL);
		if (friend != null) {
			friend.snapTo(pos, 0.0F, 0.0F);

			// Mark the mannequin as a Distant Friend
			Services.PLATFORM.attachFriendData(friend);

			// Attach skin and name data
			CommonClass.attachSkin(friend);

			level.addFreshEntityWithPassengers(friend);
		}
	}
}
