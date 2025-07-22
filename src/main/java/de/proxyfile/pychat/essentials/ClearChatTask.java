package de.proxyfile.pychat.essentials;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ClearChatTask extends BukkitRunnable {
    private final JavaPlugin plugin;
    private final String message;

    public ClearChatTask(JavaPlugin plugin) {
        this.plugin = plugin;
        this.message = plugin.getConfig().getString("auto-clearchat.message", "<gray>ᴄʜᴀᴛ ᴡᴀs ᴄʟᴇᴀʀᴇᴅ ᴀᴜᴛᴏᴍᴀᴛɪᴄᴀʟʟʏ ᴛᴏ ʀᴇᴅᴜᴄᴇ ᴄʟᴜᴛᴛᴇʀ.");
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (int i = 0; i < 100; i++) {
                player.sendMessage(Component.empty());
            }
            player.sendMessage(Toolkit.format(message, true));
        }

        Bukkit.getConsoleSender().sendMessage(Toolkit.format("<gray>ᴀᴜᴛᴏ ᴄʟᴇᴀʀᴇᴅ ᴄʜᴀᴛ", true));
    }
}
