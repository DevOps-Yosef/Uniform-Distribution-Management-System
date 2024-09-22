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
@Table(name = "LuStatus")
@NamedQueries({
    @NamedQuery(name = "LuStatus.findAll", query = "SELECT l FROM LuStatus l"),
    @NamedQuery(name = "LuStatus.findById", query = "SELECT l FROM LuStatus l WHERE l.id = :id"),
    @NamedQuery(name = "LuStatus.findByName", query = "SELECT l FROM LuStatus l WHERE l.name = :name"),
    @NamedQuery(name = "LuStatus.findByDescription", query = "SELECT l FROM LuStatus l WHERE l.description = :description")})
public class LuStatus implements Serializable {

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
    @OneToMany(mappedBy = "luStatusId")
    private Collection<Entitlement> entitlementCollection;
    @OneToMany(mappedBy = "luStatusId")
    private Collection<UniformIssue> uniformIssueCollection;
    @OneToMany(mappedBy = "luStatusId")
    private Collection<UniformRequisition> uniformRequisitionCollection;

    public LuStatus() {
        this.id = UUID.randomUUID().toString();
    }

    public LuStatus(String id) {
        this.id = id;
    }

    public LuStatus(String id, String name) {
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

    public Collection<UniformIssue> getUniformIssueCollection() {
        return uniformIssueCollection;
    }

    public void setUniformIssueCollection(Collection<UniformIssue> uniformIssueCollection) {
        this.uniformIssueCollection = uniformIssueCollection;
    }

    

    public Collection<UniformRequisition> getUniformRequisitionCollection() {
        return uniformRequisitionCollection;
    }

    public void setUniformRequisitionCollection(Collection<UniformRequisition> uniformRequisitionCollection) {
        this.uniformRequisitionCollection = uniformRequisitionCollection;
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
        if (!(object instanceof LuStatus)) {
            return false;
        }
        LuStatus other = (LuStatus) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.LuStatus[ id=" + id + " ]";
    }
    
}
