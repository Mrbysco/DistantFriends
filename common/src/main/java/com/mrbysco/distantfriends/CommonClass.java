package com.mrbysco.distantfriends;

import com.mojang.datafixers.util.Either;
import com.mrbysco.distantfriends.config.FriendConfig;
import com.mrbysco.distantfriends.platform.Services;
import com.mrbysco.distantfriends.util.FriendNamesCache;
import com.mrbysco.distantfriends.util.PlayerData;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.ClientAsset;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.decoration.Mannequin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CommonClass {
	private static final List<Item> VALID_ITEMS = List.of(
			Items.WOODEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD, Items.GOLDEN_SWORD, Items.DIAMOND_SWORD,
			Items.NETHERITE_SWORD, Items.WOODEN_SHOVEL, Items.STONE_SHOVEL, Items.IRON_SHOVEL, Items.GOLDEN_SHOVEL,
			Items.DIAMOND_SHOVEL, Items.NETHERITE_SHOVEL, Items.WOODEN_PICKAXE, Items.STONE_PICKAXE,
			Items.IRON_PICKAXE, Items.GOLDEN_PICKAXE, Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE,
			Items.WOODEN_AXE, Items.STONE_AXE, Items.IRON_AXE, Items.GOLDEN_AXE, Items.DIAMOND_AXE,
			Items.NETHERITE_AXE, Items.WOODEN_HOE, Items.STONE_HOE, Items.IRON_HOE, Items.GOLDEN_HOE,
			Items.DIAMOND_HOE, Items.NETHERITE_HOE, Items.BOW, Items.CROSSBOW, Items.SHIELD, Items.STICK
	);

	private static final TargetingConditions findPlayerCondition = TargetingConditions.forNonCombat().range(10.0D);
	private static final TargetingConditions watchTargeting = TargetingConditions.forNonCombat().range(256.0D);

	private static final Map<EntityReference<Mannequin>, List<Item>> friendItemMap = new HashMap<>();
	private static final Map<EntityReference<Mannequin>, Integer> crouchIntervalMap = new HashMap<>();
	private static final Map<EntityReference<Mannequin>, Integer> punchIntervalMap = new HashMap<>();

	public static void onFriendTick(Mannequin friend) {
		if (friend.level() instanceof ServerLevel serverLevel && friend.isAlive()) {
			if (friend.tickCount > 80 && friend.tickCount % 20 == 0) {
				boolean playerNearby = !serverLevel.getNearbyPlayers(findPlayerCondition, friend,
						friend.getBoundingBox().inflate(16.0D, 32.0D, 16.0D)
				).isEmpty();
				if (playerNearby) {
					// Send entity event 60 (which spawns poof particles)
					serverLevel.broadcastEntityEvent(friend, (byte) 60);
					friend.discard();
				}
			}

			Player player = serverLevel.getNearestPlayer(watchTargeting, friend);
			if (player != null) {
				EntityReference<Mannequin> friendRef = EntityReference.of(friend);
				boolean lookingAtMe = friend.isLookingAtMe(player, 0.025, true, false, new double[]{friend.getEyeY()});
				if (lookingAtMe) {
					RandomSource random = friend.getRandom();
					friend.lookAt(EntityAnchorArgument.Anchor.EYES, player.position());

					if (friend.tickCount % crouchIntervalMap.getOrDefault(friendRef, 10) == 0) {
						crouchIntervalMap.put(friendRef, random.nextInt(50) + 10);
						if (random.nextBoolean()) {
							Pose pose = friend.getPose();
							if (pose == Pose.STANDING)
								pose = Pose.CROUCHING;
							else
								pose = Pose.STANDING;
							friend.setPose(pose);
						}
					}

					if (friend.tickCount % punchIntervalMap.getOrDefault(friendRef, 8) == 0) {
						punchIntervalMap.put(friendRef, random.nextInt(45) + 10);
						if (random.nextBoolean()) {
							if (!friendItemMap.containsKey(friendRef)) {
								//Populate the item list with 4 random items
								List<Item> copyList = new ArrayList<>(VALID_ITEMS);
								Collections.shuffle(copyList);
								List<Item> items = copyList.subList(0, 4);
								friendItemMap.put(friendRef, items);
							}
							List<Item> items = friendItemMap.get(friendRef);
							ItemStack stack = friend.getMainHandItem();
							if (!stack.isEmpty()) {
								friend.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
							} else {
								Item item = items.get(random.nextInt(items.size()));
								friend.setItemInHand(InteractionHand.MAIN_HAND, item.getDefaultInstance());
							}
						}
					}
				} else {
					if (friend.getPose() != Pose.STANDING)
						friend.setPose(Pose.STANDING);
				}
			}
		}
	}

	public static void onFriendDamage(Mannequin friend) {
		if (friend.level() instanceof ServerLevel serverLevel) {
			serverLevel.broadcastEntityEvent(friend, (byte) 60);
			friend.discard();
		}
	}

	public static void attachSkin(Mannequin friend) {
		List<PlayerData> friends = FriendNamesCache.nameList;
		if (!friends.isEmpty()) {
			final PlayerData data = friends.get(friend.getRandom().nextInt(friends.size()));
			final String name = data.name();
			final String texture = data.texture();

			if (FriendConfig.COMMON.showName.get()) {
				friend.setCustomName(Component.literal(name));
				friend.setCustomNameVisible(true);
			}

			// If texture is specified, it will be used regardless of the name
			Optional<ClientAsset.ResourceTexture> skin = Optional.empty();
			if (texture != null && !texture.isEmpty()) {
				Identifier location = Identifier.tryParse(texture);
				if (location != null)
					skin = Optional.of(new ClientAsset.ResourceTexture(location));
			}
			// If body type is not specified, it will choose the default (wide)
			Optional<PlayerModelType> bodyType = Optional.empty();
			if (data.bodyType() != null) {
				bodyType = Optional.of(data.bodyType());
			}

			PlayerSkin.Patch patch = PlayerSkin.Patch.EMPTY;
			if (skin.isPresent() || bodyType.isPresent()) {
				patch = PlayerSkin.Patch.create(
						skin, Optional.empty(), Optional.empty(), bodyType
				);
			}

			friend.setProfile(new ResolvableProfile.Dynamic(Either.left(name), patch));
			friend.setHideDescription(true);
		}
	}
}