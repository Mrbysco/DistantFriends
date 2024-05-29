package com.mrbysco.distantfriends.platform;

import com.mrbysco.distantfriends.config.FriendConfigForge;
import com.mrbysco.distantfriends.platform.services.IPlatformHelper;
import com.mrbysco.distantfriends.registry.FriendSerializers;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.component.ResolvableProfile;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.Optional;

public class NeoForgePlatformHelper implements IPlatformHelper {

	@Override
	public EntityDataSerializer<Optional<ResolvableProfile>> getResolvableProfileSerializer() {
		return FriendSerializers.OPTIONAL_RESOLVABLE_PROFILE.get();
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
