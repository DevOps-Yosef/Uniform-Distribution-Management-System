package com.zemen.udms.dto;

import java.util.Date;

public class EmployeeUniformItemDTO {
    private String idUniformIssue;
    private String itemName;
    private String size;
    private Date date;
    private Date nextDate;


    
    public String getIdUniformIssue() {
        return idUniformIssue;
    }
    public void setIdUniformIssue(String idUniformIssue) {
        this.idUniformIssue = idUniformIssue;
    }
    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public String getSize() {
        return size;
    }
    public void setSize(String size) {
        this.size = size;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public Date getNextDate() {
        return nextDate;
    }
    public void setNextDate(Date nextDate) {
        this.nextDate = nextDate;
    }


    

    
}
