package com.mrbysco.distantfriends.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrbysco.distantfriends.client.model.FriendModel;
import com.mrbysco.distantfriends.client.state.DistantFriendRenderState;
import com.mrbysco.distantfriends.entity.DistantFriend;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class FriendRenderer extends HumanoidMobRenderer<DistantFriend, DistantFriendRenderState, FriendModel> {
	public static final PlayerSkin defaultSkin = DefaultPlayerSkin.getDefaultSkin();
	private final FriendModel playerModel;
	private final FriendModel slimPlayerModel;
	public static boolean isSlim = false;

	public FriendRenderer(EntityRendererProvider.Context context) {
		this(context, false);
	}

	public FriendRenderer(EntityRendererProvider.Context context, boolean slim) {
		super(context, new FriendModel(context.bakeLayer(ModelLayers.PLAYER), slim), 0.0F);
		this.playerModel = new FriendModel(context.bakeLayer(ModelLayers.PLAYER), false);
		this.slimPlayerModel = new FriendModel(context.bakeLayer(ModelLayers.PLAYER_SLIM), true);
		this.addLayer(
				new HumanoidArmorLayer<>(
						this,
						new HumanoidArmorModel<>(context.bakeLayer(slim ? ModelLayers.PLAYER_SLIM_INNER_ARMOR : ModelLayers.PLAYER_INNER_ARMOR)),
						new HumanoidArmorModel<>(context.bakeLayer(slim ? ModelLayers.PLAYER_SLIM_OUTER_ARMOR : ModelLayers.PLAYER_OUTER_ARMOR)),
						context.getEquipmentRenderer()
				)
		);
		this.addLayer(new ItemInHandLayer<>(this));
		this.addLayer(new WingsLayer<>(this, context.getModelSet(), context.getEquipmentRenderer()));
		this.addLayer(new CustomHeadLayer<>(this, context.getModelSet()));
	}

	@Override
	public DistantFriendRenderState createRenderState() {
		return new DistantFriendRenderState();
	}

	@Override
	public void extractRenderState(DistantFriend friend, DistantFriendRenderState renderState, float partialTick) {
		super.extractRenderState(friend, renderState, partialTick);
		HumanoidMobRenderer.extractHumanoidRenderState(friend, renderState, partialTick, this.itemModelResolver);
		renderState.leftArmPose = getFriendArmPose(friend, HumanoidArm.LEFT);
		renderState.rightArmPose = getFriendArmPose(friend, HumanoidArm.RIGHT);
		renderState.swinging = friend.swinging;
		renderState.isSpectator = friend.isSpectator();

		renderState.skin = getSkin(friend.getProfile().orElse(null));
		renderState.id = friend.getId();
		renderState.name = friend.getProfile().isPresent() ?
				friend.getProfile().get().gameProfile().getName() : "unknown";
	}

	@Override
	public void render(DistantFriendRenderState statueRenderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLightIn) {
		this.isSlim = statueRenderState.skin != null && statueRenderState.skin.model() == PlayerSkin.Model.SLIM;
		this.model = isSlim ? this.slimPlayerModel : playerModel;
		super.render(statueRenderState, poseStack, bufferSource, packedLightIn);
	}

	@Override
	protected void scale(DistantFriendRenderState renderState, PoseStack poseStack) {
		float f = 0.9375F;
		poseStack.scale(f, f, f);
	}

	@Override
	public ResourceLocation getTextureLocation(DistantFriendRenderState renderState) {
		return renderState.skin.texture();
	}

	private PlayerSkin getSkin(@Nullable ResolvableProfile profile) {
		SkinManager skinmanager = Minecraft.getInstance().getSkinManager();
		if (profile != null) {
			return skinmanager.getInsecureSkin(profile.gameProfile());
		} else {
			return defaultSkin;
		}
	}

	@Override
	public Vec3 getRenderOffset(DistantFriendRenderState renderState) {
		Vec3 vec3 = super.getRenderOffset(renderState);
		return renderState.isCrouching ? vec3.add((double) 0.0F, (double) (renderState.scale * -2.0F) / (double) 16.0F, (double) 0.0F) : vec3;
	}

	private static HumanoidModel.ArmPose getFriendArmPose(DistantFriend player, HumanoidArm arm) {
		ItemStack itemstack = player.getItemInHand(InteractionHand.MAIN_HAND);
		ItemStack itemstack1 = player.getItemInHand(InteractionHand.OFF_HAND);
		HumanoidModel.ArmPose humanoidmodel$armpose = getFriendArmPose(player, itemstack, InteractionHand.MAIN_HAND);
		HumanoidModel.ArmPose humanoidmodel$armpose1 = getFriendArmPose(player, itemstack1, InteractionHand.OFF_HAND);
		if (humanoidmodel$armpose.isTwoHanded()) {
			humanoidmodel$armpose1 = itemstack1.isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
		}

		return player.getMainArm() == arm ? humanoidmodel$armpose : humanoidmodel$armpose1;
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	private static HumanoidModel.ArmPose getFriendArmPose(DistantFriend friend, ItemStack stack, InteractionHand hand) {
		return getFriendArmPose(friend, stack, hand, (HumanoidModel.ArmPose) null);
	}

	private static HumanoidModel.ArmPose getFriendArmPose(DistantFriend friend, ItemStack stack, InteractionHand hand, @Nullable HumanoidModel.ArmPose pose) {
		if (pose != null) {
			return pose;
		} else if (stack.isEmpty()) {
			return HumanoidModel.ArmPose.EMPTY;
		} else {
			if (friend.getUsedItemHand() == hand && friend.getUseItemRemainingTicks() > 0) {
				ItemUseAnimation itemuseanimation = stack.getUseAnimation();
				if (itemuseanimation == ItemUseAnimation.BLOCK) {
					return HumanoidModel.ArmPose.BLOCK;
				}

				if (itemuseanimation == ItemUseAnimation.BOW) {
					return HumanoidModel.ArmPose.BOW_AND_ARROW;
				}

				if (itemuseanimation == ItemUseAnimation.SPEAR) {
					return HumanoidModel.ArmPose.THROW_SPEAR;
				}

				if (itemuseanimation == ItemUseAnimation.CROSSBOW) {
					return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
				}

				if (itemuseanimation == ItemUseAnimation.SPYGLASS) {
					return HumanoidModel.ArmPose.SPYGLASS;
				}

				if (itemuseanimation == ItemUseAnimation.TOOT_HORN) {
					return HumanoidModel.ArmPose.TOOT_HORN;
				}

				if (itemuseanimation == ItemUseAnimation.BRUSH) {
					return HumanoidModel.ArmPose.BRUSH;
				}
			} else if (!friend.swinging && stack.is(Items.CROSSBOW) && CrossbowItem.isCharged(stack)) {
				return HumanoidModel.ArmPose.CROSSBOW_HOLD;
			}

			return HumanoidModel.ArmPose.ITEM;
		}
	}
}
