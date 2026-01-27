package com.mrbysco.distantfriends.config;

import com.mrbysco.distantfriends.Constants;
import com.mrbysco.distantfriends.util.FriendNamesCache;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class FriendConfigNeoForge {

	public static class Common {

		public final ConfigValue<List<? extends String>> friends;
		public final ConfigValue<List<? extends String>> spawnDimensions;
		public final ModConfigSpec.BooleanValue addWhitelistPlayers;
		public final ModConfigSpec.BooleanValue showName;

		public final ModConfigSpec.BooleanValue playerMobsCompat;
		public final ConfigValue<List<? extends String>> playerMobsNameLinks;

		Common(ModConfigSpec.Builder builder) {
			builder.comment("Friends")
					.push("friend_settings");

			friends = builder
					.comment("A list of users who can be chosen when it spawns a distant friend. ",
							"Format: \"<username>\", \"<username>,<texture_location/body_type>\" or \"<username>,<texture_location/body_type>,<body_type>\"",
							"Example: \"darkosto\" or \"darkosto,minecraft:entity/player/slim/noor,slim\"")
					.defineListAllowEmpty("friends", List.of("darkosto", "shynieke", "mrbysco"), String::new, o -> (o instanceof String));
			spawnDimensions = builder
					.comment("A list of dimensions where distant friends can spawn, using their resource location. ",
							"Format: \"<namespace>:<path>\"",
							"Example: \"minecraft:overworld\" or \"minecraft:the_nether\"")
					.defineListAllowEmpty("spawnDimensions", List.of("minecraft:overworld"), String::new, o -> (o instanceof String) && Identifier.tryParse((String) o) != null);
			addWhitelistPlayers = builder
					.comment("Add the players from the whitelist to the Friends list [default: true]")
					.define("addWhitelistPlayers", true);
			showName = builder
					.comment("Show the name of the friend above their head [default: true]")
					.define("showName", true);

			builder.pop();

			builder.comment("Compat")
					.push("compat_settings");

			playerMobsCompat = builder
					.comment("Add players from a Player Mobs whitelist to the Friends list [default: false]")
					.define("playerMobsCompat", false);
			playerMobsNameLinks = builder
					.comment("The player mobs Name Links, must start with https://whitelist.gorymoon.se")
					.defineListAllowEmpty("playerMobsNameLinks", List.of(), String::new, o ->
							(o instanceof String string && string.startsWith("https://whitelist.gorymoon.se")));

			builder.pop();
		}
	}


	public static final ModConfigSpec commonSpec;
	public static final Common COMMON;

	static {
		final Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
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
