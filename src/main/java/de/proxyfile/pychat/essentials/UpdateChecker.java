package de.proxyfile.pychat.essentials;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {

    private final JavaPlugin plugin;
    private final String currentVersion;
    private final int resourceId;

    private String latestVersion = null;

    public UpdateChecker(JavaPlugin plugin, int resourceId) {
        this.plugin = plugin;
        this.currentVersion = plugin.getDescription().getVersion();
        this.resourceId = resourceId;
    }

    public void checkForUpdate() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String latest = reader.readLine();
                reader.close();

                if (latest != null && !latest.isEmpty()) {
                    latestVersion = latest.trim();
                    if (!latestVersion.equalsIgnoreCase(currentVersion)) {
                        Toolkit.log(LogType.INFO,"ᴜᴘᴅᴀᴛᴇ ᴀᴠᴀɪʟᴀʙʟᴇ! ᴄᴜʀʀᴇɴᴛ: " + currentVersion + ", ʟᴀᴛᴇsᴛ: " + latestVersion);
                    } else {
                        Toolkit.log(LogType.INFO,"ᴘʟᴜɢɪɴ ɪs ᴜᴘ ᴛᴏ ᴅᴀᴛᴇ.");
                    }
                } else {
                    Toolkit.log(LogType.WARNING,"ғᴀɪʟᴇᴅ ᴛᴏ ғᴇᴛᴄʜ ᴜᴘᴅᴀᴛᴇ ɪɴғᴏ ғʀᴏᴍ sᴘɪɢᴏᴛᴍᴄ.");
                }
            } catch (Exception e) {
                Toolkit.log(LogType.ERROR,"Eʀʀᴏʀ ᴄʜᴇᴄᴋɪɴɢ ғᴏʀ ᴜᴘᴅᴀᴛᴇ: " + e.getMessage());
            }
        });
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public boolean isUpdateAvailable() {
        return latestVersion != null && !latestVersion.equalsIgnoreCase(currentVersion);
    }
}
