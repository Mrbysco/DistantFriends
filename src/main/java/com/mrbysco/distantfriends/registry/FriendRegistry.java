package com.mrbysco.distantfriends.registry;

import com.mrbysco.distantfriends.DistantFriends;
import com.mrbysco.distantfriends.entity.DistantFriend;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.SpawnPlacementRegisterEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class FriendRegistry {
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, DistantFriends.MOD_ID);

	public static final Supplier<EntityType<DistantFriend>> FRIEND = ENTITIES.register("friend",
			() -> EntityType.Builder.<DistantFriend>of(DistantFriend::new, MobCategory.AMBIENT)
					.sized(0.6F, 1.8F).clientTrackingRange(10).build("friend"));

	public static void setupEntities(SpawnPlacementRegisterEvent event) {
		event.register(FriendRegistry.FRIEND.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DistantFriend::checkFriendSpawn, SpawnPlacementRegisterEvent.Operation.AND);
	}

	public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
		event.put(FriendRegistry.FRIEND.get(), DistantFriend.createAttributes().build());
	}
}
