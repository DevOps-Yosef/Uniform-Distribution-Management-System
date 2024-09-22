package com.zemen.udms.dto;

public class EntitlementUniformItemsDTO {
    private String id;
    private EntitlementDTO entitlementDTO;
    private int imsItemId;
    private String itemName;
    private int quantity;
    private int period;
    private String page;
    private String rowsPerPage;

    
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
    public EntitlementDTO getEntitlementDTO() {
        return entitlementDTO;
    }
    public void setEntitlementDTO(EntitlementDTO entitlementDTO) {
        this.entitlementDTO = entitlementDTO;
    }
}
