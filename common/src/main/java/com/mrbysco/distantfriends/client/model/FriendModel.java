package com.mrbysco.distantfriends.client.model;

import com.mrbysco.distantfriends.entity.DistantFriend;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;

public class FriendModel extends PlayerModel<DistantFriend> {

	public FriendModel(ModelPart modelPart, boolean slim) {
		super(modelPart, slim);

		this.hat.setRotation(0.0F, -1.75F, 0.0F);
		this.rightSleeve.setRotation(-5.0F, 2.0F, 0.0F);
	}

	@Override
	public void setupAnim(DistantFriend friend, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(friend, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

		//Make crouch if looked at
		this.crouching = friend.isCrouching();

		if (friend.isAggressive()) {
			rightArm.xRot = -1.0F;
		}
	}
}
