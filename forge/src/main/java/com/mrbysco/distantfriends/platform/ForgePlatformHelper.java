package com.mrbysco.distantfriends.platform;

import com.mojang.authlib.GameProfile;
import com.mrbysco.distantfriends.config.FriendConfigForge;
import com.mrbysco.distantfriends.platform.services.IPlatformHelper;
import com.mrbysco.distantfriends.registry.FriendSerializers;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.Optional;

public class ForgePlatformHelper implements IPlatformHelper {

	@Override
	public EntityDataSerializer<Optional<GameProfile>> getGameProfileSerializer() {
		return FriendSerializers.OPTIONAL_GAME_PROFILE.get();
	}

	@Override
	public List<? extends String> getFriends() {
		return FriendConfigForge.COMMON.friends.get();
	}

	@Override
	public boolean addWhitelistPlayers() {
		return FriendConfigForge.COMMON.addWhitelistPlayers.get();
	}

	@Override
	public boolean playerMobsCompat() {
		return FriendConfigForge.COMMON.playerMobsCompat.get();
	}

	@Override
	public List<? extends String> getPlayerMobsNameLinks() {
		return FriendConfigForge.COMMON.playerMobsNameLinks.get();
	}

	@Override
	public MinecraftServer getServer() {
		return ServerLifecycleHooks.getCurrentServer();
	}
}
