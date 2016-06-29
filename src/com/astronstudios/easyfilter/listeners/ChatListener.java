package com.astronstudios.easyfilter.listeners;

import com.astronstudios.easyfilter.*;
import com.astronstudios.easyfilter.events.PlayerChatFilterEvent;
import com.astronstudios.easyfilter.EasyFilter;
import com.astronstudios.easyfilter.filter.Filter;
import com.astronstudios.easyfilter.filter.FilterResult;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChatListener implements Listener {

    private static final FileConfiguration config = EasyFilter.getInstance().getConfig();
    private static HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static Map<String, List<String>> helpPages = new HashMap<>();
    private static Filter filter = new Filter();

    static {
        new BukkitRunnable() {
            @Override
            public void run() {
                filter.sort();
                for (UUID u : cooldowns.keySet()) {
                    Player p = Bukkit.getPlayer(u);
                    if (cooldowns.get(u) < System.currentTimeMillis() || p == null) {
                        cooldowns.remove(u);
                    }
                }
            }
        }.runTaskTimerAsynchronously(EasyFilter.getInstance(), 20 * 5, 20 * 30);
    }

    @EventHandler
    public void on(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!config.getBoolean("settings.enabled")) return;
        if (player.hasPermission("easyfilter.bypass")) return;

        // Check if the player has a cooldown applied.
        if (cooldowns.containsKey(player.getUniqueId())) {
            if (cooldowns.get(player.getUniqueId()) < System.currentTimeMillis()) {
                cooldowns.remove(player.getUniqueId());
            } else {
                event.setCancelled(true);
                for (String s : config.getStringList("settings.cooldown"))

                    MsgUtil.send(false, player,
                            s.replaceAll("%time%", String.valueOf(cooldowns.get(player.getUniqueId()) / 1000)));
                return;
            }
        }

        if (!player.isOp() || player.hasPermission("easyfilter.bypass.cooldown")) { // Check message cooldowns.
            for (String key : config.getConfigurationSection("settings.chat-cooldowns").getKeys(false)) {
                if (player.hasPermission("easyfilter.cooldown." + key)) {
                    cooldowns.put(player.getUniqueId(),
                            System.currentTimeMillis()
                                    + (config.getLong("settings.easyfilter.filter-messages.cooldown." + key) * 1000));
                    break;
                }
            }
        }

        if (!player.hasPermission("easyfilter.bypass.username")
                && config.getBoolean("settings.filter-usernames")) {
            if (new Filter().scan(player)) {
                event.setCancelled(true);
                for (String s : config.getStringList("settings.filter-messages.username"))
                    MsgUtil.send(false, player, s);
                return;
            }
        }

        // Check if the message contains course messages set in the config.
        if (!player.hasPermission("easyfilter.bypass.message")
                && config.getBoolean("settings.filter-chat")) {
            FilterResult f = filter.scan(player.getUniqueId(), event.getMessage()).getResult();
            if (f != FilterResult.CLEAN) event.setCancelled(true);
            if (f == FilterResult.ADS) {
                for (String s : config.getStringList("settings.filter-messages.advertising"))
                    MsgUtil.send(false, player, s);
            } else if (f == FilterResult.SWEAR) {
                for (String s : config.getStringList("settings.filter-messages.offensive"))
                    MsgUtil.send(false, player, s);
            } else if (f == FilterResult.UNICODE) {
                for (String s : config.getStringList("settings.filter-messages.unsupported-unicode"))
                    MsgUtil.send(false, player, s);
            } else if (f == FilterResult.REPEAT) {
                for (String s : config.getStringList("settings.filter-messages.already-sent"))
                    MsgUtil.send(false, player, s);
            }
            Bukkit.getPluginManager().callEvent(new PlayerChatFilterEvent(player, f, event.getMessage()));
        }

        // Convert any set emoji's if they are enabled
        if (config.getBoolean("settings.emoji-converter")) {
            String message = filter.convert(event.getMessage());
            event.setMessage(message);
        }
    }

    @EventHandler
    public void on(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!config.getBoolean("settings.enabled")) return;
        String[] args = event.getMessage().substring(1, event.getMessage().length()).split(" ");
        if (config.getBoolean("help-pages.enabled") &&
                (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?"))) {
            event.setCancelled(true);

            if (helpPages.isEmpty()) { // Load Pages in
                for (String key : config.getConfigurationSection("help-pages.pages").getKeys(false)) {
                    helpPages.put(key, config.getStringList("help-pages.pages." + key));
                }
            }

            if (args.length <= 1) { // Find relevant page
                for (String s : helpPages.get("screen")) MsgUtil.send(false, player, s);
            } else if (args.length >= 1 && helpPages.containsKey(args[1])) {
                for (String s : helpPages.get(args[1])) MsgUtil.send(false, player, s);
            } else {
                MsgUtil.send(true, player, "&cNo such page found!");
            }
        }
    }

    /**
     * Update all needed caches in this class. This will update the help pages and check
     * all cooldown's to see if any are in need of being removed.
     */
    public static void update() {
        if (cooldowns.size() > 0) {
            for (UUID u : cooldowns.keySet()) {
                Player p = Bukkit.getPlayer(u);
                if (cooldowns.get(u) < System.currentTimeMillis() || p == null) {
                    cooldowns.remove(u);
                }
            }
        }
        if (helpPages.size() > 0) {
            helpPages.clear();
            helpPages = new HashMap<>();
        }
        for (String key : config.getConfigurationSection("help-pages.pages").getKeys(false)) {
            helpPages.put(key, config.getStringList("help-pages.pages" + key));
        }
    }
}
