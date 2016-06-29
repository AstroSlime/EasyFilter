package com.astronstudios.easyfilter.listeners;

import com.astronstudios.easyfilter.EasyFilter;
import com.astronstudios.easyfilter.MsgUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

    private FileConfiguration config = EasyFilter.getInstance().getConfig();

    @EventHandler
    public void on(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (config.getBoolean("settings.quit-message")) {
            for (String key : config.getConfigurationSection("event-messages").getKeys(false)) {
                if (player.hasPermission("easyfilter.event-message." + key)) {
                    if (config.contains("event-messages." + key + ".quit")) {
                        String m = config.getString("event-messages." + key + ".quit");
                        if (m.length() == 0) event.setQuitMessage("");
                        else event.setQuitMessage(MsgUtil.col(m
                                .replaceAll("%username%", player.getName())
                                .replaceAll("%displayname%", player.getDisplayName())));
                        break;
                    }
                }
            }
        }
    }
}
