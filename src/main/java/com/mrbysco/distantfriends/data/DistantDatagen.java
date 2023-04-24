package com.mrbysco.distantfriends.data;

import com.mrbysco.distantfriends.DistantFriends;
import com.mrbysco.distantfriends.registry.FriendRegistry;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DistantDatagen {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();

		if (event.includeServer()) {
			generator.addProvider(true, new Loots(packOutput));

			generator.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(
					packOutput, CompletableFuture.supplyAsync(DistantDatagen::getProvider), Set.of(DistantFriends.MOD_ID)));
		}
		if (event.includeClient()) {
			generator.addProvider(true, new Language(packOutput));
		}
	}

	private static HolderLookup.Provider getProvider() {
		final RegistrySetBuilder registryBuilder = new RegistrySetBuilder();
		registryBuilder.add(ForgeRegistries.Keys.BIOME_MODIFIERS, context -> {
			final HolderGetter<Biome> biomeHolderGetter = context.lookup(Registries.BIOME);
			final BiomeModifier addSpawn = ForgeBiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(
					biomeHolderGetter.getOrThrow(BiomeTags.IS_OVERWORLD),
					new MobSpawnSettings.SpawnerData(FriendRegistry.FRIEND.get(), 20, 1, 2));

			context.register(createKey("add_distant_friend"), addSpawn);
		});
		// We need the BIOME registry to be present so we can use a biome tag, doesn't matter that it's empty
		registryBuilder.add(Registries.BIOME, $ -> {
		});
		RegistryAccess.Frozen regAccess = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
		return registryBuilder.buildPatch(regAccess, VanillaRegistries.createLookup());
	}

	private static ResourceKey<BiomeModifier> createKey(String name) {
		return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(DistantFriends.MOD_ID, name));
	}

	private static class Loots extends LootTableProvider {
		public Loots(PackOutput packOutput) {
			super(packOutput, Set.of(), List.of(
					new SubProviderEntry(FriendLootProvider::new, LootContextParamSets.ENTITY)
			));
		}

		public static class FriendLootProvider extends EntityLootSubProvider {
			protected FriendLootProvider() {
				super(FeatureFlags.REGISTRY.allFlags());
			}

			@Override
			public void generate() {
				this.add(FriendRegistry.FRIEND.get(), LootTable.lootTable());
			}

			@Override
			protected Stream<EntityType<?>> getKnownEntityTypes() {
				return FriendRegistry.ENTITIES.getEntries().stream().map(RegistryObject::get);
			}
		}

		@Override
		protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationContext) {
			map.forEach((name, table) -> LootTables.validate(validationContext, name, table));
		}
	}

	private static class Language extends LanguageProvider {
		public Language(PackOutput packOutput) {
			super(packOutput, DistantFriends.MOD_ID, "en_us");
		}

		@Override
		protected void addTranslations() {
			this.addEntityType(FriendRegistry.FRIEND, "Distant Friend");
		}
	}
}
