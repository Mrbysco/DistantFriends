package com.mrbysco.distantfriends;

import com.mojang.authlib.GameProfile;
import com.mrbysco.distantfriends.config.FriendConfigFabric;
import com.mrbysco.distantfriends.entity.DistantFriend;
import com.mrbysco.distantfriends.registration.FriendRegistry;
import com.mrbysco.distantfriends.util.ServerInstance;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Optional;
import java.util.function.Predicate;

public class DistantFriendsFabric implements ModInitializer {

	public static final EntityDataSerializer<Optional<GameProfile>> OPTIONAL_GAMEPROFILE = new EntityDataSerializer<Optional<GameProfile>>() {
		public void write(FriendlyByteBuf friendlyByteBuf, Optional<GameProfile> optionalGameProfile) {
			friendlyByteBuf.writeBoolean(optionalGameProfile.isPresent());
			if (optionalGameProfile.isPresent()) {
				friendlyByteBuf.writeNbt(NbtUtils.writeGameProfile(new CompoundTag(), optionalGameProfile.get()));
			}

		}

		public Optional<GameProfile> read(FriendlyByteBuf friendlyByteBuf) {
			return !friendlyByteBuf.readBoolean() ? Optional.empty() : Optional.of(NbtUtils.readGameProfile(friendlyByteBuf.readNbt()));
		}

		public Optional<GameProfile> copy(Optional<GameProfile> optionalGameProfile) {
			return optionalGameProfile;
		}
	};
	public static ConfigHolder<FriendConfigFabric> config;

	@Override
	public void onInitialize() {
		config = AutoConfig.register(FriendConfigFabric.class, Toml4jConfigSerializer::new);
		config.registerLoadListener((manager, data) -> {
			FriendNamesCache.refreshCache();
			return InteractionResult.SUCCESS;
		});
		config.registerSaveListener((manager, data) -> {
			FriendNamesCache.refreshCache();
			return InteractionResult.SUCCESS;
		});

		EntityDataSerializers.registerSerializer(OPTIONAL_GAMEPROFILE);
		CommonClass.init();

		addFriendSpawn();
		FabricDefaultAttributeRegistry.register(FriendRegistry.FRIEND.get(), DistantFriend.createAttributes());
		SpawnPlacements.register(FriendRegistry.FRIEND.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DistantFriend::checkFriendSpawn);

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			ServerInstance.setServer(server);
			FriendNamesCache.refreshCache();
		});
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> ServerInstance.setServer(null));
	}


	public static void addFriendSpawn() {
		Predicate<BiomeSelectionContext> overworld = (ctx -> ctx.hasTag(BiomeTags.IS_OVERWORLD));
		BiomeModifications.addSpawn(overworld, MobCategory.CREATURE, FriendRegistry.FRIEND.get(), 20, 1, 2);
	}
}
