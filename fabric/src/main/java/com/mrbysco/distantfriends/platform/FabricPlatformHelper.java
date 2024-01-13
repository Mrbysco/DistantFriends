package com.mrbysco.distantfriends.platform;

import com.mojang.authlib.GameProfile;
import com.mrbysco.distantfriends.DistantFriendsFabric;
import com.mrbysco.distantfriends.platform.services.IPlatformHelper;
import com.mrbysco.distantfriends.util.ServerInstance;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.server.MinecraftServer;

import java.util.List;
import java.util.Optional;

public class FabricPlatformHelper implements IPlatformHelper {

	@Override
	public EntityDataSerializer<Optional<GameProfile>> getGameProfileSerializer() {
		return DistantFriendsFabric.OPTIONAL_GAMEPROFILE;
	}

	@Override
	public List<? extends String> getFriends() {
		return DistantFriendsFabric.config.get().general.friends;
	}

	@Override
	public boolean addWhitelistPlayers() {
		return DistantFriendsFabric.config.get().general.addWhitelistPlayers;
	}

	@Override
	public boolean playerMobsCompat() {
		return DistantFriendsFabric.config.get().compat.playerMobsCompat;
	}

	@Override
	public List<? extends String> getPlayerMobsNameLinks() {
		return DistantFriendsFabric.config.get().compat.playerMobsWhitelist;
	}

	@Override
	public MinecraftServer getServer() {
		return ServerInstance.getServer();
	}
}
