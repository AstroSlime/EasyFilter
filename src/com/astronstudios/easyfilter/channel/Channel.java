package com.astronstudios.easyfilter.channel;

import com.astronstudios.easyfilter.EasyFilter;
import com.astronstudios.easyfilter.MsgUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Channel {

    private FileConfiguration config = EasyFilter.getInstance().getConfig();
    private String channel;
    private String permission;
    private String prefix;
    private List<UUID> members;

    public Channel(String channel) {
        channel = channel.toLowerCase();
        if (config.getConfigurationSection("channels." + channel) == null) {
            config.set("channels." + channel + ".prefix", channel + " > ");
            EasyFilter.getInstance().saveConfig();
        }
        this.channel = channel;
        this.prefix = config.getString("channels." + channel + ".prefix");
        this.permission = "easyfilter.channel." + channel;
        members = new ArrayList<>();
        memberCheck();
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
        config.set("channels." + channel + ".prefix", prefix);
    }

    public void sendMessage(Player sender, String message) {
        memberCheck();
        for (UUID p : members) {
            Player player = Bukkit.getPlayer(p);
            if (player == null) members.remove(p);
            else player.sendMessage(MsgUtil.col(prefix) + sender.getDisplayName() + "&e: " + message);
        }
    }

    private void memberCheck() {
        members.addAll(Bukkit.getOnlinePlayers().stream().filter(player ->
                player.hasPermission(permission)).map((Function<Player, UUID>)
                Entity::getUniqueId).collect(Collectors.toList()));
    }

    public String getChannel() {
        return channel;
    }

    public List<UUID> getMembers() {
        return members;
    }
}
