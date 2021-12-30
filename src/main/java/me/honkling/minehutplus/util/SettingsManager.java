package me.honkling.minehutplus.util;

import me.honkling.minehutplus.client.MinehutPlusClient;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.io.*;
import java.util.Scanner;

public class SettingsManager {
	private boolean hideAdvertisements;
	private boolean hideNpcMessages;
	private boolean hideMarketAdvertisements;
	public static SettingsManager Instance;

	public SettingsManager() {
		hideAdvertisements = false;
		hideNpcMessages = false;
		hideMarketAdvertisements = false;
		readSettings();

		Instance = this;
	}

	public void writeSettings() {
		MinehutPlusClient client = MinehutPlusClient.Instance;

		File settingsFile = new File(client.getDataFolder() + File.separator + "settings.txt");
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(settingsFile.getAbsolutePath()));
			writer.write(String.format("""
					hideAdvertisements=%s
					hideNpcMessages=%s
					hideMarketAdvertisements=%s
					""", hideAdvertisements, hideNpcMessages, hideMarketAdvertisements));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public MutableText formatSetting(String key, boolean value) {
		 MutableText text = new LiteralText("\n" + key).formatted(Formatting.AQUA);
		 MutableText hover = new LiteralText(key + "\n\nValue: ")
				 .formatted(Formatting.DARK_AQUA)
				 .append(
				 		new LiteralText(String.valueOf(value))
							    .formatted(Formatting.AQUA)
				 );
		 text = text.setStyle(
		 		Style.EMPTY
					    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover))
					    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/minehutplus setting " + key))
		 );
		 return text;
	}

	private void readSettings() {
		MinehutPlusClient client = MinehutPlusClient.Instance;

		File settingsFile = new File(client.getDataFolder() + File.separator + "settings.txt");
		if(!settingsFile.exists()) client.saveDefaultConfig();

		try {
			Scanner reader = new Scanner(settingsFile);
			while(reader.hasNextLine()) {
				String settingRaw = reader.nextLine();
				String[] splitSetting = settingRaw.split("=", 2);
				if(!splitSetting[1].equalsIgnoreCase("true") && !splitSetting[1].equalsIgnoreCase("false")) continue;
				boolean value = Boolean.parseBoolean(splitSetting[1]);
				switch (splitSetting[0]) {
					case "hideAdvertisements" -> hideAdvertisements = value;
					case "hideNpcMessages" -> hideNpcMessages = value;
					case "hideMarketAdvertisements" -> hideMarketAdvertisements = value;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public boolean hideAdvertisements() {
		return hideAdvertisements;
	}

	public boolean hideNpcMessages() {
		return hideNpcMessages;
	}

	public boolean hideMarketAdvertisements() {
		return hideMarketAdvertisements;
	}

	public void hideAdvertisements(boolean value) {
		hideAdvertisements = value;
	}

	public void hideNpcMessages(boolean value) {
		hideNpcMessages = value;
	}

	public void hideMarketAdvertisements(boolean value) {
		hideMarketAdvertisements = value;
	}
}
