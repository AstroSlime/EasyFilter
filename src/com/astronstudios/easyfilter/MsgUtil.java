package com.astronstudios.easyfilter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MsgUtil {

    /**
     * Print a message to the console. You can apply the plugins prefix
     * with this to have it stand out a little more in the console.
     *
     * @param prefix if the plugins prefix will be displayed.
     * @param message what message will be printed.
     */
    public static void print(boolean prefix, Object... message) {
        String s = prefix ? EasyFilter.getPrefix() : "";
        for (Object o : message) s += o.toString();
        Bukkit.getConsoleSender().sendMessage(col(s));
    }

    /**
     * Send a message to a player. This will format the color codes and can
     * add the plugins prefix before the message.
     *
     * @param prefix if the plugins prefix will be displayed.
     * @param player who the message is being sent to.
     * @param message what the message is that's being sent.
     */
    public static void send(boolean prefix, Player player, Object... message) {
        String s = prefix ? EasyFilter.getPrefix() : "";
        for (Object o : message) s += o.toString();
        player.sendMessage(MsgUtil.col(s));
    }

    /**
     * Colourise a message. This will take the input of a string and output
     * a formatted colored String.
     *
     * @param s what string will be formatted.
     * @return the ordinal string with all color codes formatted.
     */
    public static String col(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
