package com.mrbysco.distantfriends.platform;

import com.mrbysco.distantfriends.DistantFriendsNeoForge;
import com.mrbysco.distantfriends.config.FriendConfigNeoForge;
import com.mrbysco.distantfriends.platform.services.IPlatformHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.decoration.Mannequin;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.List;

public class NeoForgePlatformHelper implements IPlatformHelper {

	@Override
	public List<? extends String> getFriends() {
		return FriendConfigNeoForge.COMMON.friends.get();
	}

	@Override
	public boolean addWhitelistPlayers() {
		return FriendConfigNeoForge.COMMON.addWhitelistPlayers.get();
	}

	@Override
	public boolean playerMobsCompat() {
		return FriendConfigNeoForge.COMMON.playerMobsCompat.get();
	}

	@Override
	public List<? extends String> getPlayerMobsNameLinks() {
		return FriendConfigNeoForge.COMMON.playerMobsNameLinks.get();
	}

	@Override
	public boolean showName() {
		return FriendConfigNeoForge.COMMON.showName.get();
	}

	@Override
	public MinecraftServer getServer() {
		return ServerLifecycleHooks.getCurrentServer();
	}

	@Override
	public void attachFriendData(Mannequin friend) {
		friend.setData(DistantFriendsNeoForge.IS_FRIEND, true);
	}

	@Override
	public List<? extends Mannequin> getNearbyFriends(ServerLevel serverLevel, BlockPos pos, int horizontalRange, int verticalRange) {
		return serverLevel.getEntitiesOfClass(Mannequin.class, new AABB(pos).inflate(horizontalRange, verticalRange, horizontalRange), mannequin ->
				mannequin.hasData(DistantFriendsNeoForge.IS_FRIEND)
		);
	}

	@Override
	public boolean isDimensionAllowed(ResourceLocation dimension) {
		return FriendConfigNeoForge.COMMON.spawnDimensions.get().contains(dimension.toString());
	}
}
