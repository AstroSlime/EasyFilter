package com.astronstudios.easyfilter.filter;

import com.astronstudios.easyfilter.EasyFilter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class Filter {

    private FileConfiguration config = EasyFilter.getInstance().getConfig();

    private Map<UUID, String> lastSent = new HashMap<>();
    private static List<String> web = new ArrayList<>();
    private static List<String> webProtocol = new ArrayList<>();
    private static List<Character> exclamations = new ArrayList<>();

    static {
        if (webProtocol.size() == 0) {
            webProtocol.add("http");
            webProtocol.add("https");
        }
        if (web.size() == 0) {
            web.add(".com");
            web.add(".co");
            web.add(".org");
            web.add(".net");
            web.add(".tk");
            web.add(".me");
            web.add(".mc");
            web.add(".pro");
            web.add(".info");
            web.add(".host");
            web.add("www");
        }
        if (exclamations.size() == 0) {
            exclamations.add('!');
            exclamations.add('@');
            exclamations.add('#');
            exclamations.add('$');
            exclamations.add('%');
            exclamations.add('?');
        }
    }

    /**
     * Sort any missing cache's to be removed.
     */
    public void sort() {
        lastSent.keySet().stream().filter(u -> Bukkit.getPlayer(u) == null).forEach(u -> lastSent.remove(u));
    }

    /**
     * Add a player or message to the filter. This will enable a few checks
     * later such as message spam.
     *
     * @param uuid who is being added to the filter.
     */
    private void add(UUID uuid) {
        if (!lastSent.containsKey(uuid)) lastSent.put(uuid, "");
    }

    /**
     * Convert any emoji's defined in the config to be converted in this message
     * and return it.
     *
     * @param string what string will have any emoji's converted.
     * @return the string with all defined emoji's fom the config converted.
     */
    public String convert(String string) {
        if (!config.getBoolean("settings.enabled")) return string;
        String[] split = string.split(" ");
        String converted = "";
        List<String> tc = new ArrayList<>();
        tc.addAll(config.getConfigurationSection("emojis").getKeys(false));
        for (String s : split) {
            if (tc.contains(s)) {
                String[] hex = config.getString("emojis." + s).split("-");
                for (String ih : hex) {
                    if (ih.matches("-?[0-9a-zA-Z]+")) {
                        int i = Integer.parseInt(ih);
                        converted += Character.toString((char) i);
                    }
                }
                converted += " ";
            } else {
                converted += s + " ";
            }
        }
        return converted;
    }

    /**
     * Scan a players username for any inappropriate words in it.
     *
     * @param player whos username to scan.
     * @return if the players username contains any inappropriate words.
     */
    public boolean scan(Player player) {
        String name = player.getName()
                .replaceAll("_", "").toLowerCase();
        for (String s : config.getStringList("blacklisted-words")) {
            if (name.contains(s.toLowerCase())) return true;
        }
        return false;
    }

    /**
     * Scan a string for any advertisements or inappropriate messages. The less
     * that is used the less impact on the CPU this filter will have. Having both
     * advanced and web scans on will slightly increase scanning time and machine load.
     *
     * @param string what string is being scanned.
     * @return the state of the input string as a FilterComplete.
     */
    public FilterComplete scan(UUID uuid, String string) {
        if (!config.getBoolean("settings.enabled"))
            return new FilterComplete(string, FilterResult.CLEAN);
        add(uuid);

        if (config.getBoolean("filter-exclamations")) {
            String s = "";
            System.out.print("Scan");
            for (int i = 0; i < string.length(); i++) {
                char c = string.charAt(i);
                char l = s.charAt(s.length());
                System.out.print(!exclamations.contains(l) + " " + (c != l));
                if (!exclamations.contains(l) && c != l) {
                    s += c;
                }
            }
            string = s;
        }

        if (config.getBoolean("settings.disable-auto-send")) {
            String last = lastSent.get(uuid);
            if (!last.equals("")) {
                char[] lastArray = last.toCharArray();
                int changed = (string.length() > last.length()
                        ? string.length() - last.length()
                        : last.length() - string.length());
                String current = string.toLowerCase();
                for (int i = 0; i < lastArray.length; i++) {
                    if (changed > 5) break;
                    if (current.length() > i && (current.charAt(i) != lastArray[i])) changed++;
                }
                if (changed <= (current.length() < 10 ? 3 : current.length() / 9))
                    return new FilterComplete(string, FilterResult.REPEAT);
            }
        }

        List<String> sites = new ArrayList<>();
        List<String> words = new ArrayList<>();

        sites.addAll(config.getStringList("whitelisted-sites").stream()
                .map(String::toLowerCase).collect(Collectors.toList()));
        words.addAll(config.getStringList("blacklisted-words").stream()
                .map(String::toLowerCase).collect(Collectors.toList()));

        String stringConverted = string.toLowerCase();
        String[] split;
        if (config.getBoolean("settings.filter-unicodes")) {
            for (char c : stringConverted.toCharArray()) {
                int v = (int) c;
                if (!(v <= 127 && v >= 32)) {
                    return new FilterComplete(string, FilterResult.UNICODE);
                }
            }
        }
        if (config.getBoolean("settings.advanced-filter")) { // Advanced scan for bad words
            String scanner = stringConverted
                    .replaceAll("\\.", "")
                    .replaceAll(",", "")
                    .replaceAll("_", "")
                    .replaceAll("!", "")
                    .replaceAll("@", "")
                    .replaceAll("$", "")
                    .replaceAll("'", "")
                    .replaceAll(" ", "");
            for (int i = 0; i < scanner.length(); i++) {
                for (String w : words) {
                    if (i + w.length() <= scanner.length()) {
                        int leng = i + w.length();
                        String sub = (leng < scanner.length() ? scanner.substring(i, leng)
                                : scanner.substring(i, scanner.length()));
                        if (sub.equalsIgnoreCase(w)) {
                            return new FilterComplete(string, FilterResult.SWEAR);
                        }
                    }
                }
            }
        }

        split = stringConverted.split(" ");
        for (String s : split) {
            if (!config.getBoolean("settings.advanced-filter")) { // Simple scan for bad words
                if (words.size() > 0) {
                    for (String w : words) {
                        if (w.equalsIgnoreCase(s))
                            return new FilterComplete(string, FilterResult.SWEAR);
                    }
                }
            }

            if (config.getBoolean("settings.filter-websites")) { // Scan for ads not whitelisted
                boolean free = false;
                for (String siteS : sites) {
                    if (s.contains(siteS.toLowerCase())) free = true;
                }
                if (!free) {
                    for (String webP : webProtocol) {
                        if (s.startsWith(webP))
                            return new FilterComplete(string, FilterResult.ADS);
                    }
                    for (String webP : web) {
                        if (s.contains(webP.toLowerCase()))
                            return new FilterComplete(string, FilterResult.ADS);
                    }
                }
            }
        }

        lastSent.put(uuid, string.toLowerCase());
        return new FilterComplete(string, FilterResult.CLEAN);
    }
}
