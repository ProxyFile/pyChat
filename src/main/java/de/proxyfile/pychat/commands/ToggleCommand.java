package de.proxyfile.pychat.commands;

import de.proxyfile.pychat.PyChat;
import de.proxyfile.pychat.essentials.Toolkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ToggleCommand implements CommandExecutor {

    public static List<Player> toggle_list = new ArrayList<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] string) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(Toolkit.format("<red>ᴛʜᴇ ᴄᴏɴsᴏʟᴇ ɪsɴ'ᴛ ᴀʙʟᴇ ᴛᴏ ᴜsᴇ ᴛʜɪs ᴄᴏᴍᴍᴀɴᴅ", true));
        }
        Player user = (Player) sender;
        if(PyChat.getPlugin().getConfig().getBoolean("chat-toggle.enabled"))  {
            if(user.hasPermission(PyChat.getPlugin().getConfig().getString("chat-toggle.toggle-permission", "pychat.toggle"))) {
                if(!toggle_list.contains(user)) {
                    toggle_list.add(user);
                    user.sendMessage(Toolkit.format(PyChat.getPlugin().getConfig().getString("chat-toggle.toggle-message-off", "<gray>ʏᴏᴜ ᴀʀᴇ ɴᴏᴡ ɴᴏᴛ ᴀʙʟᴇ ᴛᴏ sᴇᴇ ᴛʜᴇ ᴄʜᴀᴛ."), true));
                } else {
                    toggle_list.remove(user);
                    user.sendMessage(Toolkit.format(PyChat.getPlugin().getConfig().getString("chat-toggle.toggle-message-on", "<gray>ʏᴏᴜ ᴀʀᴇ ɴᴏᴡ ᴀʙʟᴇ ᴛᴏ sᴇᴇ ᴛʜᴇ ᴄʜᴀᴛ ᴀɢᴀɪɴ."), true));
                }
            } else {
                user.sendMessage(Toolkit.format("<red>ʏᴏᴜ ᴀʀᴇɴ'ᴛ ᴀʙʟᴇ ᴛᴏ ʀᴜɴ ᴛʜɪs ᴄᴏᴍᴍᴀɴᴅ", true));
            }
        } else {
            user.sendMessage(Toolkit.format("<red>ᴛʜɪs ᴄᴏᴍᴍᴀɴᴅ ɪs ᴅᴇᴀᴄᴛɪᴠᴀᴛᴇᴅ", true));
        }
        return false;
    }
}
