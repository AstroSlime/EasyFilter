package com.astronstudios.easyfilter.commands;

import com.astronstudios.easyfilter.filter.EasyFilter;
import com.astronstudios.easyfilter.MsgUtil;
import com.astronstudios.easyfilter.listeners.ChatListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.List;

public class EasyFilterCommand implements CommandExecutor {

    private EasyFilter plugin = EasyFilter.getInstance();
    private FileConfiguration config = EasyFilter.getInstance().getConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendE(sender, "");
            sendSplit(sender);
            sendE(sender, "                     &6&lEasyFilter");
            sendE(sender, "                      &7Version " + EasyFilter.getVersion());
            sendE(sender, "");
            sendE(sender, " &e/easyfilter reset &7&o- Reset the config.");
            sendE(sender, " &e/easyfilter reload &7&o- Reload the config.");
            sendE(sender, " &e/easyfilter settings &7&o- Show current settings.");
            sendE(sender, " &e/easyfilter add w:s <listing> &7&o- Add a word/site to the filter list.");
            sendE(sender, " &e/easyfilter remove w:s <listing> &7&o- Remove a word/site from the filter list.");
            sendE(sender, " &e/easyfilter list w:s &7&o- List all filtered words/sites.");
            sendE(sender, " &e/easyfilter global y:n &7&o- Toggle if global chat is filtered.");
            sendE(sender, " &e/easyfilter usernames y:n &7&o- Toggle if usernames are filtered.");
            sendE(sender, " &e/easyfilter enabled y:n &7&o- Toggle if the plugin is enabled or disabled.");
            sendE(sender, " &e/easyfilter emoji y:n &7&o- Toggle the emoji converter.");
            sendSplit(sender);
            sendE(sender, "");
            return true;
        } else {
            if (args[0].equalsIgnoreCase("reset")) {
                send(sender, "&7Resetting configuration...");
                try {
                    File config = new File(plugin.getDataFolder(), "config.yml");
                    if (config.exists()) config.delete();
                    config = new File(plugin.getDataFolder(), "config.yml");
                    if (!config.exists()) plugin.saveDefaultConfig();
                    plugin.reloadConfig();
                    ChatListener.update();
                    send(sender, "&aReset configuration!");
                } catch (Exception e) {
                    e.printStackTrace();
                    send(sender, "&cFailed to reset configuration!");
                }
                return true;
            }

            else if (args[0].equalsIgnoreCase("reload")) {
                send(sender, "&7Reloading configuration...");
                try {
                    File config = new File(plugin.getDataFolder(), "config.yml");
                    if (!config.exists()) {
                        plugin.saveDefaultConfig();
                        send(sender, "&7Created new file!");
                    }
                    plugin.saveConfig();
                    plugin.reloadConfig();
                    ChatListener.update();
                    send(sender, "&aReloaded configuration!");
                } catch (Exception e) {
                    e.printStackTrace();
                    send(sender, "&cFailed to reload configuration!");
                }
                return true;
            }

            else if (args[0].equalsIgnoreCase("enabled")) {
                if (args.length > 1
                        && (args[1].equalsIgnoreCase("y") || args[1].equalsIgnoreCase("n"))) {
                    String a = args[1].toLowerCase();
                    if (!EasyFilter.isPluginEnabled() && a.equals("n")) {
                        send(sender, "The plugin is already disabled!");
                        return true;
                    }
                    if (EasyFilter.isPluginEnabled() && a.equals("y")) {
                        send(sender, "You seem to be trying to make an enable paradox, the plugin is already enabled.");
                        return true;
                    }
                    config.set("settings.enabled", a.equals("y"));
                    plugin.saveConfig();
                    if (a.equals("y")) {
                        send(sender, "&aYou have once again enabled me!");
                    } else {
                        send(sender, "&cGoodbye! Disabling...");
                    }
                } else {
                    send(sender, "&fUsage: /easyfilter enabled &cy:n");
                }
                return true;
            }

            else if (args[0].equalsIgnoreCase("emoji")) {
                if (args.length > 1
                        && (args[1].equalsIgnoreCase("y") || args[1].equalsIgnoreCase("n"))) {
                    String a = args[1].toLowerCase();
                    boolean isOn = config.getBoolean("settings.emoji-converter");
                    if (!isOn && a.equals("n")) {
                        send(sender, "Emoji's are already disabled!");
                        return true;
                    }
                    if (isOn && a.equals("y")) {
                        send(sender, "Emoji's are already enabled and smiling.");
                        return true;
                    }
                    config.set("settings.emoji-converter", a.equals("y"));
                    plugin.saveConfig();
                    if (a.equals("y")) {
                        send(sender, "&aEnabled emoji's &e\u263A");
                    } else {
                        send(sender, "&cEmoji, away!");
                    }
                } else {
                    send(sender, "&fUsage: /easyfilter enabled &cy:n");
                }
                return true;
            }

            else if (args[0].equalsIgnoreCase("usernames")) {
                if (enabled(sender) && args.length > 1
                        && (args[1].equalsIgnoreCase("y") || args[1].equalsIgnoreCase("n"))) {
                    String a = args[1].toLowerCase();
                    boolean var = config.getBoolean("settings.filter-usernames");
                    if (!var && a.equals("n")) {
                        send(sender, "Usernames are already not being filtered!");
                        return true;
                    }
                    if (var && a.equals("y")) {
                        send(sender, "Usernames are already clean as a whistle!");
                        return true;
                    }
                    config.set("settings.filter-usernames", a.equals("y"));
                    plugin.saveConfig();
                    if (a.equals("y")) {
                        send(sender, "&aEnabled Username Filter!");
                    } else {
                        send(sender, "&cDisabled Username Filter!");
                    }
                } else {
                    send(sender, "&fUsage: /easyfilter usernames &cy:n");
                }
                return true;
            }

            else if (args[0].equalsIgnoreCase("global")) {
                if (enabled(sender) && args.length > 1
                        && (args[1].equalsIgnoreCase("y") || args[1].equalsIgnoreCase("n"))) {
                    String a = args[1].toLowerCase();
                    boolean var = config.getBoolean("settings.filter-chat");
                    if (!var && a.equals("n")) {
                        send(sender, "Global chat is already dirty!");
                        return true;
                    }
                    if (var && a.equals("y")) {
                        send(sender, "Global chat is already clean as a whistle!");
                        return true;
                    }
                    config.set("settings.filter-chat", a.equals("y"));
                    plugin.saveConfig();
                    if (a.equals("y")) {
                        send(sender, "&aEnabled Global Chat Filter!");
                    } else {
                        send(sender, "&cDisabled Global Chat Filter!");
                    }
                } else {
                    send(sender, "&fUsage: /easyfilter global &cy:n");
                }
                return true;
            }

            else if (args[0].equalsIgnoreCase("settings")) {
                List<String> words = config.getStringList("settings.blacklisted-words");
                List<String> sites = config.getStringList("settings.whitelisted-sites");
                boolean updates = config.getBoolean("settings.check-updates");
                boolean advanced = config.getBoolean("settings.advanced-filter");
                boolean usernames = config.getBoolean("settings.filter-usernames");
                boolean global = config.getBoolean("settings.filter-chat");
                boolean message = config.getBoolean("settings.filter-private");

                String y = "&aEnabled", n = "&cDisabled";

                sendE(sender, "");
                sendSplit(sender);
                sendE(sender, "                     &6&lEasyFilter");
                sendE(sender, "                      &7Version " + EasyFilter.getVersion());
                sendE(sender, "");
                sendE(sender, " System: " + (EasyFilter.isPluginEnabled() ? y : n));
                sendE(sender, " Advanced Filter: " + (advanced ? y : n));
                sendE(sender, " Check for Updates: " + (updates ? y : n));
                sendE(sender, "");
                sendE(sender, " Blacklisted Words: &e" + words.size());
                sendE(sender, " Whitelisted Sites: &e" + sites.size());
                sendE(sender, "");
                sendE(sender, " Filters");
                sendE(sender, "    Usernames: " + (usernames ? y : n));
                sendE(sender, "    Global: " + (global ? y : n));
                sendE(sender, "    Private: " + (message ? y : n));
                sendSplit(sender);
                sendE(sender, "");
                return true;
            }

            else if (args[0].equalsIgnoreCase("list")) {
                if (args.length > 1) {
                    if (args.length >= 2 && (args[1].equalsIgnoreCase("word")
                            || args[1].equalsIgnoreCase("w"))) {
                        List<String> words = config.getStringList("blacklisted-words");
                        String msg = " ";
                        for (String i : words) msg += (msg.length() <= 1 ? i : "&e,&f " + i);

                        sendE(sender, "");
                        sendSplit(sender);
                        sendE(sender, " Blacklisted Words:");
                        sendE(sender, "");
                        sendE(sender, msg);
                        sendE(sender, "");
                        sendE(sender, " Total count: " + words.size());
                        sendSplit(sender);
                        sendE(sender, "");
                        return true;
                    } else if (args.length >= 2 && (args[1].equalsIgnoreCase("site")
                            || args[1].equalsIgnoreCase("s"))) {
                        List<String> sites = config.getStringList("whitelisted-sites");
                        String msg = "";
                        for (String i : sites) msg += (msg.length() <= 1 ? i : "&e,&f " + i);

                        sendE(sender, "");
                        sendSplit(sender);
                        sendE(sender, " Whitelisted Sites:");
                        sendE(sender, " ");
                        sendE(sender, msg);
                        sendE(sender, "");
                        sendE(sender, " Total count: " + sites.size());
                        sendSplit(sender);
                        sendE(sender, "");
                        return true;
                    }
                } else {
                    send(sender, "&fUsage: /easyfilter list &cword:site");
                    return true;
                }
            }

            else if (args[0].equalsIgnoreCase("add")) {
                if (args.length > 1) {
                    if (args[1].equalsIgnoreCase("word") || args[1].equalsIgnoreCase("w")) {
                        if (args.length > 2) {
                            List<String> words = config.getStringList("blacklisted-words");
                            words.add(args[2].toLowerCase());
                            config.set("blacklisted-words", words);
                            plugin.saveConfig();
                            send(sender, "&aAdded '" + args[2].toLowerCase() + "' to the word blacklist!");

                            return true;
                        } else {
                            send(sender, "&fUsage: /easyfilter add word &c<listing>");
                            return true;
                        }
                    } else if (args[1].equalsIgnoreCase("site") || args[1].equalsIgnoreCase("s")) {
                        if (args.length > 2) {
                            List<String> sites = config.getStringList("whitelisted-sites");
                            sites.add(args[2].toLowerCase());
                            config.set("whitelisted-sites", sites);
                            plugin.saveConfig();
                            send(sender, "&aAdded '" + args[2].toLowerCase() + "' to the site whitelist!");
                            return true;
                        } else {
                            send(sender, "&fUsage: /easyfilter add site &c<listing>");
                            return true;
                        }
                    }
                    return true;
                } else {
                    send(sender, "&fUsage: /easyfilter add &cword:site <listing>");
                    return true;
                }
            }


            else if (args[0].equalsIgnoreCase("remove")) {
                if (args.length > 1) {
                    if (args[1].equalsIgnoreCase("word") || args[1].equalsIgnoreCase("w")) {
                        if (args.length > 2) {
                            List<String> words = config.getStringList("blacklisted-words");
                            if (words.contains(args[2])) words.remove(args[2].toLowerCase());
                            config.set("blacklisted-words", words);
                            plugin.saveConfig();
                            send(sender, "&aAdded '" + args[2].toLowerCase() + "' to the word blacklist!");

                            return true;
                        } else {
                            send(sender, "&fUsage: /easyfilter add word &c<listing>");
                            return true;
                        }
                    } else if (args[1].equalsIgnoreCase("site") || args[1].equalsIgnoreCase("s")) {
                        if (args.length > 2) {
                            List<String> sites = config.getStringList("whitelisted-sites");
                            if (sites.contains(args[2])) sites.remove(args[2].toLowerCase());
                            config.set("whitelisted-sites", sites);
                            plugin.saveConfig();
                            send(sender, "&aAdded '" + args[2].toLowerCase() + "' to the site whitelist!");
                            return true;
                        } else {
                            send(sender, "&fUsage: /easyfilter add site &c<listing>");
                            return true;
                        }
                    }
                    return true;
                } else {
                    send(sender, "&fUsage: /easyfilter add &cword:site <listing>");
                    return true;
                }
            }
        }
        return false;
    }

    private boolean enabled(CommandSender sender) {
        if (!EasyFilter.isPluginEnabled()) {
            send(sender, "&cPlugin Disabled! &fDo '/easyfilter enabled y' to use these commands.");
            return false;
        }
        return true;
    }

    private void sendSplit(CommandSender sender) {
        sendE(sender, "&e&m----------------------------------------------------");
    }

    private void sendE(CommandSender sender, Object... objects) {
        String s = "";
        for (Object o : objects) s += o.toString();
        sender.sendMessage(MsgUtil.col(s));
    }

    private void send(CommandSender sender, Object... objects) {
        String s = EasyFilter.getPrefix();
        for (Object o : objects) s += o.toString();
        sender.sendMessage(MsgUtil.col(s));
    }
}
