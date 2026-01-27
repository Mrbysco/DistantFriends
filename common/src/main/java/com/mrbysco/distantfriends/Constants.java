package com.mrbysco.distantfriends;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {

	public static final String MOD_ID = "distantfriends";
	public static final String MOD_NAME = "Distant Friends";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

	public static Identifier modLoc(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}