package com.example.owner.dialoc;

public class UserReport {
    private String reportType;
    private int numberOfReports;

    public UserReport(String reportType, int numberOfReports) {
        this.reportType = reportType;
        this.numberOfReports = numberOfReports;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public int getNumberOfReports() {
        return numberOfReports;
    }

    public void setNumberOfReports(int numberOfReports) {
        this.numberOfReports = numberOfReports;
    }
}
