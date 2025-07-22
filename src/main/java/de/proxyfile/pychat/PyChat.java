package de.proxyfile.pychat;

import de.proxyfile.pychat.commands.ChatCommand;
import de.proxyfile.pychat.commands.ToggleCommand;
import de.proxyfile.pychat.essentials.ClearChatTask;
import de.proxyfile.pychat.essentials.LogType;
import de.proxyfile.pychat.essentials.Toolkit;
import de.proxyfile.pychat.essentials.UpdateChecker;
import de.proxyfile.pychat.events.ChatEvent;
import de.proxyfile.pychat.events.UserEvents;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class PyChat extends JavaPlugin {

    @Getter public static PyChat plugin;
    @Getter public Metrics metrics;
    @Getter public String prefix;

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        prefix = getConfig().getString("general.prefix");

        Toolkit.log(LogType.INFO, "<gray>ʟᴏᴀᴅɪɴɢ ᴘʏᴄʜᴀᴛ ...");
        metrics = new Metrics(this, 26261);
        new UpdateChecker(this, 126206)
                .checkForUpdate();
        registerEvents(); // Registers the Events to Bukkit API
        registerCommands(); // Registers the Commands to Bukkit API
        runClearChatTask(); // Starts the timer for auto clear chat
        Toolkit.log(LogType.INFO, "<gray>ʟᴏᴀᴅᴇᴅ ᴘʏᴄʜᴀᴛ <gray>[ <yellow>" + getDescription().getVersion() + " <gray>]");
    }

    @Override
    public void onDisable() {
        Toolkit.log(LogType.INFO, "<gray>ᴜɴʟᴏᴀᴅɪɴɢ ᴘʏᴄʜᴀᴛ ...");
        if(metrics != null) {
            metrics.shutdown();
        }
        Toolkit.log(LogType.INFO, "<red>ᴜɴʟᴏᴀᴅᴇᴅ ᴘʏᴄʜᴀᴛ <gray>[ <yellow>" + getDescription().getVersion() + " <gray>]");
    }

    public void registerCommands() {
        getCommand("toggle").setExecutor(new ToggleCommand());

        ChatCommand chat = new ChatCommand();
        getCommand("pychat").setExecutor(chat);
        getCommand("pychat").setTabCompleter(chat);
    }

    public void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new ChatEvent(this, getConfig()), this);
        pm.registerEvents(new UserEvents(getConfig()), this);
    }

    public void runClearChatTask() {
        if (getConfig().getBoolean("auto-clearchat.enabled", false)) {
            int interval = getConfig().getInt("auto-clearchat.interval-minutes", 30);
            new ClearChatTask(this)
                    .runTaskTimer(this, interval * 60L * 20L, interval * 60L * 20L);
        }
    }
}
