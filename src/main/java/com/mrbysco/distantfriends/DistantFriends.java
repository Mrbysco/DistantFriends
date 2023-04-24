package com.mrbysco.distantfriends;

import com.mojang.logging.LogUtils;
import com.mrbysco.distantfriends.client.ClientHandler;
import com.mrbysco.distantfriends.config.FriendConfig;
import com.mrbysco.distantfriends.registry.FriendRegistry;
import com.mrbysco.distantfriends.registry.FriendSerializers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(DistantFriends.MOD_ID)
public class DistantFriends {
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final String MOD_ID = "distantfriends";

	public DistantFriends() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FriendConfig.commonSpec);
		eventBus.register(FriendConfig.class);

		FriendRegistry.ENTITIES.register(eventBus);
		FriendSerializers.ENTITY_DATA_SERIALIZER.register(eventBus);

		eventBus.addListener(FriendRegistry::setupEntities);
		eventBus.addListener(FriendRegistry::registerEntityAttributes);

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			eventBus.addListener(ClientHandler::registerEntityRenders);
		});
	}
}
