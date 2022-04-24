package me.honkling.mhplus.commands;

import com.google.gson.Gson;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.honkling.mhplus.MHPlusClient;
import me.honkling.mhplus.util.SettingsManager;
import me.honkling.mhplus.util.serializables.MojangResponse;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

public class MHPlusCommand {
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("mhplus")
				.then(literal("history").executes((context) -> {
					List<SettingsManager.Data.Server> serverList = SettingsManager.Instance.data.getJoinedServers();
					MutableText text = new LiteralText("Viewing past servers.")
							.formatted(Formatting.GRAY);

					context.getSource().getPlayer().sendMessage(MHPlusClient.prefix(text), false);

					MutableText servers = new LiteralText("Here's 30 previously joined servers.\n")
							.formatted(Formatting.GRAY)
							.append(MHPlusClient.prefix(new LiteralText("")));

					int iterations = 0;

					MHPlusClient.logger.warn("[DEBUG] Data has " + serverList.size() + " servers.");

					for(SettingsManager.Data.Server server : serverList.toArray(new SettingsManager.Data.Server[0])) {
						if(iterations > 30) break;
						servers.append(
								new LiteralText(iterations > 0 ? ", " : "")
										.formatted(Formatting.GRAY)
								)
								.append(
										new LiteralText(server.getName())
												.setStyle(
														Style.EMPTY
																.withColor(Formatting.WHITE)
																.withHoverEvent(
																		new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Click to join ")
																				.formatted(Formatting.GRAY)
																				.append(
																						new LiteralText(server.getName())
																								.formatted(Formatting.WHITE)
																				))
																)
																.withClickEvent(
																		new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/join " + server.getName())))
								);
						iterations++;
					}

					context.getSource().getPlayer().sendMessage(MHPlusClient.prefix(servers), false);
					return 1;
				}))
				.then(literal("block")
					.then(argument("player", StringArgumentType.string()).executes((context) -> {
						String blockedPlayerName = context.getArgument("player", String.class);
						ClientPlayerEntity player = context.getSource().getPlayer();

						if(!blockedPlayerName.matches("[0-9a-zA-Z_]{3,16}"))
							return 0;

						UUID uuid;
						if(MHPlusClient.uuidCache.containsKey(blockedPlayerName.toLowerCase()))
							uuid = MHPlusClient.uuidCache.get(blockedPlayerName.toLowerCase());
						else {
							HttpClient client = HttpClient.newHttpClient();
							HttpResponse<String> response = null;
							MojangResponse mojangResponse = new MojangResponse();
							try {
								HttpRequest request = HttpRequest.newBuilder()
										.uri(new URI(String.format("https://api.mojang.com/users/profiles/minecraft/%s", blockedPlayerName)))
										.GET()
										.build();

								response = client.send(request, HttpResponse.BodyHandlers.ofString());
								Gson gson = new Gson();
								mojangResponse = gson.fromJson(response.body(), MojangResponse.class);
							} catch (URISyntaxException | IOException | InterruptedException e) {
								e.printStackTrace();
							}

							if(response == null || response.body() == null || response.statusCode() != 200) {
								Logger logger = MHPlusClient.logger;
								logger.error("Failed to fetch UUID.");
								if(response != null) {
									logger.error("Body: " + response.body());
									logger.error("Status: " + response.statusCode());
								}
								MutableText error = new LiteralText("Failed to fetch UUID. Please try again.")
										.formatted(Formatting.GRAY);
								player.sendMessage(MHPlusClient.prefix(error), false);
								return 1;
							}

							String rawUUID = mojangResponse.getId();
							String formattedUUID = String.format("%s-%s-%s-%s-%s", rawUUID.substring(0,8), rawUUID.substring(8,12), rawUUID.substring(12,16), rawUUID.substring(16,20), rawUUID.substring(20, 32));
							uuid = UUID.fromString(formattedUUID);
							MHPlusClient.uuidCache.put(blockedPlayerName.toLowerCase(), uuid);
						}

						SettingsManager.Data data = SettingsManager.Instance.data;
						List<String> blockedUsers = data.getBlockedUsers();

						if(!SettingsManager.Instance.data.getBlockedUsers().contains(uuid.toString().replaceAll("-", ""))) {
							blockedUsers.add(uuid.toString());
						} else {
							blockedUsers.remove(uuid.toString());
						}
						data.setBlockedUsers(blockedUsers);

						String messageTernary = blockedUsers.contains(uuid.toString()) ? "" : "un";

						MutableText success = new LiteralText(blockedPlayerName)
								.formatted(Formatting.WHITE)
								.append(new LiteralText(" has been " + messageTernary + "blocked.")
										.formatted(Formatting.GRAY));

						player.sendMessage(MHPlusClient.prefix(success), false);

						return 1;
					})))
				.then(literal("setting")
						.then(argument("setting", StringArgumentType.word()).executes((context) -> {
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
									MutableText error = new LiteralText("Invalid setting provided.")
											.formatted(Formatting.GRAY);
									client.sendMessage(MHPlusClient.prefix(error), false);
									return 1;
								}
							}

							MutableText text = new LiteralText(setting)
									.formatted(Formatting.WHITE)
									.append(
											new LiteralText(String.format(" has been %s.", newValue ? "enabled" : "disabled"))
													.formatted(Formatting.GRAY)
									);

							client.sendMessage(MHPlusClient.prefix(text), false);
							return 1;
						})))
				.executes((context) -> {
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
				}));
	}
}
