package me.honkling.mhplus.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.honkling.mhplus.commands.impl.*;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

public class MHPlusCommand {
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("mhplus")
				.then(SettingCommand.register())
				.then(SendMessageCommand.register())
				.then(HistoryCommand.register())
				.then(BlockCommand.register())
				.executes(DefaultCommand::execute)
		);
	}
}
