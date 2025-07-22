package de.proxyfile.pychat.events;

import de.proxyfile.pychat.PyChat;
import de.proxyfile.pychat.commands.ToggleCommand;
import de.proxyfile.pychat.essentials.Toolkit;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.luckperms.api.cacheddata.CachedDataManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ChatEvent implements Listener {

    private final PyChat plugin;
    private final FileConfiguration config;

    private final Map<UUID, Long> lastMessageTime = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> spamWarningCounts = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> blWarningCounts = new ConcurrentHashMap<>();

    @Getter @Setter
    public static boolean globalMute = false;

    private boolean spamEnabled;
    private long spamDelayMillis;
    private int spamKickAfter;
    private String spamBypassPermission;
    private String spamWarningMessage;
    private String spamKickMessage;

    private boolean blacklistEnabled;
    private String blacklistBypassPermission;
    private List<String> blacklistedWords;
    private String blacklistWarningMessage;
    private int blacklistKickAfter;
    private String blacklistKickMessage;

    private boolean linkEnabled;
    private boolean linkBlockIp;
    private boolean linkBlockDomains;
    private List<String> linkList;
    private String linkBypassPermission;
    private String linkMessage;
    private String linkIpMessage;

    private String chatFormat;

    public ChatEvent(PyChat plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;
        loadConfig();
    }

    /**
     * Loads all necessary values from the config file into memory.
     * This avoids costly file lookups on every chat message.
     */
    private void loadConfig() {
        spamEnabled = config.getBoolean("spam-protection.enabled", true);
        spamDelayMillis = config.getLong("spam-protection.message-delay-seconds", 2) * 1000L;
        spamKickAfter = config.getInt("spam-protection.kick-after-warnings", 5);
        spamBypassPermission = config.getString("spam-protection.bypass", "pychat.bypass");
        spamWarningMessage = config.getString("spam-protection.warning-message", "<red>Please slow down!");
        spamKickMessage = config.getString("spam-protection.kick-message", "<red>Kicked for spamming.");

        blacklistEnabled = config.getBoolean("blacklist.enabled", true);
        blacklistBypassPermission = config.getString("blacklist.bypass", "pychat.bypass");
        blacklistedWords = config.getStringList("blacklist.words").stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        blacklistWarningMessage = config.getString("blacklist.warn-message", "<red>Your message contains a blacklisted word.");
        blacklistKickAfter = config.getInt("blacklist.kick-after", 3);
        blacklistKickMessage = config.getString("blacklist.kick-message", "<red>Kicked for using blacklisted words.");

        linkEnabled = config.getBoolean("link-protection.enabled", true);
        linkBlockIp = config.getBoolean("link-protection.block-ips", true);
        linkBlockDomains = config.getBoolean("link-protection.block-domains", true);
        linkList = config.getStringList("link-protection.allowed-links").stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        linkBypassPermission = config.getString("link-protection.bypass", "pychat.bypass");
        linkMessage = config.getString("link-protection.warning-message", "<red><red>ʟɪɴᴋs ᴀʀᴇ ɴᴏᴛ ᴀʟʟᴏᴡᴇᴅ ɪɴ ᴄʜᴀᴛ.");
        linkIpMessage = config.getString("link-protection.ip-message", "<red>ɪᴘs ᴀʀᴇ ɴᴏᴛ ᴀʟʟᴏᴡᴇᴅ ɪɴ ᴄʜᴀᴛ.");

        chatFormat = config.getString("chat-format.format", "{prefix} {username} {suffix}<gray>: <white>{message}");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncChatEvent e) {
        Player user = e.getPlayer();
        if (globalMute && !user.hasPermission("pychat.bypass")) {
            user.sendMessage(Toolkit.format("<red>ᴄʜᴀᴛ ɪs ᴄᴜʀʀᴇɴᴛʟʏ ᴍᴜᴛᴇᴅ.", true));
            e.setCancelled(true);
            return;
        }

        handleLink(user, e); if(e.isCancelled()) return;
        handleSpam(user, e); if (e.isCancelled()) return;
        handleBlacklist(user, e); if (e.isCancelled()) return;
        handleFormatting(user, e);
    }


    /**
     * Handles link/IP protection by checking the message for non-whitelisted
     * links or any IPs and cancelling the event if found.
     */
    private void handleLink(Player user, AsyncChatEvent e) {
        if (!linkEnabled || user.hasPermission(linkBypassPermission)) {
            return;
        }

        String message = PlainTextComponentSerializer.plainText().serialize(e.message());

        if (linkBlockIp && Toolkit.containsIP(message)) {
            user.sendMessage(Toolkit.format(linkIpMessage, true));
            e.setCancelled(true);
            return;
        }

        if (linkBlockDomains) {
            List<String> foundLinks = Toolkit.findLinks(message);
            if (foundLinks.isEmpty()) {
                return;
            }

            if (linkList.isEmpty()) {
                user.sendMessage(Toolkit.format(linkMessage, true));
                e.setCancelled(true);
                return;
            }

            for (String link : foundLinks) {
                String host = Toolkit.getHostFromLink(link);

                if (host == null) {
                    user.sendMessage(Toolkit.format(linkMessage, true));
                    e.setCancelled(true);
                    return;
                }

                boolean isAllowed = false;
                for (String allowedDomain : linkList) {
                    if (host.equalsIgnoreCase(allowedDomain) || host.toLowerCase().endsWith("." + allowedDomain)) {
                        isAllowed = true;
                        break;
                    }
                }

                if (!isAllowed) {
                    user.sendMessage(Toolkit.format(linkMessage, true));
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

    /**
     * Handles spam protection by checking message frequency.
     */
    private void handleSpam(Player user, AsyncChatEvent e) {
        if (!spamEnabled || user.hasPermission(spamBypassPermission)) {
            return;
        }

        UUID uuid = user.getUniqueId();
        long now = System.currentTimeMillis();
        long last = lastMessageTime.getOrDefault(uuid, 0L);

        if (now - last < spamDelayMillis) {
            e.setCancelled(true);
            int warnings = spamWarningCounts.getOrDefault(uuid, 0) + 1;
            spamWarningCounts.put(uuid, warnings);

            if (spamKickAfter > 0 && warnings >= spamKickAfter) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    user.kick(Toolkit.format(spamKickMessage, false));
                    Toolkit.logKick(user, spamKickMessage, "CONSOLE (Anti-Spam)");
                    spamWarningCounts.remove(uuid);
                    lastMessageTime.remove(uuid);
                });
            } else {
                user.sendMessage(Toolkit.format(spamWarningMessage, true));
            }
        } else {
            lastMessageTime.put(uuid, now);
            spamWarningCounts.remove(uuid);
        }
    }

    /**
     * Handles blacklisted words by checking message content.
     */
    private void handleBlacklist(Player user, AsyncChatEvent e) {
        if (!blacklistEnabled || user.hasPermission(blacklistBypassPermission) || blacklistedWords.isEmpty()) {
            return;
        }

        String plainMessage = PlainTextComponentSerializer.plainText().serialize(e.message()).toLowerCase();
        UUID uuid = user.getUniqueId();

        for (String blockedWord : blacklistedWords) {
            if (plainMessage.contains(blockedWord)) {
                e.setCancelled(true);
                user.sendMessage(Toolkit.format(blacklistWarningMessage, true));

                int warnings = blWarningCounts.getOrDefault(uuid, 0) + 1;
                blWarningCounts.put(uuid, warnings);

                if (blacklistKickAfter > 0 && warnings >= blacklistKickAfter) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        user.kick(Toolkit.format(blacklistKickMessage, false));
                        Toolkit.logKick(user, blacklistKickMessage, "CONSOLE");
                        blWarningCounts.remove(uuid);
                    });
                }
                return;
            }
        }
        blWarningCounts.remove(uuid);
    }

    /**
     * Formats the chat message using Paper's modern event renderer.
     */
    private void handleFormatting(Player user, AsyncChatEvent e) {
        CachedDataManager cache = Toolkit.getUserCache(user.getName());
        String prefix = cache.getMetaData().getPrefix() != null ? cache.getMetaData().getPrefix() : "";
        String suffix = cache.getMetaData().getSuffix() != null ? cache.getMetaData().getSuffix() : "";

        String formatTemplate = chatFormat
                .replace("{prefix}", prefix)
                .replace("{suffix}", suffix)
                .replace("{username}", user.getName());

        e.renderer((source, sourceDisplayName, message, viewer) -> {
            Component finalMessage = Toolkit.format(formatTemplate, false)
                    .replaceText(builder -> builder.matchLiteral("{message}").replacement(message));
            return finalMessage;
        });

        e.viewers().removeIf(audience -> {
            if (audience instanceof Player) {
                return ToggleCommand.toggle_list.contains(((Player) audience).getUniqueId());
            }
            return false;
        });

        Toolkit.logChatMessage(user, e.message());
    }
}