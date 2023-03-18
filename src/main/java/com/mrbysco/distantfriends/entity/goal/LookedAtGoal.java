package com.mrbysco.distantfriends.entity.goal;

import com.mrbysco.distantfriends.DistantFriends;
import com.mrbysco.distantfriends.entity.DistantFriend;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class LookedAtGoal extends Goal {
	private final TargetingConditions lookTargeting = TargetingConditions.forNonCombat().range(256);
	@Nullable
	private Player player;
	private int lookTimer = 240;
	private final DistantFriend friend;

	public LookedAtGoal(DistantFriend friend) {
		this.friend = friend;
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
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
	public void stop() {

	}

	@Override
	public void tick() {
		DistantFriends.LOGGER.info("{}", player);
		if (this.lookTimer <= 0) {
			friend.setLookedAt(false);
			this.lookTimer = 0;
		} else {
			this.lookTimer--;
		}

		boolean inView = false;
		if (this.player != null) {
			if (friend.isLookingAtMe(player) && !friend.isLookedAt()) {
				friend.setLookedAt(true);
				this.lookTimer = 240;
				friend.getNavigation().stop();
				friend.lookAt(player, 45.0F, 45.0F);
			}

			if (player.hasLineOfSight(friend)) {
				inView = true;
			}
		}

		if (friend.isInView() != inView) {
			friend.setInView(inView);
		}
	}
}
