![alt text](http://i.imgur.com/yIv2czc.png)

<p>EasyFilter is a lightweight plugin built to make filtering out the chat box. 
Created in mind that you want the most configuration out of the plugin you can 
filter almost any part of the chat and even create chat channels to keep chat even cleaner.</p>


##Commands
/easyfilter reset ▬ Reset this config file back to default settings.<br>
/easyfilter reload ▬ Reload the this config file into the plugins cache.<br>
/easyfilter settings ▬ Show current settings.<br>
/easyfilter add w:s <word/site> ▬ Add a word or site to the selected filter.<br>
/easyfilter remove w:s <word/site> ▬ Remove a word or site from the selected filter.<br>
/easyfilter list w:s ▬ List all blacklisted words or all whitelisted sites.<br>
/easyfilter global y:n ▬ Set if the global chat filter is enabled or not.<br>
/easyfilter usernames y:n ▬ Set if usernames are enabled or not.<br>
/easyfilter enabled y:n ▬ Set if the plugin is enabled or not.<br>


##Permissions
easyfilter.bypass ▬ Bypass all filters.<br>
easyfilter.bypass.cooldown ▬ Bypass cooldown filter.<br>
easyfilter.bypass.message ▬ Bypass message filters (Ads, Offensive, Unicode).<br>
easyfilter.event-message.<group> ▬ Sets if that group/player will have those group settings applied.<br>
easyfilter.fancyjoinmessage ▬ Sets if the player is sent the fancy join message if enabled.<br>


##Report a Bug
Found a bug or received an error in the console/chatbox? You can either send a private message (start a conversation) to me or report it on the github page for the plugin.


##Developers
This plugin has an event for when a player triggers the chat filter. (More events and utilities will be added in later updates.) You can also use the Filter class to filter messages in custom systems as well.

Creating filters:
```java
    public void filter() {
        FilterComplete fc = new Filter().scan(players uuid, message); // Scan a message
        String emojis = new Filter().convert(message); // Convert all defined emoji's
        boolean username = new Filter().scan(player); // Scan a players username
    }
```


##Configuration File
Making sure you have full control over the plugin from the config file, every setting in the plugin is laid out within the file.


##Support
If you feel this plugin has helped a lot in making your server easier to run or is easy to use and want more plugins like it to come out. Consider donating to help support development.<br>
[DONATE](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=MR96W6T2ZYBTC)
