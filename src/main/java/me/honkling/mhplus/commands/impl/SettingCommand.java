package me.honkling.mhplus.commands.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.honkling.mhplus.MHPlusClient;
import me.honkling.mhplus.util.SettingsManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class SettingCommand {
    private static final String name = "setting";

    public static LiteralArgumentBuilder<FabricClientCommandSource> register() {
        return literal(name)
                .then(argument("setting", StringArgumentType.word()).executes(SettingCommand::execute));
    }

    public static int execute(CommandContext<FabricClientCommandSource> context) {
        String setting = context.getArgument("setting", String.class);
        ClientPlayerEntity client = context.getSource().getPlayer();
        SettingsManager.Settings settings = SettingsManager.Instance.settings;
        boolean newValue;
        switch (setting) {
            case "hideAdvertisements" -> {
                newValue = !settings.isHideAdvertisements();
                settings.setHideAdvertisements(newValue);
            }
            case "hideNpcMessages" -> {
                newValue = !settings.isHideNpcMessages();
                settings.setHideNpcMessages(newValue);
            }
            case "hideMarketAdvertisements" -> {
                newValue = !settings.isHideMarketAdvertisements();
                settings.setHideMarketAdvertisements(newValue);
            }
            case "trackServers" -> {
                newValue = !settings.isTrackServers();
                settings.setTrackServers(newValue);
            }
            case "hideMinehutBroadcasts" -> {
                newValue = !settings.isHideMinehutBroadcasts();
                settings.setHideMinehutBroadcasts(newValue);
            }
            case "protectAdvertisementTypos" -> {
                newValue = !settings.isProtectAdvertisementTypos();
                settings.setProtectAdvertisementTypos(newValue);
            }
            default -> {
                MutableText error = Text.literal("Invalid setting provided.")
                        .formatted(Formatting.GRAY);
                client.sendMessage(MHPlusClient.prefix(error), false);
                return 1;
            }
        }

        MutableText text = Text.literal(setting)
                .formatted(Formatting.WHITE)
                .append(
                        Text.literal(String.format(" has been %s.", newValue ? "enabled" : "disabled"))
                                .formatted(Formatting.GRAY)
                );

        client.sendMessage(MHPlusClient.prefix(text), false);
        return 1;
    }
}
