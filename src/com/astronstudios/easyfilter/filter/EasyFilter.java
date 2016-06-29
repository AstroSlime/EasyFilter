package com.astronstudios.easyfilter.filter;

import com.astronstudios.easyfilter.MsgUtil;
import com.astronstudios.easyfilter.VersionChecker;
import com.astronstudios.easyfilter.commands.EasyFilterCommand;
import com.astronstudios.easyfilter.listeners.ChatListener;
import com.astronstudios.easyfilter.listeners.JoinListener;
import com.astronstudios.easyfilter.listeners.QuitListener;
import com.astronstudios.easyfilter.listeners.ServerPingListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class EasyFilter extends JavaPlugin implements Listener {

    private static String version;
    private final int resource = 24882;
    private static final String prefix = "\u00A7e[EasyFilter]\u00A7r ";
    private static String latestVersion = "1.0";
    private static EasyFilter instance;
    private static FileConfiguration config;

    /**
     * @return the plugins prefix.
     */
    public static String getPrefix() {
        return prefix;
    }

    /**
     * @return the current version of the plugin.
     */
    public static String getVersion() {
        return version;
    }

    /**
     * @return if the plugin is enabled by the config.
     */
    public static boolean isPluginEnabled() {
        return config.getBoolean("settings.enabled");
    }

    /**
     * @return an instance of this class.
     */
    public static EasyFilter getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;
        config = getConfig();
        version = getDescription().getVersion();

        MsgUtil.print(true, "&aLoading Files...");
        try {
            File config = new File(getDataFolder(), "config.yml");
            if (!config.exists()) saveDefaultConfig();
            MsgUtil.print(true, "&aLoaded files successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            MsgUtil.print(true, "&cException loading config files!");
        }

        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new JoinListener(), this);
        manager.registerEvents(new ChatListener(), this);
        manager.registerEvents(new ServerPingListener(), this);
        manager.registerEvents(new QuitListener(), this);

        getCommand("easyfilter").setExecutor(new EasyFilterCommand());

        boolean checkUpdates = getConfig().getBoolean("settings.check-updates");

        if (checkUpdates) {
            latestVersion = new VersionChecker(resource).getLatest();
        }

        int verV = Integer.parseInt(version.replaceAll("\\.", ""));
        int verL = Integer.parseInt(latestVersion.replaceAll("\\.", ""));

        MsgUtil.print(false, " ");
        MsgUtil.print(false, "==== &e*&f ==== &e*&f ==== &e*&f ==== &e*&f ==== &e*&f ==== &e*&f ==== &e*&f ====");
        MsgUtil.print(false, " ");
        MsgUtil.print(false, " &e            E A S Y   F I L T E R");
        MsgUtil.print(false, " ");
        MsgUtil.print(false, " Status: " + (isPluginEnabled() ? "&aEnabled" : "&cDisabled"));
        MsgUtil.print(false, " Version: " + version + (checkUpdates ?
                (verV < verL ? " &6Outdated!" : (verV > verL) ? " &cDev Build" : " &aUp to date") : ""));
        if (checkUpdates && (verV < verL)) MsgUtil.print(false, " Latest Version: " + latestVersion);
        MsgUtil.print(false, " ");
        MsgUtil.print(false, "==== &e*&f ==== &e*&f ==== &e*&f ==== &e*&f ==== &e*&f ==== &e*&f ==== &e*&f ====");
        MsgUtil.print(false, " ");
    }

    public void onDisable() {

    }
}
