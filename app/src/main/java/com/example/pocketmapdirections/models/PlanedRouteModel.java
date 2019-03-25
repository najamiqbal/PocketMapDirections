package com.example.pocketmapdirections.models;

public class PlanedRouteModel {
    public static final String TABLE_NAME = "planed";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_START_POINT = "startpoint";
    public static final String COLUMN_END_POINT = "endpoint";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME = "time";


    private int id;
    private String startpoint;
    private String endpoint;
    private String date;
    private String time;


    // Create table SQL query
    public static final String CREATE_TABLE2 =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_START_POINT + " TEXT,"
                    + COLUMN_END_POINT + " TEXT,"
                    + COLUMN_DATE + " TEXT,"
                    + COLUMN_TIME + " TEXT"
                    + ")";


    public PlanedRouteModel() {

    }

    public PlanedRouteModel(int id, String startpoint, String endpoint, String date, String time) {
        this.id = id;
        this.startpoint = startpoint;
        this.endpoint = endpoint;
        this.date = date;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStartpoint() {
        return startpoint;
    }

    public void setStartpoint(String startpoint) {
        this.startpoint = startpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
