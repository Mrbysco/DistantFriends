package com.mrbysco.distantfriends.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

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
			builder.add("text.autoconfig.distantfriends.title", "Distant Friends");
			builder.add("text.autoconfig.distantfriends.option.general", "General");
			builder.add("text.autoconfig.distantfriends.option.general.friends", "Friends");
			builder.add("text.autoconfig.distantfriends.option.general.spawnDimensions", "Spawn Dimensions");
			builder.add("text.autoconfig.distantfriends.option.general.addWhitelistPlayers", "Add Whitelist Players");
			builder.add("text.autoconfig.distantfriends.option.general.showName", "Show Name");
			builder.add("text.autoconfig.distantfriends.option.spawning", "Spawning");
			builder.add("text.autoconfig.distantfriends.option.spawning.spawnWeight", "Spawn Weight");
			builder.add("text.autoconfig.distantfriends.option.compat", "Compat");
			builder.add("text.autoconfig.distantfriends.option.compat.playerMobsCompat", "Player Mobs Compat");
			builder.add("text.autoconfig.distantfriends.option.compat.playerMobsWhitelist", "Player Mobs Whitelist");
		}
	}
}
