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
@Table(name = "UniformRequisition")
@NamedQueries({
    @NamedQuery(name = "UniformRequisition.findAll", query = "SELECT u FROM UniformRequisition u"),
    @NamedQuery(name = "UniformRequisition.findById", query = "SELECT u FROM UniformRequisition u WHERE u.id = :id")})
public class UniformRequisition implements Serializable {

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
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "uniformRequisitionId")
    private Collection<UniformRequisitionDetail> uniformRequisitionDetailCollection;
    @JoinColumn(name = "EmployeeId", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private Employee employeeId;
    @JoinColumn(name = "LuStatusId", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private LuStatus luStatusId;
    @Column(name = "NotificationManager")
    private Boolean notificationManager;
    @Column(name = "NotificationForIssue")
    private Boolean notificationForIssue;
    @Column(name = "Remark")
    private String remark;

    public UniformRequisition() {
        this.id = UUID.randomUUID().toString();
    }

    public UniformRequisition(String id) {
        this.id = id;
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

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }


    
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public Employee getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Employee employeeId) {
        this.employeeId = employeeId;
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
        if (!(object instanceof UniformRequisition)) {
            return false;
        }
        UniformRequisition other = (UniformRequisition) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.UniformRequisition[ id=" + id + " ]";
    }

    public LuStatus getLuStatusId() {
        return luStatusId;
    }

    public void setLuStatusId(LuStatus luStatusId) {
        this.luStatusId = luStatusId;
    }

    public Collection<UniformRequisitionDetail> getUniformRequisitionDetailCollection() {
        return uniformRequisitionDetailCollection;
    }

    public void setUniformRequisitionDetailCollection(
            Collection<UniformRequisitionDetail> uniformRequisitionDetailCollection) {
        this.uniformRequisitionDetailCollection = uniformRequisitionDetailCollection;
    }

    public Boolean getNotificationManager() {
        return notificationManager;
    }

    public void setNotificationManager(Boolean notificationManager) {
        this.notificationManager = notificationManager;
    }

    public Boolean getNotificationForIssue() {
        return notificationForIssue;
    }

    public void setNotificationForIssue(Boolean notificationForIssue) {
        this.notificationForIssue = notificationForIssue;
    }
    
}
