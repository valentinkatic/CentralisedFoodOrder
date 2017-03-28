package com.katic.centralisedfoodorder.classes;

public class FilterData {

    private String title;
    private String id;

    public FilterData(String title, String id) {
        this.title = title;
        this.id = id;
    }

    public FilterData() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
