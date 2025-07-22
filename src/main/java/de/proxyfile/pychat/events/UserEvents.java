package de.proxyfile.pychat.events;

import de.proxyfile.pychat.PyChat;
import de.proxyfile.pychat.essentials.Toolkit;
import net.luckperms.api.cacheddata.CachedDataManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class UserEvents implements Listener {

    private final FileConfiguration config;

    public UserEvents(FileConfiguration config) {
        this.config = config;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player user = e.getPlayer();

        boolean enabled = config.getBoolean("join-messages.enabled", false);
        if(enabled) {
            CachedDataManager cache = Toolkit.getUserCache(user.getName());

            String message = PyChat.getPlugin().getConfig().getString("join-messages.join", "<gray>[<green>+<gray>] <gray>{username}");
            String prefix = cache.getMetaData().getPrefix() != null
                    ? cache.getMetaData().getPrefix()
                    : "";
            String suffix = cache.getMetaData().getSuffix() != null
                    ? cache.getMetaData().getSuffix()
                    : "";

            message = message.replace("{prefix}", prefix)
                             .replace("{suffix}", suffix)
                             .replace("{username}", user.getName());

            Bukkit.broadcast(Toolkit.format(message, false));
            e.setJoinMessage("");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player user = e.getPlayer();

        boolean enabled = config.getBoolean("join-messages.enabled", false);
        if(enabled) {
            CachedDataManager cache = Toolkit.getUserCache(user.getName());

            String message = PyChat.getPlugin().getConfig().getString("join-messages.quit", "<gray>[<red>-<gray>] <gray>{username}");
            String prefix = cache.getMetaData().getPrefix() != null
                    ? cache.getMetaData().getPrefix()
                    : "";
            String suffix = cache.getMetaData().getSuffix() != null
                    ? cache.getMetaData().getSuffix()
                    : "";

            message = message.replace("{prefix}", prefix)
                             .replace("{suffix}", suffix)
                             .replace("{username}", user.getName());

            Bukkit.broadcast(Toolkit.format(message, false));
            e.setQuitMessage("");
        }
    }
}
