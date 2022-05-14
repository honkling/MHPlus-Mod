package me.honkling.mhplus.util;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import me.honkling.mhplus.MHPlusClient;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class SettingsManager {
	public static SettingsManager Instance;
	public Settings settings;
	public Data data;
	public String message;

	public boolean excuseMessage;

	private SettingsManager() {
		settings = new Settings();
		data = new Data();
		read();
	}

	public static SettingsManager getInstance() {
		if(Instance == null)
			Instance = new SettingsManager();

		return Instance;
	}

	public void write() {
		File etcFile = new File(MHPlusClient.Instance.getDataFolder() + File.separator + "etc.bin");
		try {
			if (etcFile.exists()) etcFile.delete();
			etcFile.createNewFile();
			final FileOutputStream writer = new FileOutputStream(etcFile, true);

			// Settings octal
			int octal = 0;
			int steps = 1;
			Field[] fields = settings.getClass().getDeclaredFields();
			for (Field field : fields) {
				if(field.getType() == SettingsManager.class) continue;
				field.setAccessible(true);
				try {
					boolean value = field.getBoolean(settings);
					octal += value ? steps : 0;
				} catch (IllegalAccessException e) {
					field.setAccessible(true);
					writer.close();
					write();
					return;
				}
				steps *= 2;
			}
			writer.write((byte) octal);

			// Blocked users
			List<String> blockedUsers = data.getBlockedUsers();
			writer.write(blockedUsers.size());
			for(String blockedUser : blockedUsers) {
				blockedUser = blockedUser.replaceAll("-", "");
				writer.write(blockedUser.getBytes());
			}

			// Joined servers
			List<Data.Server> joinedServers = data.getJoinedServers();
			writer.write(joinedServers.size());
			for(Data.Server joinedServer : joinedServers) {
				writer.write(joinedServer.getId().getBytes());
			}

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void read() {
		// Format for etc.bin
		// (octal number for settings)(size of blocked users)(continuous list of uuids for blocked users)(size of joined servers)(continuous list of uuids for joined servers)

		File etcFile = new File(MHPlusClient.Instance.getDataFolder() + File.separator + "etc.bin");

		settings.setTrackServers(false);
		settings.setHideAdvertisements(false);
		settings.setHideMarketAdvertisements(false);
		settings.setHideMinehutBroadcasts(false);
		settings.setHideNpcMessages(false);
		data.setJoinedServers(new ArrayList<>());
		data.setBlockedUsers(new ArrayList<>());
		if (etcFile.exists()) {
			try (FileInputStream stream = new FileInputStream(etcFile)) {
				// Settings
				String octalSettings = Integer.toBinaryString(stream.read());
				Field[] fields = settings.getClass().getDeclaredFields();
				int settingsCount = Arrays.stream(fields).filter((field) -> field.getType() != SettingsManager.class).toArray().length;
				octalSettings += "0".repeat(settingsCount - octalSettings.length());
				for (int i = 0; i < fields.length; i++) {
					Field field = fields[i];
					if(field.getType() == SettingsManager.class) continue;
					int value = Integer.parseInt(String.valueOf(octalSettings.charAt(i)));
					field.setAccessible(true);
					field.setBoolean(settings, value == 1);
				}

				// Blocked users
				int blockedUsersSize = stream.read();
				List<String> blockedUsers = new ArrayList<>(blockedUsersSize);
				for (int i = 0; i < blockedUsersSize; i++) {
					byte[] uuid = new byte[32];
					stream.read(uuid);
					blockedUsers.add(new String(uuid));
				}
				data.setBlockedUsers(blockedUsers);

				// Joined servers
				int joinedServersSize = stream.read();
				List<Data.Server> servers = new ArrayList<>(joinedServersSize);
				for (int i = 0; i < joinedServersSize; i++) {
					byte[] rawId = new byte[24];
					stream.read(rawId);
					String id = new String(rawId);

					// I apologize in advance for the API abuse
					HttpClient client = HttpClient.newHttpClient();
					HttpRequest request = HttpRequest.newBuilder()
							.uri(new URI(String.format("https://api.minehut.com/server/%s", id)))
							.GET()
							.build();

					HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
					Gson gson = new Gson();
					Data.Server server = gson.fromJson(response.body().substring(10, response.body().length() - 1), Data.Server.class);
					servers.add(server);
				}
				data.setJoinedServers(servers);
			} catch (IOException | IllegalAccessException | URISyntaxException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public MutableText formatSetting(String key, boolean value) {
		 MutableText text = new LiteralText("\n" + key).formatted(Formatting.WHITE);
		 MutableText hover = new LiteralText(key + "\n\nValue: ")
				 .formatted(Formatting.GRAY)
				 .append(
				 		new LiteralText(String.valueOf(value))
							    .formatted(Formatting.WHITE)
				 );
		 text = text.setStyle(
		 		Style.EMPTY
					    .withFormatting(Formatting.WHITE)
					    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover))
					    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mhplus setting " + key))
		 );
		 return text;
	}

	public static class Data {
		private List<String> blockedUsers;
		private List<Server> joinedServers;

		public List<String> getBlockedUsers() {
			return blockedUsers;
		}

		public void setBlockedUsers(List<String> blockedUsers) {
			this.blockedUsers = blockedUsers;
		}

		public List<Server> getJoinedServers() {
			return joinedServers;
		}

		public void setJoinedServers(List<Server> joinedServers) {
			this.joinedServers = joinedServers;
		}

		public static class Server {
			private String name;
			@SerializedName("_id") private String id;

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public String getId() {
				return id;
			}

			public void setId(String id) {
				this.id = id;
			}

			public String toString() {
				return String.format("Server{name=%s,id=%s}", getName(), getId());
			}
		}
	}

	public static class Settings {
		private boolean hideAdvertisements;
		private boolean hideNpcMessages;
		private boolean hideMarketAdvertisements;
		private boolean trackServers;
		private boolean hideMinehutBroadcasts;
		private boolean protectAdvertisementTypos;

		public boolean isHideAdvertisements() {
			return hideAdvertisements;
		}

		public void setHideAdvertisements(boolean hideAdvertisements) {
			this.hideAdvertisements = hideAdvertisements;
		}

		public boolean isHideNpcMessages() {
			return hideNpcMessages;
		}

		public void setHideNpcMessages(boolean hideNpcMessages) {
			this.hideNpcMessages = hideNpcMessages;
		}

		public boolean isHideMarketAdvertisements() {
			return hideMarketAdvertisements;
		}

		public void setHideMarketAdvertisements(boolean hideMarketAdvertisements) {
			this.hideMarketAdvertisements = hideMarketAdvertisements;
		}

		public boolean isTrackServers() {
			return trackServers;
		}

		public void setTrackServers(boolean trackServers) {
			this.trackServers = trackServers;
		}

		public boolean isHideMinehutBroadcasts() {
			return hideMinehutBroadcasts;
		}

		public void setHideMinehutBroadcasts(boolean hideMinehutBroadcasts) {
			this.hideMinehutBroadcasts = hideMinehutBroadcasts;
		}

		public boolean isProtectAdvertisementTypos() {
			return protectAdvertisementTypos;
		}

		public void setProtectAdvertisementTypos(boolean protectAdvertisementTypos) {
			this.protectAdvertisementTypos = protectAdvertisementTypos;
		}
	}
}
