/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.zemen.udms.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

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

/**
 *
 * @author Yosef.Girma
 */
@Entity
@Table(name = "UniformIssue")
@NamedQueries({
    @NamedQuery(name = "UniformIssue.findAll", query = "SELECT u FROM UniformIssue u"),
    @NamedQuery(name = "UniformIssue.findById", query = "SELECT u FROM UniformIssue u WHERE u.id = :id")})
public class UniformIssue implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id")
    private String id;
    @Column(name = "Department")
    private String department;
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
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},mappedBy = "uniformIssue")
    private Collection<UniformIssueDetail> uniformIssueDetailCollection;
    @JoinColumn(name = "LuStatusId", referencedColumnName = "Id")
    @ManyToOne
    private LuStatus luStatusId;
    @Column(name = "NotificationOfficeManager")
    private Boolean notificationOfficeManager;
    @Column(name = "Remark")
    private String remark;

    public UniformIssue() {
        this.id = UUID.randomUUID().toString();
    }

    public UniformIssue(String id) {
        this.id = id;
    }

    public UniformIssue(String id, Date createdDate) {
        this.id = id;
        this.createdDate = createdDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedBy() {
        return createdBy;
    }


    
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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



    public Collection<UniformIssueDetail> getUniformIssueDetailCollection() {
        return uniformIssueDetailCollection;
    }

    public void setUniformIssueDetailCollection(Collection<UniformIssueDetail> uniformIssueDetailCollection) {
        this.uniformIssueDetailCollection = uniformIssueDetailCollection;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
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
        if (!(object instanceof UniformIssue)) {
            return false;
        }
        UniformIssue other = (UniformIssue) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.UniformIssue[ id=" + id + " ]";
    }

    public Boolean getNotificationOfficeManager() {
        return notificationOfficeManager;
    }

    public void setNotificationOfficeManager(Boolean notificationOfficeManager) {
        this.notificationOfficeManager = notificationOfficeManager;
    }
    
    
}
