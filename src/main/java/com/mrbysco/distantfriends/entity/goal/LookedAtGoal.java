package com.mrbysco.distantfriends.entity.goal;

import com.mrbysco.distantfriends.entity.DistantFriend;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

public class LookedAtGoal extends Goal {
	private final TargetingConditions lookTargeting = TargetingConditions.forNonCombat().range(256);
	@Nullable
	private Player player;
	private final DistantFriend friend;
	private final Random random;
	private int crouchInterval = 10;
	private int punchInterval = 5;

	public LookedAtGoal(DistantFriend friend) {
		this.friend = friend;
		this.random = friend.getRandom();
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
	}

	@Override
	public boolean canUse() {
		this.player = friend.level.getNearestPlayer(this.lookTargeting, friend);
		return this.player != null;
	}

	public boolean canContinueToUse() {
		if (this.player != null && !this.player.isAlive()) {
			return false;
		} else return friend.blockPosition().distManhattan(this.player.blockPosition()) <= 512;
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void start() {
		super.start();
		friend.setPose(Pose.STANDING);
		friend.setAggressive(false);
	}

	@Override
	public void stop() {
		super.stop();
		friend.setPose(Pose.STANDING);
		friend.setAggressive(false);
	}

	@Override
	public void tick() {
		boolean inView = false;
		if (this.player != null) {
			boolean lookingAtMe = friend.isLookingAtMe(player);
			boolean lookedAt = friend.isLookedAt();
			if (lookingAtMe) {
				if (!lookedAt) {
					friend.setLookedAt(true);
					friend.getNavigation().stop();
				}
			} else {
				friend.setLookedAt(false);
			}

			if (player.hasLineOfSight(friend)) {
				inView = true;
			}
		}

		if (friend.isInView() != inView) {
			friend.setInView(inView);
		}

		if (friend.isLookedAt()) {
			if (player != null)
				friend.getLookControl().setLookAt(player.getX(), player.getY(), player.getZ());

			if (friend.tickCount % crouchInterval == 0) {
				crouchInterval = random.nextInt(50) + 10;
				Pose pose = friend.isCrouching() ? Pose.STANDING : Pose.CROUCHING;
				friend.setPose(pose);
			}

			if (friend.tickCount % punchInterval == 0) {
				punchInterval = random.nextInt(25) + 10;
				friend.setAggressive(!friend.isAggressive());
			}
		}
	}
}
