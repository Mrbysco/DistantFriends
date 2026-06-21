package com.mrbysco.distantfriends.data;

import com.mrbysco.distantfriends.Constants;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class DistantDatagen implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		var pack = generator.createPack();
		pack.addProvider(Language::new);
	}

	private static class Language extends FabricLanguageProvider {
		public Language(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
			super(dataOutput, registryLookup);
		}

		@Override
		public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder builder) {
			addConfig(builder, "title", "Distant Friends Config", null);

			addConfig(builder, "friend_settings", "Friends", "Friend Settings");
			addConfig(builder, "friends", "Friends", "A list of users who can be chosen when it spawns a distant friend");
			addConfig(builder, "spawnDimensions", "Friends", "A list of dimensions where distant friends can spawn, using their resource location");
			addConfig(builder, "addWhitelistPlayers", "Add Whitelist Players", "Add the players from the whitelist to the Friends list [default: true]");
			addConfig(builder, "showName", "Show Name", "Show the name of the friend above their head");

			addConfig(builder, "compat_settings", "Compat", "Compat Settings");
			addConfig(builder, "playerMobsCompat", "Player Mobs Compat", "Add players from a Player Mobs whitelist to the Friends list [default: false]");
			addConfig(builder, "playerMobsNameLinks", "Player Mobs Name Links", "The player mobs Name Links");
		}

		private void addConfig(TranslationBuilder builder, String path, String name, @Nullable String description) {
			builder.add(Constants.MOD_ID + ".configuration." + path, name);
			if (description != null && !description.isEmpty())
				builder.add(Constants.MOD_ID + ".configuration." + path + ".tooltip", description);
		}
	}
}
