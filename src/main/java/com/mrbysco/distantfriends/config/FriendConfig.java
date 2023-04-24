package com.mrbysco.distantfriends.config;

import com.mrbysco.distantfriends.DistantFriends;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class FriendConfig {

	public static class Common {
		public final ConfigValue<List<? extends String>> friends;

		public final ForgeConfigSpec.BooleanValue playerMobsCompat;
		public final ForgeConfigSpec.ConfigValue<List<? extends String>> playerMobsNameLinks;

		Common(ForgeConfigSpec.Builder builder) {
			builder.comment("Friends")
					.push("friends");

			friends = builder
					.comment("A list of users who can be chosen when it spawns a distant friend")
					.defineList("friends", List.of("darkosto"), o -> (o instanceof String));

			builder.pop();

			builder.comment("Compat")
					.push("compat");

			this.playerMobsCompat = builder
					.comment("Add players from a Player Mobs whitelist to the Friends list [default: false]")
					.define("playerMobsCompat", false);
			this.playerMobsNameLinks = builder
					.comment("The player mobs Name Links")
					.defineListAllowEmpty(List.of("playerMobsWhitelist"), () -> List.of(""), o ->
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
		DistantFriends.LOGGER.debug("Loaded Distant Friends' config file {}", configEvent.getConfig().getFileName());
		FriendNamesCache.refreshCache();
	}

	@SubscribeEvent
	public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
		DistantFriends.LOGGER.warn("Distant Friends' config just got changed on the file system!");
		FriendNamesCache.refreshCache();
	}
}
