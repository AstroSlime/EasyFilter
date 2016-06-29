package com.astronstudios.easyfilter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VersionChecker {

    private int r;

    public VersionChecker(int resource) {
        r = resource;
    }

    /**
     * @return the latest version from the spigot forums.
     */
    public String getLatest() {
        try {
            HttpURLConnection con = (HttpURLConnection) (
                    new URL("http://www.spigotmc.org/api/general.php")).openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.getOutputStream().write((
                    "key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&" +
                            "resource=" + r).getBytes("UTF-8"));
            return  (new BufferedReader(new InputStreamReader(con.getInputStream()))).readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
