package com.mrbysco.distantfriends.platform;

import com.mrbysco.distantfriends.DistantFriendsFabric;
import com.mrbysco.distantfriends.platform.services.IPlatformHelper;
import com.mrbysco.distantfriends.util.ServerInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.decoration.Mannequin;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class FabricPlatformHelper implements IPlatformHelper {

	@Override
	public MinecraftServer getServer() {
		return ServerInstance.getServer();
	}

	@Override
	public void attachFriendData(Mannequin friend) {
		friend.setAttached(DistantFriendsFabric.IS_FRIEND, true);
	}

	@Override
	public List<? extends Mannequin> getNearbyFriends(ServerLevel serverLevel, BlockPos pos, int horizontalRange, int verticalRange) {
		return serverLevel.getEntitiesOfClass(Mannequin.class, new AABB(pos).inflate(horizontalRange, verticalRange, horizontalRange), mannequin ->
				mannequin.hasAttached(DistantFriendsFabric.IS_FRIEND)
		);
	}

}
