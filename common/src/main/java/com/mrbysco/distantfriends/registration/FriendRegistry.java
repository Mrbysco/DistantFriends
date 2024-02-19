package com.mrbysco.distantfriends.registration;

import com.mrbysco.distantfriends.Constants;
import com.mrbysco.distantfriends.entity.DistantFriend;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class FriendRegistry {

	/**
	 * The provider for EntityTypes
	 */
	public static final RegistrationProvider<EntityType<?>> ENTITY_TYPES = RegistrationProvider.get(Registry.ENTITY_TYPE, Constants.MOD_ID);

	public static final RegistryObject<EntityType<DistantFriend>> FRIEND = ENTITY_TYPES.register("friend",
			() -> EntityType.Builder.<DistantFriend>of(DistantFriend::new, MobCategory.AMBIENT)
					.sized(0.6F, 1.8F).clientTrackingRange(10).build("friend"));


	// Called in the mod initializer / constructor in order to make sure that items are registered
	public static void loadClass() {
	}
}
