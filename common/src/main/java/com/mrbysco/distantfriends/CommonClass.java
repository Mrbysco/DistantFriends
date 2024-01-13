package com.mrbysco.distantfriends;

import com.mrbysco.distantfriends.registration.FriendRegistry;

public class CommonClass {

	public static void init() {
		FriendRegistry.loadClass();
	}
}