/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.zemen.udms.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 *
 * @author Yosef.Girma
 */
@Entity
@Table(name = "PositionCategory")
@NamedQueries({
    @NamedQuery(name = "PositionCategory.findAll", query = "SELECT p FROM PositionCategory p"),
    @NamedQuery(name = "PositionCategory.findById", query = "SELECT p FROM PositionCategory p WHERE p.id = :id"),
    @NamedQuery(name = "PositionCategory.findByName", query = "SELECT p FROM PositionCategory p WHERE p.name = :name"),
    @NamedQuery(name = "PositionCategory.findByDescription", query = "SELECT p FROM PositionCategory p WHERE p.description = :description")})
public class PositionCategory implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id")
    private String id;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @OneToMany(mappedBy = "positionCategory")
    private Collection<Employee> employeeCollection;
    @OneToMany(mappedBy = "positionCategory")
    private Collection<Entitlement> entitlementCollection;


    public PositionCategory() {
        this.id = UUID.randomUUID().toString();
    }

    public PositionCategory(String id) {
        this.id = id;
    }

    public PositionCategory(String id, String name) {
        this.id = id;
        this.name = name;
    }

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

    public Collection<Entitlement> getEntitlementCollection() {
        return entitlementCollection;
    }

    public void setEntitlementCollection(Collection<Entitlement> entitlementCollection) {
        this.entitlementCollection = entitlementCollection;
    }

    public Collection<Employee> getEmployeeCollection() {
        return employeeCollection;
    }

    public void setEmployeeCollection(Collection<Employee> employeeCollection) {
        this.employeeCollection = employeeCollection;
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
        if (!(object instanceof PositionCategory)) {
            return false;
        }
        PositionCategory other = (PositionCategory) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.PositionCategory[ id=" + id + " ]";
    }
    
}
