package me.honkling.mhplus.commands.impl;

import com.mojang.brigadier.context.CommandContext;
import me.honkling.mhplus.MHPlusClient;
import me.honkling.mhplus.util.SettingsManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

public class DefaultCommand {
    public static int execute(CommandContext<FabricClientCommandSource> context) {
        MutableText settingsComponent = new LiteralText("MH+ Settings\n").formatted(Formatting.GRAY);
        SettingsManager manager = SettingsManager.Instance;
        SettingsManager.Settings settings = manager.settings;

        settingsComponent = settingsComponent
                .append(manager.formatSetting("hideAdvertisements", settings.isHideAdvertisements()))
                .append(manager.formatSetting("hideNpcMessages", settings.isHideNpcMessages()))
                .append(manager.formatSetting("hideMarketAdvertisements", settings.isHideMarketAdvertisements()))
                .append(manager.formatSetting("trackServers", settings.isTrackServers()))
                .append(manager.formatSetting("hideMinehutBroadcasts", settings.isHideMinehutBroadcasts()))
                .append(manager.formatSetting("protectAdvertisementTypos", settings.isProtectAdvertisementTypos()))
                .append(new LiteralText("\n\nClick on the setting to toggle it.").formatted(Formatting.GRAY));

        context.getSource().getPlayer().sendMessage(MHPlusClient.prefix(settingsComponent), false);
        return 1;
    }
}
