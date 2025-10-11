package com.mrbysco.distantfriends;

import com.mojang.serialization.Codec;
import com.mrbysco.distantfriends.commands.DistantCommands;
import com.mrbysco.distantfriends.config.FriendConfigFabric;
import com.mrbysco.distantfriends.util.FriendNamesCache;
import com.mrbysco.distantfriends.util.ServerInstance;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.Mannequin;

public class DistantFriendsFabric implements ModInitializer {
	public static ConfigHolder<FriendConfigFabric> config;

	@SuppressWarnings("UnstableApiUsage")
	public static final AttachmentType<Boolean> IS_FRIEND = AttachmentRegistry.createPersistent(
			Constants.modLoc("is_friend"),
			Codec.BOOL
	);

	@SuppressWarnings("UnstableApiUsage")
	@Override
	public void onInitialize() {
		config = AutoConfig.register(FriendConfigFabric.class, Toml4jConfigSerializer::new);
		config.registerLoadListener((manager, data) -> {
			FriendNamesCache.refreshCache();
			return InteractionResult.SUCCESS;
		});
		config.registerSaveListener((manager, data) -> {
			FriendNamesCache.refreshCache();
			return InteractionResult.SUCCESS;
		});

		AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
			if (entity instanceof Mannequin mannequin && mannequin.hasAttached(IS_FRIEND))
				CommonClass.onFriendDamage(mannequin);
			return InteractionResult.PASS;
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> {
			DistantCommands.initializeCommands(dispatcher);
		});

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			ServerInstance.setServer(server);
			FriendNamesCache.refreshCache();
		});
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> ServerInstance.setServer(null));
	}
}
