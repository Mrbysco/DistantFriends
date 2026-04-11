package com.mrbysco.distantfriends.util;

import com.mrbysco.distantfriends.Constants;
import com.mrbysco.distantfriends.config.FriendConfig;
import com.mrbysco.distantfriends.platform.Services;
import net.minecraft.resources.Identifier;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.world.entity.player.PlayerModelType;

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

		if (FriendConfig.COMMON.addWhitelistPlayers.get() && Services.PLATFORM.getServer() != null) {
			UserWhiteList whitelist = Services.PLATFORM.getServer().getPlayerList().getWhiteList();
			Arrays.stream(whitelist.getUserList())
					.map(PlayerData::new)
					.forEach(nameList::add);
		}

		if (FriendConfig.COMMON.playerMobsCompat.get()) {
			List<? extends String> links = FriendConfig.COMMON.playerMobsNameLinks.get();
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

	/**
	 * Generate a list of PlayerData from the friends list
	 *
	 * @return List of PlayerData
	 */
	public static List<PlayerData> generateDataList() {
		List<PlayerData> dataList = new ArrayList<>();
		List<? extends String> names = new ArrayList<>(FriendConfig.COMMON.friends.get());
		for (String entry : names) {
			if (entry.isEmpty()) {
				continue;
			}
			// Split on comma, left = name, right = texture
			String[] parts = entry.split(",");
			String secondEntry = null;
			PlayerModelType bodyType = null;
			if (parts.length > 1) {
				entry = parts[0].trim();
				secondEntry = parts[1].trim();
				// Check for body type (texture part can be "slim" or "wide" to indicate model type)
				if (secondEntry.equals("slim") || secondEntry.equals("wide")) {
					bodyType = secondEntry.equals("slim") ? PlayerModelType.SLIM : PlayerModelType.WIDE;
					secondEntry = "";
				} else {
					if (parts.length > 2) {
						String type = parts[2].trim().toLowerCase();
						if (type.equals("slim")) {
							bodyType = PlayerModelType.SLIM;
						} else if (type.equals("wide")) {
							bodyType = PlayerModelType.WIDE;
						}
					}
				}
				// Validate Identifier
				if (!secondEntry.isEmpty()) {
					if (Identifier.tryParse(secondEntry) == null) {
						Constants.LOGGER.error("Malformed Identifier for friend: {}, {}", entry, secondEntry);
						secondEntry = null;
					}
				}
			}

			dataList.add(new PlayerData(entry, secondEntry, bodyType));
		}

		return dataList;
	}
}
