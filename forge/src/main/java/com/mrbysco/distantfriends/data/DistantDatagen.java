package com.mrbysco.distantfriends.data;

import com.mrbysco.distantfriends.Constants;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber
public class DistantDatagen {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent.Client event) {
		event.createProvider(Language::new);
	}

	private static class Language extends LanguageProvider {
		public Language(PackOutput packOutput) {
			super(packOutput, Constants.MOD_ID, "en_us");
		}

		@Override
		protected void addTranslations() {
			addConfig("title", "Distant Friends Config", null);

			addConfig("friend_settings", "Friends", "Friend Settings");
			addConfig("friends", "Friends", "A list of users who can be chosen when it spawns a distant friend");
			addConfig("spawnDimensions", "Friends", "A list of dimensions where distant friends can spawn, using their resource location");
			addConfig("addWhitelistPlayers", "Add Whitelist Players", "Add the players from the whitelist to the Friends list [default: true]");
			addConfig("showName", "Show Name", "Show the name of the friend above their head");

			addConfig("compat_settings", "Compat", "Compat Settings");
			addConfig("playerMobsCompat", "Player Mobs Compat", "Add players from a Player Mobs whitelist to the Friends list [default: false]");
			addConfig("playerMobsNameLinks", "Player Mobs Name Links", "The player mobs Name Links");
		}

		/**
		 * Add the translation for a config entry
		 *
		 * @param path        The path of the config entry
		 * @param name        The name of the config entry
		 * @param description The description of the config entry (optional in case of targeting "title" or similar entries that have no tooltip)
		 */
		private void addConfig(String path, String name, @Nullable String description) {
			this.add(Constants.MOD_ID + ".configuration." + path, name);
			if (description != null && !description.isEmpty())
				this.add(Constants.MOD_ID + ".configuration." + path + ".tooltip", description);
		}
	}
}
