package me.honkling.mhplus.mixins;

import com.google.gson.Gson;
import me.honkling.mhplus.MHPlusClient;
import me.honkling.mhplus.util.SettingsManager;
import me.honkling.mhplus.util.serializables.MojangResponse;
import net.minecraft.client.gui.hud.ChatHudListener;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ChatHudListener.class)
public class ChatHudListenerMixin {
	@Inject(at = @At("HEAD"), method = "onChatMessage", cancellable = true)
	public void onChatMessage(MessageType messageType, Text message, UUID sender, CallbackInfo ci) {
		if (SettingsManager.Instance.data.getBlockedUsers().contains(sender.toString()))
			ci.cancel();

		if (MHPlusClient.isOnMinehut) {
			String msg = message.getString().replaceAll("([§�].)", "").trim().toLowerCase();
			SettingsManager.Settings settings = SettingsManager.Instance.settings;
			if (settings.isHideAdvertisements() && msg.matches("^\\[ad] (\\[.+] )?.+: /join .+")) ci.cancel();
			if (settings.isHideNpcMessages() && msg.matches("^\\[npc] .+")) ci.cancel();
			if (settings.isHideMarketAdvertisements() && msg.matches("^\\[shop] .+")) ci.cancel();
			if (settings.isHideMinehutBroadcasts() && msg.matches("(.*\n)?\\[(minehut|market)] .+")) ci.cancel();
			if (msg.matches("^sending you to the lobby!")) MHPlusClient.isInLobby = true;
			if (settings.isTrackServers() && MHPlusClient.isInLobby && msg.matches("^sending you to [0-9a-z]{4,10}!")) {
				MHPlusClient.isInLobby = false;
				String rawServerName = msg.substring(15, msg.length() - 1);

				HttpClient client = HttpClient.newHttpClient();
				HttpResponse<String> response = null;
				try {
					HttpRequest request = HttpRequest.newBuilder()
							.uri(new URI(String.format("https://api.minehut.com/server/%s?byName=true", rawServerName)))
							.GET()
							.build();

					response = client.send(request, HttpResponse.BodyHandlers.ofString());
				} catch (URISyntaxException | IOException | InterruptedException e) {
					e.printStackTrace();
				}

				if (response == null || response.body() == null || response.statusCode() != 200)
					return;

				Gson gson = new Gson();
				SettingsManager.Data.Server minehutResponse = gson.fromJson(response.body().substring(10, response.body().length() - 1), SettingsManager.Data.Server.class);

				SettingsManager.Data data = SettingsManager.Instance.data;
				List<SettingsManager.Data.Server> joinedServers = data.getJoinedServers();
				if (joinedServers.size() >= 30) joinedServers.remove(joinedServers.size() - 1);
				joinedServers.add(0, minehutResponse);
				data.setJoinedServers(joinedServers);
			}
			if (msg.matches("from (\\[.+] )?[a-zA-Z0-9_]{3,16}: .*") || msg.matches("[a-zA-Z0-9_]{3,16} would like to be your friend!")) {
				boolean isFormer = msg.matches("from (\\[.+] )?[a-zA-Z0-9_]{3,16}: .*");
				Pattern pattern = Pattern.compile(isFormer ? "from (\\[.+] )?([a-zA-Z0-9_]{3,16}): .*" : "([a-zA-Z0-9_]{3,16}) would like to be your friend!");
				Matcher matcher = pattern.matcher(msg);

				if (matcher.find()) {
					String username = matcher.group(isFormer ? 1 : 0);
					UUID uuid;
					if (MHPlusClient.uuidCache.containsKey(username.toLowerCase())) {
						uuid = MHPlusClient.uuidCache.get(username.toLowerCase());
					} else {
						HttpClient client = HttpClient.newHttpClient();
						HttpResponse<String> response = null;
						MojangResponse mojangResponse = new MojangResponse();
						try {
							HttpRequest request = HttpRequest.newBuilder()
									.uri(new URI(String.format("https://api.mojang.com/users/profiles/minecraft/%s", username)))
									.GET()
									.build();

							response = client.send(request, HttpResponse.BodyHandlers.ofString());
							Gson gson = new Gson();
							mojangResponse = gson.fromJson(response.body(), MojangResponse.class);
						} catch (URISyntaxException | IOException | InterruptedException e) {
							e.printStackTrace();
						}

						if (response == null || response.body() == null || response.statusCode() != 200) {
							Logger logger = MHPlusClient.logger;
							logger.error("Failed to fetch UUID.");
							if (response != null) {
								logger.error("Body: " + response.body());
								logger.error("Status: " + response.statusCode());
							}
							return;
						}

						String rawUUID = mojangResponse.getId();
						String formattedUUID = String.format("%s-%s-%s-%s-%s", rawUUID.substring(0,8), rawUUID.substring(8,12), rawUUID.substring(12,16), rawUUID.substring(16,20), rawUUID.substring(20, 32));
						uuid = UUID.fromString(formattedUUID);
						MHPlusClient.uuidCache.put(username.toLowerCase(), uuid);
					}

					if (SettingsManager.Instance.data.getBlockedUsers().contains(uuid.toString().replaceAll("-", "")))
						ci.cancel();
				}
			}
		}
	}
}
