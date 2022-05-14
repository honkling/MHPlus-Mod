package me.honkling.mhplus.commands.impl;

import com.google.gson.Gson;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.honkling.mhplus.MHPlusClient;
import me.honkling.mhplus.util.SettingsManager;
import me.honkling.mhplus.util.serializables.MojangResponse;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
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

public class BlockCommand {
    private static final String name = "block";

    public static LiteralArgumentBuilder<FabricClientCommandSource> register() {
        return literal(name)
                .then(argument("player", StringArgumentType.string()).executes(BlockCommand::execute));
    }

    public static int execute(CommandContext<FabricClientCommandSource> context) {
        String blockedPlayerName = context.getArgument("player", String.class);
        ClientPlayerEntity player = context.getSource().getPlayer();

        if(!blockedPlayerName.matches("\\w{3,16}"))
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
    }
}
