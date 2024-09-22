package com.zemen.udms.dto;

import java.util.Collection;
import java.util.Date;

public class UniformRequisitionDetailDTO {
    private String id;
    private int imsItemId;
    private String itemName;
    private String size;
    private String approvedBy;
    private Date approvedDate;
    private Date lastIssueDate;
    private Date nextIssueDate;
    private Collection<UniformIssueDetailDTO> uniformIssueDetailCollection;
    private UniformRequisitionDTO uniformRequisitionId;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public int getImsItemId() {
        return imsItemId;
    }
    public void setImsItemId(int imsItemId) {
        this.imsItemId = imsItemId;
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
    public Collection<UniformIssueDetailDTO> getUniformIssueDetailCollection() {
        return uniformIssueDetailCollection;
    }
    public void setUniformIssueDetailCollection(Collection<UniformIssueDetailDTO> uniformIssueDetailCollection) {
        this.uniformIssueDetailCollection = uniformIssueDetailCollection;
    }
    public UniformRequisitionDTO getUniformRequisitionId() {
        return uniformRequisitionId;
    }
    public void setUniformRequisitionId(UniformRequisitionDTO uniformRequisitionId) {
        this.uniformRequisitionId = uniformRequisitionId;
    }
    public Date getLastIssueDate() {
        return lastIssueDate;
    }
    public void setLastIssueDate(Date lastIssueDate) {
        this.lastIssueDate = lastIssueDate;
    }
    public Date getNextIssueDate() {
        return nextIssueDate;
    }
    public void setNextIssueDate(Date nextIssueDate) {
        this.nextIssueDate = nextIssueDate;
    }
    public Date getApprovedDate() {
        return approvedDate;
    }
    public void setApprovedDate(Date approvedDate) {
        this.approvedDate = approvedDate;
    }
    public String getApprovedBy() {
        return approvedBy;
    }
    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }




}
