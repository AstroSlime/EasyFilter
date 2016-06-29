package com.astronstudios.easyfilter.filter;

public class FilterComplete {

    private String message;
    private FilterResult result;

    public FilterComplete(String message, FilterResult result) {
        this.message = message;
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public FilterResult getResult() {
        return result;
    }
}
