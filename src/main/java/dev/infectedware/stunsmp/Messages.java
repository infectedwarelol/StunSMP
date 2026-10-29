package dev.infectedware.stunsmp;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;

final class Messages {

    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .build();

    private final FileConfiguration config;

    Messages(FileConfiguration config) {
        this.config = config;
    }

    void send(Audience audience, String key, String... placeholders) {
        audience.sendMessage(format(key, placeholders));
    }

    void sendActionBar(Audience audience, String key, String... placeholders) {
        audience.sendActionBar(format(key, placeholders));
    }

    private Component format(String key, String... placeholders) {
        String text = config.getString("messages." + key, "");
        for (int index = 0; index + 1 < placeholders.length; index += 2) {
            text = text.replace(placeholders[index], placeholders[index + 1]);
        }
        return SERIALIZER.deserialize(text);
    }
}
