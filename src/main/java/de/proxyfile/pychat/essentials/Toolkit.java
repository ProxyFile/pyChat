package de.proxyfile.pychat.essentials;

import de.proxyfile.pychat.PyChat;
import de.proxyfile.pychat.discord.DiscordHandler;
import de.proxyfile.pychat.discord.EmbedBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedDataManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Toolkit {

    private static final Pattern LINK_PATTERN = Pattern.compile(
            "(?<!@)((?:https?://)?(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(?:/[\\w./?=&%-]*)?)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern IP_PATTERN = Pattern.compile(
            "\\b((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b"
    );

    public static @NotNull Component format(String msg, Boolean showPrefix) {
        MiniMessage mm = MiniMessage.miniMessage();
        String message = showPrefix ? PyChat.getPlugin().getPrefix() + msg : msg;
        return mm.deserialize(message);
    }

    public static void log(LogType type, String msg) {
        Component message = switch (type) {
            case FATAL -> format("<gray>[<red><bold>FATAL<gray>] <gray>" + msg, true);
            case ERROR -> format("<gray>[<red>ERROR<gray>] <gray>" + msg, true);
            case WARNING -> format("<gray>[<gold>WARNING<gray>] <gray>" + msg, true);
            case INFO -> format("<gray>[<dark_green>INFO<gray>] <gray>" + msg, true);
            case SUCCESS -> format("<gray>[<green>SUCCESS<gray>] <gray>" + msg, true);
            case DEBUG -> format("<gray>[<dark_aqua>DEBUG<gray>] <gray>" + msg, true);
        };
        Bukkit.getConsoleSender().sendMessage(message);
    }

    public static CachedDataManager getUserCache(String username) {
        LuckPerms lp = LuckPermsProvider.get();
        return lp.getUserManager().getUser(username).getCachedData();
    }

    public static void logChatMessage(Player sender, Component messageComponent) {
        FileConfiguration config = PyChat.getPlugin().getConfig();
        if (!config.getBoolean("discord-webhook.enabled", false)) return;
        if(!config.getBoolean("discord-webhook.log-chat", false)) return;

        String webhookUrl = config.getString("discord-webhook.url");
        if (webhookUrl == null || webhookUrl.isEmpty()) return;

        String message = LegacyComponentSerializer.legacySection().serialize(messageComponent);
        String avatarUrl = "https://minotar.net/avatar/" + sender.getName() + "/64"; // Minecraft avatar service

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("💬 New Chat Message")
                .setDescription("```" + message + "```")
                .addField("User", sender.getName(), true)
                .addField("UUID", sender.getUniqueId().toString(), true)
                .setThumbnail(avatarUrl)
                .setColor(0x00AAFF)
                .setFooter("ᴘʏᴄʜᴀᴛ ʟᴏɢɢᴇʀ", avatarUrl);

        JSONObject payload = embed.buildWebhookPayload();

        Bukkit.getScheduler().runTaskAsynchronously(PyChat.getPlugin(), () -> {
            try {
                new DiscordHandler().sendWebhook(payload, webhookUrl);
            } catch (IOException e) {
                log(LogType.ERROR, "Failed to send Discord chat log: " + e.getMessage());
            }
        });
    }

    public static void logKick(Player player, String reason, String source) {
        FileConfiguration config = PyChat.getPlugin().getConfig();
        if (!config.getBoolean("discord-webhook.enabled", false)) return;
        if(!config.getBoolean("discord-webhook.log-kick", true)) return;

        String webhookUrl = config.getString("discord-webhook.url");
        if (webhookUrl == null || webhookUrl.isEmpty()) return;

        String avatarUrl = "https://minotar.net/avatar/" + player.getName() + "/64";

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("🚫 Player Kicked")
                .setColor(0xFF0000)
                .addField("Player", player.getName(), true)
                .addField("UUID", player.getUniqueId().toString(), true)
                .addField("Reason", "```" + reason + "```", false)
                .addField("Kicked By", source != null ? source : "System", true)
                .setThumbnail(avatarUrl)
                .setFooter("ᴘʏᴄʜᴀᴛ ʟᴏɢɢᴇʀ", avatarUrl);

        JSONObject payload = embed.buildWebhookPayload();

        Bukkit.getScheduler().runTaskAsynchronously(PyChat.getPlugin(), () -> {
            try {
                new DiscordHandler().sendWebhook(payload, webhookUrl);
            } catch (IOException e) {
                log(LogType.ERROR, "Failed to send Discord chat log: " + e.getMessage());
            }
        });
    }


    public static List<String> findLinks(String text) {
        List<String> links = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return links;
        }
        Matcher matcher = LINK_PATTERN.matcher(text);
        while (matcher.find()) {
            links.add(matcher.group(0));
        }
        return links;
    }

    public static String getHostFromLink(String link) {
        if (link == null || link.trim().isEmpty()) {
            return null;
        }

        try {
            String urlString = link.trim();
            if (!urlString.startsWith("http://") && !urlString.startsWith("https://")) {
                urlString = "http://" + urlString;
            }
            return new URL(urlString).getHost();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public static boolean containsIP(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        return IP_PATTERN.matcher(text).find();
    }
}
