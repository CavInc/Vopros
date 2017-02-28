package cav.vopros.utils;


public class RecordModel {
    private String dateRec;
    private String countRect;

    public RecordModel(String dateRec,String countRect) {
        this.countRect = countRect;
        this.dateRec = dateRec;
    }

    public String getDateRec() {
        return dateRec;
    }

    public String getCountRect() {
        return countRect;
    }
}
