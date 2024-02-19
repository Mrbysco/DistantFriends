package com.mrbysco.distantfriends.data;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import com.mrbysco.distantfriends.Constants;
import com.mrbysco.distantfriends.registration.FriendRegistry;
import com.mrbysco.distantfriends.registration.RegistryObject;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.EntityLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DistantDatagen {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		final RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.builtinCopy());
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper helper = event.getExistingFileHelper();

		if (event.includeServer()) {
			generator.addProvider(true, new Loots(generator));

			final HolderSet.Named<Biome> overworldTag = new HolderSet.Named<>(ops.registry(Registry.BIOME_REGISTRY).get(),
					BiomeTags.IS_OVERWORLD);
			final BiomeModifier addSpawn = ForgeBiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(
					overworldTag,
					new MobSpawnSettings.SpawnerData(FriendRegistry.FRIEND.get(), 20, 1, 2));

			generator.addProvider(event.includeServer(), JsonCodecProvider.forDatapackRegistry(
					generator, helper, Constants.MOD_ID, ops, ForgeRegistries.Keys.BIOME_MODIFIERS,
					Map.of(
							new ResourceLocation(Constants.MOD_ID, "add_distant_friend"), addSpawn
					)
			));
		}
		if (event.includeClient()) {
			generator.addProvider(true, new Language(generator));
		}
	}

	private static class Loots extends LootTableProvider {
		public Loots(DataGenerator generator) {
			super(generator);
		}

		@Override
		protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
			return ImmutableList.of(
					Pair.of(FriendLootProvider::new, LootContextParamSets.ENTITY)
			);
		}

		public static class FriendLootProvider extends EntityLoot {

			@Override
			public void addTables() {
				this.add(FriendRegistry.FRIEND.get(), LootTable.lootTable());
			}

			@Override
			protected Iterable<EntityType<?>> getKnownEntities() {
				Stream<EntityType<?>> entityTypeStream = FriendRegistry.ENTITY_TYPES.getEntries().stream().map(RegistryObject::get);
				return entityTypeStream::iterator;
			}
		}

		@Override
		protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationContext) {
			map.forEach((name, table) -> table.validate(validationContext));
		}
	}

	private static class Language extends LanguageProvider {
		public Language(DataGenerator generator) {
			super(generator, Constants.MOD_ID, "en_us");
		}

		@Override
		protected void addTranslations() {
			this.addEntityType(FriendRegistry.FRIEND, "Distant Friend");
		}
	}
}
