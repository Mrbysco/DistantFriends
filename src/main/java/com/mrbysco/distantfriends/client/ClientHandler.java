package com.mrbysco.distantfriends.client;

import com.mrbysco.distantfriends.client.renderer.FriendRenderer;
import com.mrbysco.distantfriends.registry.FriendRegistry;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class ClientHandler {
	public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(FriendRegistry.FRIEND.get(), FriendRenderer::new);
	}
}
