package com.mrbysco.distantfriends.world;

import com.mrbysco.distantfriends.DistantFriends;
import com.mrbysco.distantfriends.config.FriendConfig;
import com.mrbysco.distantfriends.registry.FriendRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DistantFriends.MOD_ID)
public class SpawnHandler {
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void addSpawn(BiomeLoadingEvent event) {
		if (event.getName() != null) {
			ResourceKey<Biome> biomeKey = ResourceKey.create(Registry.BIOME_REGISTRY, event.getName());
			if (BiomeDictionary.hasType(biomeKey, BiomeDictionary.Type.OVERWORLD) && FriendConfig.COMMON.friendWeight.get() > 0) {
				event.getSpawns().getSpawner(MobCategory.AMBIENT).add(
						new MobSpawnSettings.SpawnerData(FriendRegistry.FRIEND.get(),
								FriendConfig.COMMON.friendWeight.get(), FriendConfig.COMMON.friendMinGroup.get(), FriendConfig.COMMON.friendMaxGroup.get()));
			}
		}
	}
}
