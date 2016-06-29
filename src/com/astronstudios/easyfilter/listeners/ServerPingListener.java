package com.astronstudios.easyfilter.listeners;

import com.astronstudios.easyfilter.filter.EasyFilter;
import com.astronstudios.easyfilter.MsgUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerPingListener implements Listener {

    private FileConfiguration config = EasyFilter.getInstance().getConfig();

    @EventHandler
    public void on(ServerListPingEvent event) {
        if (config.getBoolean("motd.enabled-list")) {
            event.setMotd(MsgUtil.col(config.getString("motd.server-list")));
        }
        if (config.getBoolean("motd.enabled-players")) {
            event.setMaxPlayers(config.getInt("motd.max-players"));
        }
    }
}
