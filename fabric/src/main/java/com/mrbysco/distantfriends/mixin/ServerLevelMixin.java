package com.mrbysco.distantfriends.mixin;

import com.google.common.collect.ImmutableList;
import com.mrbysco.distantfriends.util.FriendSpawner;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {
	@Shadow @Final @Mutable
	private List<CustomSpawner> customSpawners;

	protected ServerLevelMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, RegistryAccess registryAccess, Holder<DimensionType> holder, boolean bl, boolean bl2, long l, int i) {
		super(writableLevelData, resourceKey, registryAccess, holder, bl, bl2, l, i);
	}

	@Inject(method = "<init>", at = @At(value = "RETURN"))
	public void distantfriends$patchLevels(MinecraftServer server, Executor executor,
	                                       LevelStorageSource.LevelStorageAccess levelStorage,
	                                       ServerLevelData levelData, ResourceKey dimension, LevelStem levelStem,
	                                       boolean isDebug, long biomeZoomSeed, List customSpawners, boolean tickTime,
	                                       CallbackInfo ci) {
		this.customSpawners = ImmutableList.<CustomSpawner>builder()
				.addAll(customSpawners)
				.add(new FriendSpawner())
				.build();
	}
}
