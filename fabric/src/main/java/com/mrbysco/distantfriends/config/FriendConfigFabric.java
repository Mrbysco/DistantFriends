package com.mrbysco.distantfriends.config;

import com.mrbysco.distantfriends.Constants;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.ArrayList;
import java.util.List;

@Config(name = Constants.MOD_ID)
public class FriendConfigFabric implements ConfigData {
	@ConfigEntry.Gui.CollapsibleObject
	public General general = new General();

	@ConfigEntry.Gui.CollapsibleObject
	public Compat compat = new Compat();

	@ConfigEntry.Gui.CollapsibleObject
	public Spawning spawning = new Spawning();

	public static class General {

		@Comment("""
				A list of users who can be chosen when it spawns a distant friend.
				Format: "<username>", "<username>,<texture_location/body_type>" or "<username>,<texture_location/body_type>,<body_type>"
				Example: "darkosto" or "darkosto,minecraft:entity/player/slim/noor,slim\"""")
		public List<String> friends = List.of("Darkosto", "ShyNieke", "Mrbysco");

		@Comment("""
				A list of dimensions where distant friends can spawn, using their resource location.\s
				Format: "<namespace>:<path>"
				Example: "minecraft:overworld" or "minecraft:the_nether\"""")
		public List<String> spawnDimensions = List.of("minecraft:overworld");

		@Comment("Add the players from the whitelist to the Friends list [default: true]")
		public boolean addWhitelistPlayers = true;

		@Comment("Show the name of the friend above their head [default: true]")
		public boolean showName = true;
	}

	public static class Spawning {

		@Comment("The spawn weight of the distant friend (Must be above 0) (Requires a restart) [default: 20]")
		public int spawnWeight = 20;
	}

	public static class Compat {

		@Comment("Add players from a Player Mobs whitelist to the Friends list [default: false]")
		public boolean playerMobsCompat = false;

		@Comment("The player mobs Name Links")
		public List<String> playerMobsWhitelist = new ArrayList<>();
	}

	@Override
	public void validatePostLoad() throws ValidationException {
		// Validate spawn weight
		if (spawning.spawnWeight <= 0) {
			spawning.spawnWeight = 20; // Reset to default
			throw new ValidationException("Spawn weight must be above 0 for distant friends. Resetting to default (20).");
		}
		if (!compat.playerMobsWhitelist.isEmpty()) {
			for (String link : compat.playerMobsWhitelist) {
				if (!link.startsWith("https://whitelist.gorymoon.se")) {
					throw new ValidationException("The link " + link + " is not a valid link. Please use a link from https://whitelist.gorymoon.se");
				}
			}
		}
	}
}