package com.mrbysco.distantfriends.client.renderer;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrbysco.distantfriends.client.model.FriendModel;
import com.mrbysco.distantfriends.entity.DistantFriend;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class FriendRenderer extends MobRenderer<DistantFriend, FriendModel> {
	private final FriendModel playerModel;
	private final FriendModel slimPlayerModel;
	public static final ResourceLocation defaultTexture = DefaultPlayerSkin.getDefaultSkin();

	public FriendRenderer(EntityRendererProvider.Context context) {
		this(context, false);
	}

	public FriendRenderer(EntityRendererProvider.Context context, boolean slim) {
		super(context, new FriendModel(context.bakeLayer(ModelLayers.PLAYER), slim), 0.0F);
		this.playerModel = new FriendModel(context.bakeLayer(ModelLayers.PLAYER), false);
		this.slimPlayerModel = new FriendModel(context.bakeLayer(ModelLayers.PLAYER_SLIM), true);

		this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel(context.bakeLayer(slim ? ModelLayers.PLAYER_SLIM_INNER_ARMOR :
				ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel(context.bakeLayer(slim ? ModelLayers.PLAYER_SLIM_OUTER_ARMOR : ModelLayers.PLAYER_OUTER_ARMOR))));
		this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
		this.addLayer(new ElytraLayer<>(this, context.getModelSet()));
		this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
	}

	@Override
	public void render(DistantFriend playerStatue, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLightIn) {
		this.model = playerStatue.isSlim() ? this.slimPlayerModel : playerModel;

		super.render(playerStatue, entityYaw, partialTicks, poseStack, bufferSource, packedLightIn);
	}

	protected void scale(DistantFriend friend, PoseStack poseStack, float partialTickTime) {
		float f = 0.9375F;
		poseStack.scale(f, f, f);
	}

	@Override
	public ResourceLocation getTextureLocation(DistantFriend friend) {
		return friend.getGameProfile()
				.map(this::getSkin)
				.orElse(defaultTexture);
	}

	private ResourceLocation getSkin(GameProfile gameProfile) {
		if (!gameProfile.isComplete()) {
			return defaultTexture;
		} else {
			final Minecraft minecraft = Minecraft.getInstance();
			SkinManager skinManager = minecraft.getSkinManager();
			final Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> loadSkinFromCache = skinManager.getInsecureSkinInformation(gameProfile); // returned map may or may not be typed
			if (loadSkinFromCache.containsKey(MinecraftProfileTexture.Type.SKIN)) {
				return skinManager.registerTexture(loadSkinFromCache.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
			} else {
				return DefaultPlayerSkin.getDefaultSkin(gameProfile.getId());
			}
		}
	}
}
