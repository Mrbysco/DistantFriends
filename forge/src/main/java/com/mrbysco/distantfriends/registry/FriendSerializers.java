package com.mrbysco.distantfriends.registry;

import com.mrbysco.distantfriends.Constants;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.item.component.ResolvableProfile;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Optional;
import java.util.function.Supplier;

public class FriendSerializers {
	public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZER = DeferredRegister.create(NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, Constants.MOD_ID);

	public static final Supplier<EntityDataSerializer<Optional<ResolvableProfile>>> OPTIONAL_RESOLVABLE_PROFILE = ENTITY_DATA_SERIALIZER.register("optional_resolvable_profile", () -> EntityDataSerializer.forValueType(
			ResolvableProfile.STREAM_CODEC.apply(ByteBufCodecs::optional)
	));
}
