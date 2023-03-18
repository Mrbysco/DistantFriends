package com.mrbysco.distantfriends.client.model;

import com.google.common.collect.ImmutableList;
import com.mrbysco.distantfriends.entity.DistantFriend;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class FriendModel extends PlayerModel<DistantFriend> {
	public boolean lookedAt;

	public FriendModel(ModelPart modelPart, boolean slim) {
		super(modelPart, slim);

		this.hat.setRotation(0.0F, -1.75F, 0.0F);
		this.rightSleeve.setRotation(-5.0F, 2.0F, 0.0F);
	}

	public static MeshDefinition createStatueMesh(CubeDeformation cubeDeformation, boolean slim) {
		MeshDefinition meshdefinition = PlayerModel.createMesh(cubeDeformation, slim);
		PartDefinition partdefinition = meshdefinition.getRoot();

		partdefinition.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, cubeDeformation.extend(2.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		if (slim) {
			partdefinition.addOrReplaceChild("right_sleeve", CubeListBuilder.create().texOffs(40, 32).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, cubeDeformation.extend(0.25F)), PartPose.offset(-5.0F, 2.5F, 0.0F));
		} else {
			partdefinition.addOrReplaceChild("right_sleeve", CubeListBuilder.create().texOffs(40, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, cubeDeformation.extend(0.25F)), PartPose.offset(-5.0F, 2.0F, 0.0F));
		}
		return meshdefinition;
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

	public static void animatePunch(ModelPart rightArm, boolean aggressive, float unused, float unused2) {

		AnimationUtils.bobModelPart(rightArm, unused2, -1.0F);
	}

	@Override
	protected Iterable<ModelPart> headParts() {
		return ImmutableList.of(this.head, this.hat);
	}
}
