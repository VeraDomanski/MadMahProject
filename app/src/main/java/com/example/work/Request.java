package com.example.work;

import java.util.List;

public class Request {
    private String day;
    private String hour;
    private String date;
    private String subject;
    private String extra;
    private String from;


    private Boolean taken;
    private Boolean rejected;
    private List<String> tables;
    public Request(){}



    public Request(String day, String hour, String date , String subject, String extra, String from, Boolean taken, List<String> tables, Boolean rejected) {
        this.day = day;
        this.date = date;
        this.hour = hour;
        this.subject = subject;
        this.extra = extra;
        this.from=from;
        this.taken=taken;
        this.tables = tables;
        this.rejected=rejected;
    }

    public String getDay() {
        return day;
    }
    public String getDate() {
        return date;
    }
    public Boolean getRejected() {
        return rejected;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public String getSubject() {
        return subject;
    }

    public String getExtra() {
        return extra;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Boolean getTaken() {
        return taken;
    }

    public void setTaken(Boolean taken) {
        this.taken = taken;
    }
    public List<String> getTables() {
        return tables;
    }

    public void setTables(List<String> tables) {
        this.tables = tables;
    }
}
