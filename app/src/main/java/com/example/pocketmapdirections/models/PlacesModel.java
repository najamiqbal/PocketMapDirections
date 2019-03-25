package com.example.pocketmapdirections.models;

public class PlacesModel {
    public static final String TABLE_NAME = "favorite";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_SUB_TITLE = "sub_title";

    private int id;
    private String title;
    private String subtitle;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_TITLE + " TEXT,"
                    + COLUMN_SUB_TITLE + " TEXT"
                    + ")";

    public PlacesModel() {

    }

    public PlacesModel(int id, String title, String subtitle) {
        this.title = title;
        this.id=id;
        this.subtitle = subtitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
