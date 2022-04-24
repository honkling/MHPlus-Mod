package me.honkling.mhplus;

import com.google.gson.Gson;
import me.honkling.mhplus.commands.MHPlusCommand;
import me.honkling.mhplus.mixins.ClickActionExtension;
import me.honkling.mhplus.util.SettingsManager;
import me.honkling.mhplus.util.StackManager;
import me.honkling.mhplus.util.serializables.MessageObject;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.ClickEvent;
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
	public static MHPlusClient Instance;
	public static SettingsManager settings;

	public static boolean isOnMinehut;
	public static boolean isInLobby;
	public static Logger logger = LogManager.getLogger();
	public static HashMap<String, UUID> uuidCache = new HashMap<>();

	@Override
	public void onInitializeClient() {
		logger.info("[MHPlus] MHPlus is enabled.");
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

		// Setup click event
		new ClickActionExtension().run();

		// Subscribe to stacks
		StackManager.getInstance().subscribe("confirmSendMessage", (parameters) -> {
			ClickEvent clickEvent = (ClickEvent) parameters[0];
			String fullValue = clickEvent.getValue();
			String value = fullValue.split("\s+")[1];

			Gson gson = new Gson();
			MessageObject message = gson.fromJson(value, MessageObject.class);

			ClientPlayerEntity player = MinecraftClient.getInstance().player;
			if (player == null)
				return;
			player.sendChatMessage(message.getMessage());
		});

		// register commands
		MHPlusCommand.register(ClientCommandManager.DISPATCHER);
	}

	public static MutableText prefix(MutableText post) {
		return new LiteralText("MHPlus> ")
				.formatted(Formatting.RED)
				.append(post);
	}

	public String getDataFolder() {
		return new File("mods/MHPlus").getAbsolutePath();
	}
}
