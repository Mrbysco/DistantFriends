package com.mrbysco.distantfriends.data;

import com.mrbysco.distantfriends.registration.FriendRegistry;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.function.BiConsumer;

public class DistantDatagen implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		generator.addProvider(Loots::new);
		generator.addProvider(Language::new);
	}

	private static class Loots extends SimpleFabricLootTableProvider {
		public Loots(FabricDataGenerator generator) {
			super(generator, LootContextParamSets.ENTITY);
		}

		@Override
		public void accept(BiConsumer<ResourceLocation, LootTable.Builder> biConsumer) {
			biConsumer.accept(FriendRegistry.FRIEND.get().getDefaultLootTable(), LootTable.lootTable());
		}
	}

	private static class Language extends FabricLanguageProvider {
		public Language(FabricDataGenerator generator) {
			super(generator);
		}

		@Override
		public void generateTranslations(TranslationBuilder builder) {
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
