package com.mrbysco.distantfriends.data;

import com.mrbysco.distantfriends.Constants;
import com.mrbysco.distantfriends.registration.FriendRegistry;
import com.mrbysco.distantfriends.registration.RegistryObject;
import net.minecraft.core.Cloner;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class DistantDatagen {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();

		if (event.includeServer()) {
			generator.addProvider(true, new Loots(packOutput, event.getLookupProvider()));

			generator.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(
					packOutput, CompletableFuture.supplyAsync(DistantDatagen::getProvider), Set.of(Constants.MOD_ID)));
		}
		if (event.includeClient()) {
			generator.addProvider(true, new Language(packOutput));
		}
	}

	private static RegistrySetBuilder.PatchedRegistries getProvider() {
		final RegistrySetBuilder registryBuilder = new RegistrySetBuilder();
		registryBuilder.add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, context -> {
			final HolderGetter<Biome> biomeHolderGetter = context.lookup(Registries.BIOME);
			final BiomeModifier addSpawn = BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(
					biomeHolderGetter.getOrThrow(BiomeTags.IS_OVERWORLD),
					new MobSpawnSettings.SpawnerData(FriendRegistry.FRIEND.get(), 20, 1, 2));

			context.register(createKey("add_distant_friend"), addSpawn);
		});
		// We need the BIOME registry to be present, so we can use a biome tag, doesn't matter that it's empty
		registryBuilder.add(Registries.BIOME, $ -> {
		});
		RegistryAccess.Frozen regAccess = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
		Cloner.Factory cloner$factory = new Cloner.Factory();
		net.neoforged.neoforge.registries.DataPackRegistriesHooks.getDataPackRegistriesWithDimensions().forEach(data -> data.runWithArguments(cloner$factory::addCodec));
		return registryBuilder.buildPatch(regAccess, VanillaRegistries.createLookup(), cloner$factory);
	}

	private static ResourceKey<BiomeModifier> createKey(String name) {
		return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name));
	}

	private static class Loots extends LootTableProvider {
		public Loots(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
			super(packOutput, Set.of(), List.of(
					new SubProviderEntry(FriendLootProvider::new, LootContextParamSets.ENTITY)
			), lookupProvider);
		}

		public static class FriendLootProvider extends EntityLootSubProvider {
			protected FriendLootProvider(HolderLookup.Provider provider) {
				super(FeatureFlags.REGISTRY.allFlags(), provider);
			}

			@Override
			public void generate() {
				this.add(FriendRegistry.FRIEND.get(), LootTable.lootTable());
			}

			@Override
			protected Stream<EntityType<?>> getKnownEntityTypes() {
				return FriendRegistry.ENTITY_TYPES.getEntries().stream().map(RegistryObject::get);
			}
		}

		@Override
		protected void validate(WritableRegistry<LootTable> writableregistry, ValidationContext validationcontext, ProblemReporter.Collector problemreporter$collector) {
			super.validate(writableregistry, validationcontext, problemreporter$collector);
		}
	}

	private static class Language extends LanguageProvider {
		public Language(PackOutput packOutput) {
			super(packOutput, Constants.MOD_ID, "en_us");
		}

		@Override
		protected void addTranslations() {
			this.addEntityType(FriendRegistry.FRIEND, "Distant Friend");

			addConfig("title", "Distant Friends Config", null);

			addConfig("friend_settings", "Friends", "Friend Settings");
			addConfig("friends", "Friends", "A list of users who can be chosen when it spawns a distant friend");
			addConfig("addWhitelistPlayers", "Add Whitelist Players", "Add the players from the whitelist to the Friends list [default: true]");

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
