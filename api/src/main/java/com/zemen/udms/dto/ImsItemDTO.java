package com.zemen.udms.dto;

public class ImsItemDTO {
    private int id;
    private String name;
    private boolean selected;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    public void setName(String name) {
        this.name = name;
    }

    
}
