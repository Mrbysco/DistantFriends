package com.mrbysco.distantfriends.config;

import com.mrbysco.distantfriends.DistantFriends;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FriendNamesCache {

	public static List<String> nameList = new ArrayList<>();

	public static void refreshCache() {
		DistantFriends.LOGGER.info("Refreshing friends cache");

		nameList.clear();
		nameList.addAll(FriendConfig.COMMON.friends.get());

		if (FriendConfig.COMMON.playerMobsCompat.get()) {
			List<? extends String> links = FriendConfig.COMMON.playerMobsNameLinks.get();
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
					DistantFriends.LOGGER.error(String.format("Error fetching names from %s", link), e);
				}
			}
		}
	}
}
