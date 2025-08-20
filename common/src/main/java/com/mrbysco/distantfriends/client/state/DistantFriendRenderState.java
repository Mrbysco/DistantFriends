package com.mrbysco.distantfriends.client.state;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;

public class DistantFriendRenderState extends HumanoidRenderState {
	public PlayerSkin skin = DefaultPlayerSkin.getDefaultSkin();
	public int id;
	public String name = "Steve";
	
	public boolean isSpectator;
	public boolean swinging;
}
