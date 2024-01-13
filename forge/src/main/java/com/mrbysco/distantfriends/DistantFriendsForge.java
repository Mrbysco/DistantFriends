package com.mrbysco.distantfriends;

import com.mrbysco.distantfriends.client.ClientHandler;
import com.mrbysco.distantfriends.config.FriendConfigForge;
import com.mrbysco.distantfriends.entity.DistantFriend;
import com.mrbysco.distantfriends.registration.FriendRegistry;
import com.mrbysco.distantfriends.registry.FriendSerializers;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public class DistantFriendsForge {

	public DistantFriendsForge() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FriendConfigForge.commonSpec);
		eventBus.register(FriendConfigForge.class);

		FriendSerializers.ENTITY_DATA_SERIALIZER.register(eventBus);
		CommonClass.init();

		eventBus.addListener(this::setupEntities);
		eventBus.addListener(this::registerEntityAttributes);

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			eventBus.addListener(ClientHandler::registerEntityRenders);
		});
	}

	public void setupEntities(SpawnPlacementRegisterEvent event) {
		event.register(FriendRegistry.FRIEND.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DistantFriend::checkFriendSpawn, SpawnPlacementRegisterEvent.Operation.AND);
	}

	public void registerEntityAttributes(EntityAttributeCreationEvent event) {
		event.put(FriendRegistry.FRIEND.get(), DistantFriend.createAttributes().build());
	}
}