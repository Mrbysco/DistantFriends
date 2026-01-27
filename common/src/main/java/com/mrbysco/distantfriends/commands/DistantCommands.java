package com.mrbysco.distantfriends.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mrbysco.distantfriends.CommonClass;
import com.mrbysco.distantfriends.Constants;
import com.mrbysco.distantfriends.platform.Services;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.Mannequin;
import net.minecraft.world.phys.Vec3;

public class DistantCommands {
	public static void initializeCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
		final LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(Constants.MOD_ID);
		// Add spawnFriend command that has optional argument for name
		root.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
				.then(Commands.literal("spawnFriend")
						.executes((commandContext) ->
								spawnFriend(commandContext, commandContext.getSource().getPosition())
						)
						.then(
								Commands.argument("pos", Vec3Argument.vec3())
										.executes(
												context -> spawnFriend(
														context,
														Vec3Argument.getVec3(context, "pos")
												)
										)
						)
				);
		dispatcher.register(root);
	}

	private static int spawnFriend(CommandContext<CommandSourceStack> context, Vec3 position) {
		final ServerLevel level = context.getSource().getLevel();
		BlockPos pos = BlockPos.containing(position);

		Mannequin friend = EntityType.MANNEQUIN.create(level, EntitySpawnReason.COMMAND);
		if (friend != null) {
			friend.snapTo(pos, 0.0F, 0.0F);

			// Mark the mannequin as a Distant Friend
			Services.PLATFORM.attachFriendData(friend);

			// Attach skin and name data
			CommonClass.attachSkin(friend);

			level.addFreshEntityWithPassengers(friend);
			return 0;
		}

		return 1;
	}
}
