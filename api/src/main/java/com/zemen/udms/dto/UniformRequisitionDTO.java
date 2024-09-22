package com.zemen.udms.dto;

import java.util.Collection;
import java.util.Date;


public class UniformRequisitionDTO {
    private String id;
    private String department;
    private String createdBy;
    private Date createdDate;
    private String approvedBy;
    private Date approvedDate;
    private Collection<UniformRequisitionDetailDTO> uniformRequisitionDetailCollection;
    private EmployeeProfile employeeId;
    private LuStatusDTO luStatusId;
    private Date dateFrom;
    private Date dateTo;
    private Boolean notificationManager;
    private Boolean notificationForIssue;
    private String remark;
    private String page;
    private String rowsPerPage;   
    private String positionCategoryName; 
    
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public String getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    public Date getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    public String getApprovedBy() {
        return approvedBy;
    }
    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }
    public Date getApprovedDate() {
        return approvedDate;
    }
    public void setApprovedDate(Date approvedDate) {
        this.approvedDate = approvedDate;
    }
    public Collection<UniformRequisitionDetailDTO> getUniformRequisitionDetailCollection() {
        return uniformRequisitionDetailCollection;
    }
    public void setUniformRequisitionDetailCollection(
            Collection<UniformRequisitionDetailDTO> uniformRequisitionDetailCollection) {
        this.uniformRequisitionDetailCollection = uniformRequisitionDetailCollection;
    }
    public EmployeeProfile getEmployeeId() {
        return employeeId;
    }
    public void setEmployeeId(EmployeeProfile employeeId) {
        this.employeeId = employeeId;
    }
    public LuStatusDTO getLuStatusId() {
        return luStatusId;
    }
    public void setLuStatusId(LuStatusDTO luStatusId) {
        this.luStatusId = luStatusId;
    }
    
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
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public Boolean getNotificationManager() {
        return notificationManager;
    }
    public void setNotificationManager(Boolean notificationManager) {
        this.notificationManager = notificationManager;
    }
    public Boolean getNotificationForIssue() {
        return notificationForIssue;
    }
    public void setNotificationForIssue(Boolean notificationForIssue) {
        this.notificationForIssue = notificationForIssue;
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
    public String getPositionCategoryName() {
        return positionCategoryName;
    }
    public void setPositionCategoryName(String positionCategoryName) {
        this.positionCategoryName = positionCategoryName;
    }

    

}
