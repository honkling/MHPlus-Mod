package me.honkling.minehutplus.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.honkling.minehutplus.util.SettingsManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

public class MinehutPlusCommand {
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("minehutplus")
				.then(literal("setting")
						.then(argument("setting", StringArgumentType.word()).executes((context) -> {
							String setting = context.getArgument("setting", String.class);
							ClientPlayerEntity client = context.getSource().getPlayer();
							SettingsManager settings = SettingsManager.Instance;
							boolean newValue;
							switch (setting) {
								case "hideAdvertisements" -> {
									newValue = !settings.hideAdvertisements();
									settings.hideAdvertisements(newValue);
								}
								case "hideNpcMessages" -> {
									newValue = !settings.hideNpcMessages();
									settings.hideNpcMessages(newValue);
								}
								case "hideMarketAdvertisements" -> {
									newValue = !settings.hideMarketAdvertisements();
									settings.hideMarketAdvertisements(newValue);
								}
								default -> {
									client.sendChatMessage("&3Invalid setting provided.");
									return 1;
								}
							}

							MutableText text = new LiteralText(setting)
									.formatted(Formatting.AQUA)
									.append(
											new LiteralText(String.format(" has been %s.", newValue ? "enabled" : "disabled"))
													.formatted(Formatting.DARK_AQUA)
									);

							client.sendMessage(text, false);
							return 1;
						})))
				.executes((context) -> {
					MutableText settings = new LiteralText("Minehut+ Settings\n").formatted(Formatting.DARK_AQUA);
					SettingsManager manager = SettingsManager.Instance;

					settings = settings
							.append(manager.formatSetting("hideAdvertisements", manager.hideAdvertisements()))
							.append(manager.formatSetting("hideNpcMessages", manager.hideNpcMessages()))
							.append(manager.formatSetting("hideMarketAdvertisements", manager.hideMarketAdvertisements()))
							.append(new LiteralText("\n\nClick on the setting to toggle it.").formatted(Formatting.DARK_AQUA));

					context.getSource().getPlayer().sendMessage(settings, false);
					return 1;
				}));
	}
}
