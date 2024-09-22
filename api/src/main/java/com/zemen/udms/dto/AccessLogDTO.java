package com.zemen.udms.dto;

import java.util.Date;


public class AccessLogDTO {
    private String id;
    private Date dateFrom;
    private Date dateTo;
    private Date date;
    private String username;
    private String operation;
    private String objectInfo;
    private String page;
    private String rowsPerPage;



    public Date getDateFrom() {
        return dateFrom;
    }
    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }
    public Date getDateTo() {
        return dateTo;
    }
    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getOperation() {
        return operation;
    }
    public void setOperation(String operation) {
        this.operation = operation;
    }
    public String getObjectInfo() {
        return objectInfo;
    }
    public void setObjectInfo(String objectInfo) {
        this.objectInfo = objectInfo;
    }
    public String getPage() {
        return page;
    }
    public void setPage(String page) {
        this.page = page;
    }
    public String getRowsPerPage() {
        return rowsPerPage;
    }
    public void setRowsPerPage(String rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }


    
  

}
