package com.mrbysco.distantfriends.data;

import com.mrbysco.distantfriends.registration.FriendRegistry;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class DistantDatagen implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		var pack = generator.createPack();

		pack.addProvider(Loots::new);
		pack.addProvider(Language::new);
	}

	private static class Loots extends SimpleFabricLootTableProvider {
		public Loots(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
			super(dataOutput, registryLookup, LootContextParamSets.ENTITY);
		}

		@Override
		public void generate(HolderLookup.Provider provider, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer) {
			biConsumer.accept(FriendRegistry.FRIEND.get().getDefaultLootTable(), LootTable.lootTable());
		}
	}

	private static class Language extends FabricLanguageProvider {
		public Language(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
			super(dataOutput, registryLookup);
		}

		@Override
		public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder builder) {
			builder.add(FriendRegistry.FRIEND.get(), "Distant Friend");

			builder.add("text.autoconfig.distantfriends.title", "Distant Friends");
			builder.add("text.autoconfig.distantfriends.option.general", "General");
			builder.add("text.autoconfig.distantfriends.option.general.friends", "Friends");
			builder.add("text.autoconfig.distantfriends.option.general.addWhitelistPlayers", "Add Whitelist Players");
			builder.add("text.autoconfig.distantfriends.option.compat", "Compat");
			builder.add("text.autoconfig.distantfriends.option.compat.playerMobsCompat", "Player Mobs Compat");
			builder.add("text.autoconfig.distantfriends.option.compat.playerMobsWhitelist", "Player Mobs Whitelist");
		}
	}
}
