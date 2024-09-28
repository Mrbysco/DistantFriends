package com.mrbysco.distantfriends;

import com.mrbysco.distantfriends.client.ClientHandler;
import com.mrbysco.distantfriends.config.FriendConfigForge;
import com.mrbysco.distantfriends.entity.DistantFriend;
import com.mrbysco.distantfriends.registration.FriendRegistry;
import com.mrbysco.distantfriends.registry.FriendSerializers;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

@Mod(Constants.MOD_ID)
public class DistantFriendsNeoForge {

	public DistantFriendsNeoForge(IEventBus eventBus, ModContainer container, Dist dist) {
		container.registerConfig(ModConfig.Type.COMMON, FriendConfigForge.commonSpec);
		eventBus.register(FriendConfigForge.class);

		FriendSerializers.ENTITY_DATA_SERIALIZER.register(eventBus);
		CommonClass.init();

		eventBus.addListener(this::setupEntities);
		eventBus.addListener(this::registerEntityAttributes);

		if (dist.isClient()) {
			container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
			eventBus.addListener(ClientHandler::registerEntityRenders);
		}
	}

	public void setupEntities(RegisterSpawnPlacementsEvent event) {
		event.register(FriendRegistry.FRIEND.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				DistantFriend::checkFriendSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
	}

	public void registerEntityAttributes(EntityAttributeCreationEvent event) {
		event.put(FriendRegistry.FRIEND.get(), DistantFriend.createAttributes().build());
	}
}