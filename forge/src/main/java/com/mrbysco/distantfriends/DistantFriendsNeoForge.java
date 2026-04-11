package com.mrbysco.distantfriends;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mrbysco.distantfriends.commands.DistantCommands;
import com.mrbysco.distantfriends.config.FriendConfig;
import com.mrbysco.distantfriends.util.FriendSpawner;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.decoration.Mannequin;
import net.minecraft.world.level.CustomSpawner;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

@Mod(Constants.MOD_ID)
public class DistantFriendsNeoForge {
	private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Constants.MOD_ID);
	public static final Supplier<AttachmentType<Boolean>> IS_FRIEND = ATTACHMENT_TYPES.register("is_friend", () -> AttachmentType.builder(() -> false)
			.serialize(Codec.BOOL.fieldOf("is_friend")).build());

	public DistantFriendsNeoForge(IEventBus eventBus, ModContainer container, Dist dist) {
		container.registerConfig(ModConfig.Type.COMMON, FriendConfig.commonSpec);

		ATTACHMENT_TYPES.register(eventBus);

		NeoForge.EVENT_BUS.addListener(this::onFriendDamage);
		NeoForge.EVENT_BUS.addListener(this::onFriendTick);
		NeoForge.EVENT_BUS.addListener(this::onLevelLoad);
		NeoForge.EVENT_BUS.addListener(this::onCommandRegister);

		if (dist.isClient()) {
			container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
		}
	}

	private void onFriendDamage(LivingIncomingDamageEvent event) {
		if (event.getEntity() instanceof Mannequin mannequin && mannequin.hasData(IS_FRIEND)) {
			CommonClass.onFriendDamage(mannequin);
		}
	}

	private void onFriendTick(EntityTickEvent.Pre event) {
		if (event.getEntity() instanceof Mannequin mannequin && mannequin.hasData(IS_FRIEND)) {
			CommonClass.onFriendTick(mannequin);
		}
	}

	private void onLevelLoad(LevelEvent.Load event) {
		if (event.getLevel() instanceof ServerLevel level) {
			level.customSpawners = ImmutableList.<CustomSpawner>builder()
					.addAll(level.customSpawners)
					.add(new FriendSpawner())
					.build();
		}
	}

	public void onCommandRegister(RegisterCommandsEvent event) {
		DistantCommands.initializeCommands(event.getDispatcher());
	}
}