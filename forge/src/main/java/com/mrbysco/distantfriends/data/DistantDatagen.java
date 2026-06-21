package com.mrbysco.distantfriends.data;

import com.mrbysco.distantfriends.Constants;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public class DistantDatagen {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent.Client event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		generator.addProvider(true, new Datapack(
				packOutput, lookupProvider, Set.of(Constants.MOD_ID)));

		generator.addProvider(true, new Language(packOutput));

	}

	private static class Datapack extends DatapackBuiltinEntriesProvider {
		public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
				.add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, context -> {
					final HolderGetter<Biome> biomeHolderGetter = context.lookup(Registries.BIOME);
					final BiomeModifier addSpawn = BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(
							biomeHolderGetter.getOrThrow(BiomeTags.IS_OVERWORLD),
							new Weighted<>(new MobSpawnSettings.SpawnerData(EntityTypes.MANNEQUIN, 1, 2), 20));

					context.register(createKey("add_distant_friend"), addSpawn);
				});

		public Datapack(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, Set<String> modIds) {
			super(output, registries, BUILDER, modIds);
		}
	}

	private static ResourceKey<BiomeModifier> createKey(String name) {
		return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Constants.modLoc(name));
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
