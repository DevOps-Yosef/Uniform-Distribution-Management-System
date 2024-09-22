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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 *
 * @author Yosef.Girma
 */
@Entity
@Table(name = "UniformRequisitionDetail")
@NamedQueries({
    @NamedQuery(name = "UniformRequisitionDetail.findAll", query = "SELECT u FROM UniformRequisitionDetail u"),
    @NamedQuery(name = "UniformRequisitionDetail.findById", query = "SELECT u FROM UniformRequisitionDetail u WHERE u.id = :id")})
public class UniformRequisitionDetail implements Serializable {

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
    @Column(name = "Size")
    private String size;
    @OneToMany(mappedBy = "uniformRequisitionDetail")
    private Collection<UniformIssueDetail> uniformIssueDetailCollection;
    @JoinColumn(name = "UniformRequisitionId", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private UniformRequisition uniformRequisitionId;

    public UniformRequisitionDetail() {
        this.id = UUID.randomUUID().toString();
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

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }
   

    public Collection<UniformIssueDetail> getUniformIssueDetailCollection() {
        return uniformIssueDetailCollection;
    }

    public void setUniformIssueDetailCollection(Collection<UniformIssueDetail> uniformIssueDetailCollection) {
        this.uniformIssueDetailCollection = uniformIssueDetailCollection;
    }

    public UniformRequisition getUniformRequisitionId() {
        return uniformRequisitionId;
    }

    public void setUniformRequisitionId(UniformRequisition uniformRequisitionId) {
        this.uniformRequisitionId = uniformRequisitionId;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UniformRequisitionDetail)) {
            return false;
        }
        UniformRequisitionDetail other = (UniformRequisitionDetail) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.UniformRequisitionDetail[ id=" + id + " ]";
    }


    
}
