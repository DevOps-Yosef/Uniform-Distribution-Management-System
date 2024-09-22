/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.zemen.udms.model;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

/**
 *
 * @author Yosef.Girma
 */
@Entity
@Table(name = "EntitlementUniformItems")
@NamedQueries({
    @NamedQuery(name = "EntitlementUniformItems.findAll", query = "SELECT e FROM EntitlementUniformItems e"),
    @NamedQuery(name = "EntitlementUniformItems.findById", query = "SELECT e FROM EntitlementUniformItems e WHERE e.id = :id")})
public class EntitlementUniformItems implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id")
    private String id;
    @Basic(optional = false)
    @Column(name = "IMS_ITEM_ID")
    private int imsItemId;
    @Basic(optional = false)
    @Column(name = "ItemName")
    private String itemName;
    @Basic(optional = false)
    @Column(name = "Quantity")
    private int quantity;
    @Basic(optional = false)
    @Column(name = "Period")
    private int period;
    @JoinColumn(name = "Entitlement", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private Entitlement entitlement;



    public EntitlementUniformItems() {
        this.id = UUID.randomUUID().toString();
    }

    public EntitlementUniformItems(String id) {
        this.id = id;
    }

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
    
    public Entitlement getEntitlement() {
        return entitlement;
    }

    public void setEntitlement(Entitlement entitlement) {
        this.entitlement = entitlement;
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
        if (!(object instanceof EntitlementUniformItems)) {
            return false;
        }
        EntitlementUniformItems other = (EntitlementUniformItems) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.EntitlementUniformItems[ id=" + id + " ]";
    }
    
}
