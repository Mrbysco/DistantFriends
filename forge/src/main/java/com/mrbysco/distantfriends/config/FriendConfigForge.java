package com.mrbysco.distantfriends.config;

import com.mrbysco.distantfriends.Constants;
import com.mrbysco.distantfriends.FriendNamesCache;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class FriendConfigForge {

	public static class Common {

		public final ConfigValue<List<? extends String>> friends;
		public final ForgeConfigSpec.BooleanValue addWhitelistPlayers;

		public final ForgeConfigSpec.BooleanValue playerMobsCompat;
		public final ConfigValue<List<? extends String>> playerMobsNameLinks;

		Common(ForgeConfigSpec.Builder builder) {
			builder.comment("Friends")
					.push("friends");

			friends = builder
					.comment("A list of users who can be chosen when it spawns a distant friend")
					.defineListAllowEmpty("friends", List.of("darkosto"), o -> (o instanceof String));
			addWhitelistPlayers = builder
					.comment("Add the players from the whitelist to the Friends list [default: true]")
					.define("addWhitelistPlayers", true);

			builder.pop();

			builder.comment("Compat")
					.push("compat");

			playerMobsCompat = builder
					.comment("Add players from a Player Mobs whitelist to the Friends list [default: false]")
					.define("playerMobsCompat", false);
			playerMobsNameLinks = builder
					.comment("The player mobs Name Links, must start with https://whitelist.gorymoon.se")
					.defineListAllowEmpty(List.of("playerMobsNameLinks"), List.of(), o ->
							(o instanceof String string && string.startsWith("https://whitelist.gorymoon.se")));

			builder.pop();
		}
	}


	public static final ForgeConfigSpec commonSpec;
	public static final Common COMMON;

	static {
		final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
		commonSpec = specPair.getRight();
		COMMON = specPair.getLeft();
	}

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
