package com.astronstudios.easyfilter.listeners;

import com.astronstudios.easyfilter.EasyFilter;
import com.astronstudios.easyfilter.MsgUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JoinListener  implements Listener {

    private FileConfiguration config = EasyFilter.getInstance().getConfig();

    @EventHandler
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (config.getBoolean("settings.join-message")) {
            for (String key : config.getConfigurationSection("event-messages").getKeys(false)) {
                if (player.hasPermission("easyfilter.event-message." + key)) {
                    if (config.contains("event-messages." + key + ".join")) {
                        String m = config.getString("event-messages." + key + ".join");
                        if (m.length() == 0) event.setJoinMessage("");
                        else event.setJoinMessage(MsgUtil.col(m
                                .replaceAll("%username%", player.getName())
                                .replaceAll("%displayname%", player.getDisplayName())));
                        break;
                    }
                }
            }
        }

        if (config.getBoolean("fancy-join-messages.enabled")
                && player.hasPermission("easyfilter.fancyjoinmessage")) {
            sendFancyJoinMessage(player);
            if (config.getBoolean("fancy-join-messages.override-motd")) return;
        }

        if (config.getBoolean("motd.enabled-join")) {
            for (String s : config.getStringList("motd.join-message")) {
                MsgUtil.send(false, player, s);
            }
        }
    }

    /**
     * @param player who the messages are sent to.
     */
    private void sendFancyJoinMessage(Player player) {
        List<FancyMap> send = new ArrayList<>();
        String fancy = "fancy-join-messages.messages";
        send.addAll(config.getConfigurationSection(fancy).getKeys(false)
                .stream().map(key -> new FancyMap(config.getInt(fancy + "." + key + ".delay"),
                config.getStringList(fancy + "." + key + ".message")))
                .collect(Collectors.toList()));
        new BukkitRunnable() {
            int passed = 0;
            int index = 0;
            @Override
            public void run() {
                if (index >= send.size() || player == null) {
                    this.cancel();
                    return;
                }
                if (index < send.size() && send.get(index).time < passed) {
                    passed = 0;
                    for (String s : send.get(index).msg)
                        MsgUtil.send(false, player, s
                                .replaceAll("%username%", player.getName())
                                .replaceAll("%max%", String.valueOf(Bukkit.getServer().getMaxPlayers()))
                                .replaceAll("%min%", String.valueOf(Bukkit.getOnlinePlayers().size())));
                    index++;
                } else {
                    passed++;
                }
            }
        }.runTaskTimerAsynchronously(EasyFilter.getInstance(), 0, 1);
    }

    private class FancyMap {
        int time;
        List<String> msg;
        FancyMap(int t, List<String> m) {
            time = t;
            msg = m;
        }
    }
}
