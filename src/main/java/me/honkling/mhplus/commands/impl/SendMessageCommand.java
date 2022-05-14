package me.honkling.mhplus.commands.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.honkling.mhplus.util.SettingsManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

public class SendMessageCommand {

    private static final String name = "sendMessage";

    public static LiteralArgumentBuilder<FabricClientCommandSource> register() {
        return literal(name).executes(SendMessageCommand::execute);
    }

    public static int execute(CommandContext<FabricClientCommandSource> context) {
        String message = SettingsManager.Instance.message;

        if (message == null) return 1;

        SettingsManager.Instance.excuseMessage = true;
        context.getSource().getPlayer().sendChatMessage(message);

        return 1;
    }
}
