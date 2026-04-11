package com.mrbysco.distantfriends.config;

import com.mrbysco.distantfriends.Constants;
import com.mrbysco.distantfriends.util.FriendNamesCache;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;

@EventBusSubscriber
public class ConfigHandler {

	@SubscribeEvent
	public static void onLoad(final ModConfigEvent.Loading configEvent) {
		Constants.LOGGER.debug("Loaded Distant Friends' config file {}", configEvent.getConfig().getFileName());
		FriendNamesCache.refreshCache();
	}

	@SubscribeEvent
	public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
		Constants.LOGGER.debug("Distant Friends' config just got changed on the file system!");
		FriendNamesCache.refreshCache();
	}
}
