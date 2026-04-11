package com.mrbysco.distantfriends;

import com.mojang.serialization.Codec;
import com.mrbysco.distantfriends.commands.DistantCommands;
import com.mrbysco.distantfriends.config.FriendConfig;
import com.mrbysco.distantfriends.util.FriendNamesCache;
import com.mrbysco.distantfriends.util.ServerInstance;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.v5.ModConfigEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.Mannequin;
import net.neoforged.fml.config.ModConfig;

public class DistantFriendsFabric implements ModInitializer {

	@SuppressWarnings("UnstableApiUsage")
	public static final AttachmentType<Boolean> IS_FRIEND = AttachmentRegistry.createPersistent(
			Constants.modLoc("is_friend"),
			Codec.BOOL
	);

	@Override
	public void onInitialize() {
		ConfigRegistry.INSTANCE.register(Constants.MOD_ID, ModConfig.Type.COMMON, FriendConfig.commonSpec);

		ModConfigEvents.loading(Constants.MOD_ID).register((config) -> {
			Constants.LOGGER.debug("Loaded Distant Friends' config file {}", config.getFileName());
			FriendNamesCache.refreshCache();
		});
		ModConfigEvents.reloading(Constants.MOD_ID).register((config) -> {
			Constants.LOGGER.debug("Distant Friends' config just got changed on the file system!");
			FriendNamesCache.refreshCache();
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
