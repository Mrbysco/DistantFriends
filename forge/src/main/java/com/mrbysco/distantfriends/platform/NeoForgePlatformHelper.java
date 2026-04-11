package com.mrbysco.distantfriends.platform;

import com.mrbysco.distantfriends.DistantFriendsNeoForge;
import com.mrbysco.distantfriends.platform.services.IPlatformHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.decoration.Mannequin;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.List;

public class NeoForgePlatformHelper implements IPlatformHelper {

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

}
