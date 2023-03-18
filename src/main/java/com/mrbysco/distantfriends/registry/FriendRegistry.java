package com.mrbysco.distantfriends.registry;

import com.mrbysco.distantfriends.DistantFriends;
import com.mrbysco.distantfriends.entity.DistantFriend;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FriendRegistry {
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, DistantFriends.MOD_ID);

	public static final RegistryObject<EntityType<DistantFriend>> FRIEND = ENTITIES.register("friend",
			() -> EntityType.Builder.<DistantFriend>of(DistantFriend::new, MobCategory.AMBIENT)
					.sized(0.6F, 1.8F).clientTrackingRange(10).build("friend"));

	public static void setupEntities() {
		SpawnPlacements.register(FriendRegistry.FRIEND.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DistantFriend::checkFriendSpawn);
	}

	public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
		event.put(FriendRegistry.FRIEND.get(), DistantFriend.createAttributes().build());
	}
}
