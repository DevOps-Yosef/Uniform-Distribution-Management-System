/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.zemen.udms.model;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

/**
 *
 * @author Yosef.Girma
 */
@Entity
@Table(name = "UniformIssueDetail")
@NamedQueries({
    @NamedQuery(name = "UniformIssueDetail.findAll", query = "SELECT u FROM UniformIssueDetail u"),
    @NamedQuery(name = "UniformIssueDetail.findById", query = "SELECT u FROM UniformIssueDetail u WHERE u.id = :id"),
    @NamedQuery(name = "UniformIssueDetail.findBySize", query = "SELECT u FROM UniformIssueDetail u WHERE u.size = :size")})
public class UniformIssueDetail implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id")
    private String id;
    @Column(name = "IMS_GRN_DETAIL_ID")
    private Integer imsGrnDetailId;
    @Column(name = "Size")
    private String size;
    @JoinColumn(name = "UniformIssue", referencedColumnName = "Id")
    @ManyToOne
    private UniformIssue uniformIssue;
    @JoinColumn(name = "UniformRequisitionDetail", referencedColumnName = "Id")
    @ManyToOne
    private UniformRequisitionDetail uniformRequisitionDetail;


    public UniformIssueDetail() {
        this.id = UUID.randomUUID().toString();
    }

    public UniformIssueDetail(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public UniformIssue getUniformIssue() {
        return uniformIssue;
    }

    public void setUniformIssue(UniformIssue uniformIssue) {
        this.uniformIssue = uniformIssue;
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
        if (!(object instanceof UniformIssueDetail)) {
            return false;
        }
        UniformIssueDetail other = (UniformIssueDetail) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.zemen.udms.model.UniformIssueDetail[ id=" + id + " ]";
    }

    public Integer getImsGrnDetailId() {
        return imsGrnDetailId;
    }

    public void setImsGrnDetailId(Integer imsGrnDetailId) {
        this.imsGrnDetailId = imsGrnDetailId;
    }

    public UniformRequisitionDetail getUniformRequisitionDetail() {
        return uniformRequisitionDetail;
    }

    public void setUniformRequisitionDetail(UniformRequisitionDetail uniformRequisitionDetail) {
        this.uniformRequisitionDetail = uniformRequisitionDetail;
    }
    
}
