package com.mrbysco.distantfriends.mixin;

import com.mrbysco.distantfriends.CommonClass;
import com.mrbysco.distantfriends.DistantFriendsFabric;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.Mannequin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

	@Inject(method = "tick()V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V",
					shift = At.Shift.AFTER))
	public void distantfriends$tickFriend(CallbackInfo ci) {
		LivingEntity entity = (LivingEntity) (Object) this;
		if (entity instanceof Mannequin friend && friend.hasAttached(DistantFriendsFabric.IS_FRIEND)) {
			CommonClass.onFriendTick(friend);
		}

	}
}
