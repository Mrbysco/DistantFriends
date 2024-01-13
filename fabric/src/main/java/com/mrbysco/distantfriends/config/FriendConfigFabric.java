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

	public static class General {

		@Comment("A list of users who can be chosen when it spawns a distant friend")
		public List<String> friends = List.of("darkosto");

		@Comment("Add the players from the whitelist to the Friends list [default: true]")
		public boolean addWhitelistPlayers = true;
	}


	public static class Compat {

		@Comment("Add players from a Player Mobs whitelist to the Friends list [default: false]")
		public boolean playerMobsCompat = false;

		@Comment("The player mobs Name Links")
		public List<String> playerMobsWhitelist = new ArrayList<>();
	}

	@Override
	public void validatePostLoad() throws ValidationException {
		if (!compat.playerMobsWhitelist.isEmpty()) {
			for (String link : compat.playerMobsWhitelist) {
				if (!link.startsWith("https://whitelist.gorymoon.se")) {
					throw new ValidationException("The link " + link + " is not a valid link. Please use a link from https://whitelist.gorymoon.se");
				}
			}
		}
	}
}