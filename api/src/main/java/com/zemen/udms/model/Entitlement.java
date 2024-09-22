/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.zemen.udms.model;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author Yosef.Girma
 */
@Entity
@Table(name = "Entitlement")
@NamedQueries({
        @NamedQuery(name = "Entitlement.findAll", query = "SELECT e FROM Entitlement e"),
        @NamedQuery(name = "Entitlement.findById", query = "SELECT e FROM Entitlement e WHERE e.id = :id") })
public class Entitlement implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id")
    private String id;
    @Basic(optional = false)
    @Column(name = "CreatedBy")
    private String createdBy;
    @Basic(optional = false)
    @Column(name = "CreatedDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Column(name = "ApprovedBy")
    private String approvedBy;
    @Column(name = "ApprovedDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date approvedDate;
    @Column(name = "NotificationOfficeManager")
    private Boolean notificationOfficeManager;
    @Column(name = "Remark")
    private String remark;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "entitlement")
    private Collection<EntitlementUniformItems> entitlementUniformItemsCollection;
    @JoinColumn(name = "LuStatusId", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private LuStatus luStatusId;
    @JoinColumn(name = "PositionCategory", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private PositionCategory positionCategory;

    
    public Entitlement() {
        this.id = UUID.randomUUID().toString();
    }

    public Entitlement(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public LuStatus getLuStatusId() {
        return luStatusId;
    }

    public void setLuStatusId(LuStatus luStatusId) {
        this.luStatusId = luStatusId;
    }


    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Entitlement)) {
            return false;
        }
        Entitlement other = (Entitlement) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Entitlement[ id=" + id + " ]";
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public Collection<EntitlementUniformItems> getEntitlementUniformItemsCollection() {
        return entitlementUniformItemsCollection;
    }

    public void setEntitlementUniformItemsCollection(
            Collection<EntitlementUniformItems> entitlementUniformItemsCollection) {
        this.entitlementUniformItemsCollection = entitlementUniformItemsCollection;
    }

    public PositionCategory getPositionCategory() {
        return positionCategory;
    }

    public void setPositionCategory(PositionCategory positionCategory) {
        this.positionCategory = positionCategory;
    }

    public Boolean getNotificationOfficeManager() {
        return notificationOfficeManager;
    }

    public void setNotificationOfficeManager(Boolean notificationOfficeManager) {
        this.notificationOfficeManager = notificationOfficeManager;
    }

}
