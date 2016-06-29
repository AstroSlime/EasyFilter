package com.astronstudios.easyfilter.events;

import com.astronstudios.easyfilter.filter.FilterResult;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerChatFilterEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private Player player;
    private FilterResult result;
    private String message;

    public PlayerChatFilterEvent(Player player, FilterResult result, String message) {
        this.player = player;
        this.result = result;
        this.message = message;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public Player getPlayer() {
        return player;
    }

    public FilterResult getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }
}
