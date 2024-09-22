package com.zemen.udms.dto;

import java.util.Collection;
import java.util.Date;

public class EntitlementDTO {
    private String id;
    private LuStatusDTO luStatusDTO;
    private PositionCategoryDTO positionCategory;
    private String positionCategoryName;
    private Collection<EntitlementUniformItemsDTO> entitlementUniformItemsCollection;
    private String createdBy;
    private Date createdDate;
    private String approvedBy;
    private Date approvedDate;
    private Boolean notificationOfficeManager;
    private String remark;
    private String page;
    private String rowsPerPage;
    //#############################
    private int imsItemId;
    private String itemName;
    private int quantity;
    private int period;



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

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }


    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(Date approvedDate) {
        this.approvedDate = approvedDate;
    }

    public LuStatusDTO getLuStatusDTO() {
        return luStatusDTO;
    }

    public void setLuStatusDTO(LuStatusDTO luStatusDTO) {
        this.luStatusDTO = luStatusDTO;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }



    public String getPositionCategoryName() {
        return positionCategoryName;
    }

    public void setPositionCategoryName(String positionCategoryName) {
        this.positionCategoryName = positionCategoryName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public Collection<EntitlementUniformItemsDTO> getEntitlementUniformItemsCollection() {
        return entitlementUniformItemsCollection;
    }

    public void setEntitlementUniformItemsCollection(
            Collection<EntitlementUniformItemsDTO> entitlementUniformItemsCollection) {
        this.entitlementUniformItemsCollection = entitlementUniformItemsCollection;
    }

    public PositionCategoryDTO getPositionCategory() {
        return positionCategory;
    }

    public void setPositionCategory(PositionCategoryDTO positionCategory) {
        this.positionCategory = positionCategory;
    }

    public Boolean getNotificationOfficeManager() {
        return notificationOfficeManager;
    }

    public void setNotificationOfficeManager(Boolean notificationOfficeManager) {
        this.notificationOfficeManager = notificationOfficeManager;
    }


}
