package com.zemen.udms.dto;

import java.util.Collection;
import java.util.Date;

public class EmployeeProfile {
    private String id;
    private String badgeNumber;
    private String userName;
    private String name;
    private String gender;    
    private Collection<UniformRequisitionDTO> uniformRequisitionCollection;
    private PositionCategoryDTO positionCategory;
    private String positionCategoryName;
    private String jobPosition;
    private String jobGrade;
    private String jobCategory;
    private String department;
    private boolean inactive;
    private String status;
    private String eMail;
    private Date createdDate;
    private String createdBy;
    private String page;
    private String rowsPerPage;

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBadgeNumber() {
        return badgeNumber;
    }

    public void setBadgeNumber(String badgeNumber) {
        this.badgeNumber = badgeNumber;
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getJobPosition() {
        return jobPosition;
    }

    public void setJobPosition(String jobPosition) {
        this.jobPosition = jobPosition;
    }

    public String getJobGrade() {
        return jobGrade;
    }

    public void setJobGrade(String jobGrade) {
        this.jobGrade = jobGrade;
    }

    public String getJobCategory() {
        return jobCategory;
    }

    public void setJobCategory(String jobCategory) {
        this.jobCategory = jobCategory;
    }

    public boolean isInactive() {
        return inactive;
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }
    
    public Collection<UniformRequisitionDTO> getUniformRequisitionCollection() {
        return uniformRequisitionCollection;
    }

    public void setUniformRequisitionCollection(Collection<UniformRequisitionDTO> uniformRequisitionCollection) {
        this.uniformRequisitionCollection = uniformRequisitionCollection;
    }

    public PositionCategoryDTO getPositionCategory() {
        return positionCategory;
    }

    public void setPositionCategory(PositionCategoryDTO positionCategory) {
        this.positionCategory = positionCategory;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPositionCategoryName() {
        return positionCategoryName;
    }

    public void setPositionCategoryName(String positionCategoryName) {
        this.positionCategoryName = positionCategoryName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    
}
