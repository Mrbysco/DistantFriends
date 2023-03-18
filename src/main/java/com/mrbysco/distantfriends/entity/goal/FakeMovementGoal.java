package com.mrbysco.distantfriends.entity.goal;

import com.mrbysco.distantfriends.entity.DistantFriend;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.Random;

public class FakeMovementGoal extends Goal {
	private DistantFriend friend;
	private Random random;
	private int crouchInterval = 10;
	private int punchInterval = 10;

	public FakeMovementGoal(DistantFriend friend) {
		this.friend = friend;
		this.random = friend.getRandom();
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
	}

	@Override
	public boolean canUse() {
		return friend.isLookedAt();
	}

	@Override
	public void start() {
		super.start();
		friend.setPose(Pose.CROUCHING);
	}

	@Override
	public void stop() {
		super.stop();
		friend.setPose(Pose.STANDING);
	}

	@Override
	public void tick() {
		if (friend.tickCount % crouchInterval == 0) {
			crouchInterval = random.nextInt(10) + 5;
			friend.setPose(friend.isCrouching() ? Pose.STANDING : Pose.CROUCHING);
		}

		if (friend.tickCount % punchInterval == 0) {
			punchInterval = random.nextInt(10) + 5;
			friend.setAggressive(!friend.isAggressive());
		}
	}
}
