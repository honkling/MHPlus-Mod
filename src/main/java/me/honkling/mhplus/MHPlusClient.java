package me.honkling.mhplus;

import me.honkling.mhplus.commands.MHPlusCommand;
import me.honkling.mhplus.util.SettingsManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class MHPlusClient implements ClientModInitializer {
	private static boolean debug = true;
	public static MHPlusClient Instance;
	public static SettingsManager settings;
	public static boolean isOnMinehut;
	public static boolean isInLobby;
	public static Logger logger = LogManager.getLogger("MHPlus");
	public static HashMap<String, UUID> uuidCache = new HashMap<>();

	@Override
	public void onInitializeClient() {
		logger.info("MHPlus is enabled.");
		Instance = this;
		settings = SettingsManager.getInstance();
		isOnMinehut = false;

		// clear uuid cache
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				uuidCache.clear();
			}
		}, 0, 1000 * 60 * 30 /* 30 minutes */);

		// register commands
		MHPlusCommand.register(ClientCommandManager.DISPATCHER);
	}

	public static MutableText prefix(MutableText post) {
		return new LiteralText("MHPlus> ")
				.formatted(Formatting.RED)
				.append(post);
	}

	public String getDataFolder() {
		File file = new File("mods/MHPlus");
		if (!file.exists()) file.mkdir();
		return file.getAbsolutePath();
	}
}
