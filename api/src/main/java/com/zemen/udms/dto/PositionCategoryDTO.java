package com.zemen.udms.dto;

import java.util.Collection;

public class PositionCategoryDTO {
    private String id;
    private String name;
    private String description;
    private Collection<EntitlementDTO> entitlementDTOs;
    private Collection<EmployeeProfile> employeeProfiles;

    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Collection<EntitlementDTO> getEntitlementDTOs() {
        return entitlementDTOs;
    }
    public void setEntitlementDTOs(Collection<EntitlementDTO> entitlementDTOs) {
        this.entitlementDTOs = entitlementDTOs;
    }
    public Collection<EmployeeProfile> getEmployeeProfiles() {
        return employeeProfiles;
    }
    public void setEmployeeProfiles(Collection<EmployeeProfile> employeeProfiles) {
        this.employeeProfiles = employeeProfiles;
    }

    

    
}
