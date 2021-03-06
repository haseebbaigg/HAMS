package com.example.text_recognition;

public class Messages {

    String message,type,from;
    long time;
    Boolean seen;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public Messages() {
    }

    public Messages(String message, String type, String from, long time, Boolean seen) {
        this.message = message;
        this.type = type;
        this.from = from;
        this.time = time;
        this.seen = seen;
    }
}
