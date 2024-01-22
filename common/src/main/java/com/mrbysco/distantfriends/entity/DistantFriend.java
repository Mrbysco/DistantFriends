package com.mrbysco.distantfriends.entity;

import com.mojang.authlib.GameProfile;
import com.mrbysco.distantfriends.FriendNamesCache;
import com.mrbysco.distantfriends.entity.goal.LookedAtGoal;
import com.mrbysco.distantfriends.platform.Services;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class DistantFriend extends PathfinderMob {
	private static final EntityDataAccessor<Optional<GameProfile>> GAMEPROFILE = SynchedEntityData.defineId(DistantFriend.class, Services.PLATFORM.getGameProfileSerializer());
	private static final EntityDataAccessor<Boolean> DATA_IN_VIEW = SynchedEntityData.defineId(DistantFriend.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> DATA_LOOKED_AT = SynchedEntityData.defineId(DistantFriend.class, EntityDataSerializers.BOOLEAN);

	private final NonNullList<ItemStack> handItems = NonNullList.withSize(2, ItemStack.EMPTY);
	private final NonNullList<ItemStack> armorItems = NonNullList.withSize(4, ItemStack.EMPTY);
	private final TargetingConditions findPlayerCondition = TargetingConditions.forNonCombat().range(16.0D);

	public DistantFriend(EntityType<? extends PathfinderMob> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(GAMEPROFILE, Optional.empty());
		this.entityData.define(DATA_IN_VIEW, false);
		this.entityData.define(DATA_LOOKED_AT, false);
	}

	public Optional<GameProfile> getGameProfile() {
		return entityData.get(GAMEPROFILE);
	}

	public void setGameProfile(GameProfile playerProfile) {
		this.entityData.set(GAMEPROFILE, Optional.ofNullable(playerProfile));
	}

	public boolean isLookedAt() {
		return this.entityData.get(DATA_LOOKED_AT);
	}

	public void setLookedAt(boolean lookedAt) {
		this.entityData.set(DATA_LOOKED_AT, lookedAt);
	}

	public boolean isInView() {
		return this.entityData.get(DATA_IN_VIEW);
	}

	public void setInView(boolean inView) {
		this.entityData.set(DATA_IN_VIEW, inView);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new LookedAtGoal(this));
		this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 128.0F, 0.75F, false));
		this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, Player.class, 32.0F, 1.0F, 1.33D, EntitySelector.NO_SPECTATORS::test));

		this.goalSelector.addGoal(6, new StrollWhenOutOfSight(this));
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.FOLLOW_RANGE, 35.0D)
				.add(Attributes.MOVEMENT_SPEED, (double) 0.23F)
				.add(Attributes.ATTACK_DAMAGE, 3.0D);
	}

	public Iterable<ItemStack> getHandSlots() {
		return this.handItems;
	}

	public Iterable<ItemStack> getArmorSlots() {
		return this.armorItems;
	}

	public ItemStack getItemBySlot(EquipmentSlot slotIn) {
		return switch (slotIn.getType()) {
			case HAND -> this.handItems.get(slotIn.getIndex());
			case ARMOR -> this.armorItems.get(slotIn.getIndex());
		};
	}

	@Override
	public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack stack) {
		this.verifyEquippedItem(stack);
		switch (equipmentSlot.getType()) {
			case HAND -> this.onEquipItem(equipmentSlot, this.handItems.set(equipmentSlot.getIndex(), stack), stack);
			case ARMOR -> this.onEquipItem(equipmentSlot, this.armorItems.set(equipmentSlot.getIndex(), stack), stack);
		}
	}

	@Override
	public HumanoidArm getMainArm() {
		return HumanoidArm.RIGHT;
	}

	@Override
	public boolean isInvisibleTo(Player player) {
		if (player.hasLineOfSight(this) && this.tickCount < 100) {
			return true;
		}
		return super.isInvisibleTo(player);
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		this.discard();
		return super.hurt(source, amount);
	}

	@Override
	public void aiStep() {
		if (this.tickCount > 80 && tickCount % 20 == 0) {
			if (!this.level().getNearbyPlayers(findPlayerCondition, this, this.getBoundingBox().inflate(16.0D, 32.0D, 16.0D)).isEmpty()) {
				for (int i = 0; i < 20; ++i) {
					double d0 = this.random.nextGaussian() * 0.02D;
					double d1 = this.random.nextGaussian() * 0.02D;
					double d2 = this.random.nextGaussian() * 0.02D;
					this.level().addParticle(ParticleTypes.POOF, this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D), d0, d1, d2);
				}
				this.discard();
			}
		}

		super.aiStep();
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putBoolean("inView", isInView());
		tag.putBoolean("lookedAt", isLookedAt());
		tag.putBoolean("gameProfileExists", entityData.get(GAMEPROFILE).isPresent());
		if (getGameProfile().isPresent()) {
			tag.put("gameProfile", NbtUtils.writeGameProfile(new CompoundTag(), entityData.get(GAMEPROFILE).get()));
		}
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		setInView(compound.getBoolean("inView"));
		setLookedAt(compound.getBoolean("lookedAt"));
		entityData.set(GAMEPROFILE, !compound.getBoolean("gameProfileExists") ? Optional.empty() :
				Optional.ofNullable(NbtUtils.readGameProfile(compound.getCompound("gameProfile"))));
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
		SpawnGroupData data = super.finalizeSpawn(level, difficultyIn, reason, spawnDataIn, dataTag);

		List<String> friends = FriendNamesCache.nameList;
		if (!friends.isEmpty()) {
			String name = friends.get(random.nextInt(friends.size()));
//			DistantFriends.LOGGER.info("Spawned Distant friend with name {}", name);
			SkullBlockEntity.fetchGameProfile(name)
					.thenAccept(
							profile -> this.setGameProfile(profile.orElse(new GameProfile(Util.NIL_UUID, name)))
					);
			this.getGameProfile().ifPresent(profile -> setCustomName(Component.literal(profile.getName())));
		}

		return data;
	}


	public boolean isLookingAtMe(Player player) {
		Vec3 vec3 = player.getViewVector(1.0F).normalize();
		Vec3 offsetVec = new Vec3(this.getX() - player.getX(), this.getEyeY() - player.getEyeY(), this.getZ() - player.getZ());
		double d0 = offsetVec.length();
		offsetVec = offsetVec.normalize();
		double d1 = vec3.dot(offsetVec);
		return d1 > 1.0D - 0.025D / d0 && player.hasLineOfSight(this);
	}

	public static boolean checkFriendSpawn(EntityType<? extends DistantFriend> entityType, ServerLevelAccessor levelAccessor,
										   MobSpawnType spawnType, BlockPos pos, RandomSource random) {
		return levelAccessor.getDifficulty() != Difficulty.PEACEFUL && isDarkEnoughToSpawn(levelAccessor, pos, random) &&
				checkMobSpawnRules(entityType, levelAccessor, spawnType, pos, random);
	}

	public static boolean isDarkEnoughToSpawn(ServerLevelAccessor levelAccessor, BlockPos pos, RandomSource random) {
		if (levelAccessor.getBrightness(LightLayer.SKY, pos) > random.nextInt(32)) {
			return false;
		} else if (levelAccessor.getBrightness(LightLayer.BLOCK, pos) > 0) {
			return false;
		} else {
			int i = levelAccessor.getLevel().isThundering() ? levelAccessor.getMaxLocalRawBrightness(pos, 10) : levelAccessor.getMaxLocalRawBrightness(pos);
			return i <= random.nextInt(8);
		}
	}

	static class StrollWhenOutOfSight extends WaterAvoidingRandomStrollGoal {
		private final DistantFriend friend;

		public StrollWhenOutOfSight(DistantFriend friend) {
			super(friend, 1.0D);
			this.friend = friend;
		}

		@Override
		public boolean canUse() {
			return super.canUse() && !friend.isInView();
		}
	}
}
