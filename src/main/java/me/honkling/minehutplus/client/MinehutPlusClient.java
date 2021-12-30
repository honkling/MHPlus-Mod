package me.honkling.minehutplus.client;

import me.honkling.minehutplus.commands.MinehutPlusCommand;
import me.honkling.minehutplus.util.SettingsManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;

import java.io.*;

@Environment(EnvType.CLIENT)
public class MinehutPlusClient implements ClientModInitializer {
	public static MinehutPlusClient Instance;
	public static SettingsManager settings;

	public static boolean isOnMinehut;

	@Override
	public void onInitializeClient() {
		System.out.println("MinehutPlus is enabled.");
		Instance = this;
		settings = new SettingsManager();
		isOnMinehut = false;

		saveDefaultConfig();

		// register commands
		MinehutPlusCommand.register(ClientCommandManager.DISPATCHER);

		// register shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread(() -> settings.writeSettings()));
	}

	public void saveDefaultConfig() {
		new File(getDataFolder()).mkdir();
		File settingsFile = new File(getDataFolder() + File.separator + "settings.txt");
		try {
			if (!settingsFile.exists()) {
				settingsFile.createNewFile();
				BufferedWriter writer = new BufferedWriter(new FileWriter(settingsFile.getAbsolutePath()));
				InputStream stream = getClass().getResourceAsStream("/settings.txt");
				BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
				String line;
				StringBuilder content = new StringBuilder();
				while((line = reader.readLine()) != null) {
					content.append(line);
				}
				writer.write(content.toString());
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getDataFolder() {
		return new File("mods/MinehutPlus").getAbsolutePath();
	}
}
