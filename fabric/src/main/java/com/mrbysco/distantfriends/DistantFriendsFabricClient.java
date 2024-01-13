package com.mrbysco.distantfriends;

import com.mrbysco.distantfriends.client.renderer.FriendRenderer;
import com.mrbysco.distantfriends.registration.FriendRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class DistantFriendsFabricClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(FriendRegistry.FRIEND.get(), FriendRenderer::new);
	}
}
