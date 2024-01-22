package com.mrbysco.distantfriends;

import com.mrbysco.distantfriends.client.ClientHandler;
import com.mrbysco.distantfriends.config.FriendConfigForge;
import com.mrbysco.distantfriends.entity.DistantFriend;
import com.mrbysco.distantfriends.registration.FriendRegistry;
import com.mrbysco.distantfriends.registry.FriendSerializers;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.SpawnPlacementRegisterEvent;

@Mod(Constants.MOD_ID)
public class DistantFriendsNeoForge {

	public DistantFriendsNeoForge() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FriendConfigForge.commonSpec);
		eventBus.register(FriendConfigForge.class);

		FriendSerializers.ENTITY_DATA_SERIALIZER.register(eventBus);
		CommonClass.init();

		eventBus.addListener(this::setupEntities);
		eventBus.addListener(this::registerEntityAttributes);

		if (FMLEnvironment.dist.isClient()) {
			eventBus.addListener(ClientHandler::registerEntityRenders);
		}
	}

	public void setupEntities(SpawnPlacementRegisterEvent event) {
		event.register(FriendRegistry.FRIEND.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DistantFriend::checkFriendSpawn, SpawnPlacementRegisterEvent.Operation.AND);
	}

	public void registerEntityAttributes(EntityAttributeCreationEvent event) {
		event.put(FriendRegistry.FRIEND.get(), DistantFriend.createAttributes().build());
	}
}