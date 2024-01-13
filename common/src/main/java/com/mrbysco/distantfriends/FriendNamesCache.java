package com.mrbysco.distantfriends;

import com.mrbysco.distantfriends.platform.Services;
import net.minecraft.server.players.UserWhiteList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FriendNamesCache {

	public static final List<String> nameList = new ArrayList<>();

	public static void refreshCache() {
		Constants.LOGGER.info("Refreshing friends cache");

		nameList.clear();
		nameList.addAll(Services.PLATFORM.getFriends());

		if (Services.PLATFORM.addWhitelistPlayers() && Services.PLATFORM.getServer() != null) {
			UserWhiteList whitelist = Services.PLATFORM.getServer().getPlayerList().getWhiteList();
			String[] whitelisted = whitelist.getUserList();
			for (String name : whitelisted) {
				nameList.add(name);
			}
		}

		if (Services.PLATFORM.playerMobsCompat()) {
			List<? extends String> links = Services.PLATFORM.getPlayerMobsNameLinks();
			for (String link : links) {
				try {
					URL url = new URL(link);
					try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
						String line;
						while ((line = reader.readLine()) != null) {
							nameList.add(line);
						}
					}
				} catch (IOException e) {
					Constants.LOGGER.error(String.format("Error fetching names from %s", link), e);
				}
			}
		}
	}
}
