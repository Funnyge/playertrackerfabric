package com.example.playertracker;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;

import java.util.HashSet;
import java.util.Set;

public class PlayerTracker implements ClientModInitializer {

    // Lista graczy do śledzenia:
    private final Set<String> trackedPlayers = Set.of(
            "niverrr", "keym1l", "xsunshine_", "kunmai", "medesei",
            "seguv_", "vttttt", "xyzverray", "youpuk", "kiniaxxs", "sakushima", "sleyz123"
    );

    // Stan online śledzonych graczy, aby uniknąć dublowania powiadomień:
    private final Set<String> onlineTrackedPlayers = new HashSet<>();

    @Override
    public void onInitializeClient() {
        // Gdy dołączasz do serwera lub łączysz się z serwerem:
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            client.execute(() -> {
                handler.getPlayerList().forEach(playerInfo -> {
                    String name = playerInfo.getProfile().getName();
                    if (trackedPlayers.contains(name) && !onlineTrackedPlayers.contains(name)) {
                        onlineTrackedPlayers.add(name);
                        sendChatMessage("[Tracker] " + name + " dołączył do gry!");
                    }
                });
            });
        });

        // Gdy disconnect (wyjście z serwera / zamknięcie połączenia):
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            client.execute(() -> {
                // Wyślij powiadomienie o wyjściu dla wszystkich, którzy byli onlineTrackedPlayers
                for (String name : onlineTrackedPlayers) {
                    sendChatMessage("[Tracker] " + name + " opuścił serwer.");
                }
                onlineTrackedPlayers.clear();
            });
        });
    }

    private void sendChatMessage(String message) {
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(
                new LiteralText(message),
                MessageType.SYSTEM,
                Util.NIL_UUID
        );
    }
}
