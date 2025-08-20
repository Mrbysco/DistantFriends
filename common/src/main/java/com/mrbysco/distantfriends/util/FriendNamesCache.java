package com.mrbysco.distantfriends.util;

import com.mrbysco.distantfriends.Constants;
import com.mrbysco.distantfriends.platform.Services;
import net.minecraft.server.players.UserWhiteList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FriendNamesCache {

	public static final List<PlayerData> nameList = new ArrayList<>();

	@SuppressWarnings("deprecation")
	public static void refreshCache() {
		Constants.LOGGER.info("Refreshing friends cache");

		nameList.clear();
		nameList.addAll(generateDataList());

		if (Services.PLATFORM.addWhitelistPlayers() && Services.PLATFORM.getServer() != null) {
			UserWhiteList whitelist = Services.PLATFORM.getServer().getPlayerList().getWhiteList();
			Arrays.stream(whitelist.getUserList())
					.map(PlayerData::new)
					.forEach(nameList::add);
		}

		if (Services.PLATFORM.playerMobsCompat()) {
			List<? extends String> links = Services.PLATFORM.getPlayerMobsNameLinks();
			for (String link : links) {
				try {
					URL url = new URL(link);
					try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
						String line;
						while ((line = reader.readLine()) != null) {
							nameList.add(new PlayerData(line));
						}
					}
				} catch (IOException e) {
					Constants.LOGGER.error("Error fetching names from {}", link, e);
				}
			}
		}
	}

	public static List<PlayerData> generateDataList() {
		List<PlayerData> dataList = new ArrayList<>();
		List<? extends String> names = new ArrayList<>(Services.PLATFORM.getFriends());
		for (String entry : names) {
			if (entry.isEmpty()) {
				continue;
			}
			// Split on comma, left = name, right = texture
			String[] parts = entry.split(",");
			String texture = null;
			if (parts.length > 1) {
				texture = parts[1].trim();
				entry = parts[0].trim();
				// Use regex to check if texture is valid base64 or if it might be malformed
				if (!texture.matches("^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$")) {
					Constants.LOGGER.error("Malformed base64 texture for friend: {}, {}", entry, texture);
					Constants.LOGGER.error("If you inserted this base64 from the config screen the base64 was likely cut off due to the text limitations of the text field.");
					texture = null;
				}
			}

			dataList.add(new PlayerData(entry, texture));
		}

		return dataList;
	}
}
