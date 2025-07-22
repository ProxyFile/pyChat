package de.proxyfile.pychat.commands;

import de.proxyfile.pychat.PyChat;
import de.proxyfile.pychat.essentials.Toolkit;
import de.proxyfile.pychat.events.ChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChatCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(Toolkit.format("<red>ᴛʜᴇ ᴄᴏɴsᴏʟᴇ ɪsɴ'ᴛ ᴀʙʟᴇ ᴛᴏ ᴜsᴇ ᴛʜɪs ᴄᴏᴍᴍᴀɴᴅ", true));
        }
        Player user = (Player) sender;
        if(args.length == 0) {
            user.sendMessage(Toolkit.format("<red>ɪɴᴠᴀʟɪᴅ sʏɴᴛᴀx ᴜsᴇ: <gray>/pychat help", true));
        } else {
            switch(args[0]) {
                case "help" -> help(user);
                case "reload" ->  reload(user);
                case "mute" -> mute();
                case "unmute" -> unmute();
                case "clear" -> clear();
                default -> help(user);
            }
        }
        return false;
    }

    private void clear() {
        Component message = Toolkit.format("<gray>ᴄʜᴀᴛ ᴡᴀs ᴄʟᴇᴀʀᴇᴅ ʙʏ ᴀɴ ᴀᴅᴍɪɴɪsᴛʀᴀᴛᴏʀ", true);
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (int i = 0; i < 100; i++) {
                player.sendMessage(Component.empty());
            }
            player.sendMessage(message);
        }

        Bukkit.getConsoleSender().sendMessage(Toolkit.format("<gray>ᴄʜᴀᴛ ᴡᴀs ᴄʟᴇᴀʀᴇᴅ ʙʏ ᴀɴ ᴀᴅᴍɪɴɪsᴛʀᴀᴛᴏʀ", true));
    }

    private void mute() {
        ChatEvent.setGlobalMute(true);
        Bukkit.broadcast(Toolkit.format("<red>ᴄʜᴀᴛ ʜᴀs ʙᴇᴇɴ ɢʟᴏʙᴀʟʟʏ ᴍᴜᴛᴇᴅ ʙʏ ᴀɴ ᴀᴅᴍɪɴɪsᴛʀᴀᴛᴏʀ.", true));
    }

    private void unmute() {
        ChatEvent.setGlobalMute(false);
        Bukkit.broadcast(Toolkit.format("<gray>Gʟᴏʙᴀʟ ᴄʜᴀᴛ ᴍᴜᴛᴇ ʜᴀs ʙᴇᴇɴ ʟɪғᴛᴇᴅ.", true));
    }

    private void help(Player user) {
        user.sendMessage(Toolkit.format("<gray>------ <yellow><bold>ᴘʏᴄʜᴀᴛ ʜᴇʟᴘ</bold> <gray>------", false));
        user.sendMessage(Toolkit.format("<yellow>/toggle <gray>- <white>Toggle chat visibility", false));
        user.sendMessage(Toolkit.format("<yellow>/pychat reload <gray>- <white>Reload PyChat configuration", false));
        user.sendMessage(Toolkit.format("<yellow>/pychat clear <gray>- <white>Clear chat for all players", false));
        user.sendMessage(Toolkit.format("<yellow>/pychat help <gray>- <white>Show this help menu", false));
        user.sendMessage(Toolkit.format("<yellow>/pychat mute <gray>- <white>Globally mutes the chat.", false));
        user.sendMessage(Toolkit.format("<yellow>/pychat unmute <gray>- <white>Unmutes the chat.", false));
        user.sendMessage(Toolkit.format("<gray>--------------------------", false));
    }


    private void reload(Player user) {
        if (user.hasPermission("pychat.reload")) {
            PyChat.getPlugin().reloadConfig();
            user.sendMessage(Toolkit.format("<gray>ᴄᴏɴғɪɢᴜʀᴀᴛɪᴏɴ ʀᴇʟᴏᴀᴅᴇᴅ sᴜᴄᴄᴇssғᴜʟʟʏ.", true));
        } else {
            user.sendMessage(Toolkit.format("<red>ʏᴏᴜ ᴀʀᴇɴ'ᴛ ᴀʙʟᴇ ᴛᴏ ʀᴜɴ ᴛʜɪs ᴄᴏᴍᴍᴀɴᴅ.", true));
        }
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if(args.length == 1) {
            completions.add("help");
            completions.add("reload");
            completions.add("mute");
            completions.add("unmute");
            completions.add("clear");
        }
        return completions;
    }
}
