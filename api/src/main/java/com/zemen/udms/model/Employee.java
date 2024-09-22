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
@Table(name = "Employee")
@NamedQueries({
        @NamedQuery(name = "Employee.findAll", query = "SELECT e FROM Employee e"),
        @NamedQuery(name = "Employee.findById", query = "SELECT e FROM Employee e WHERE e.id = :id"),
        @NamedQuery(name = "Employee.findByBadgeNumber", query = "SELECT e FROM Employee e WHERE e.badgeNumber = :badgeNumber"),
        @NamedQuery(name = "Employee.findByName", query = "SELECT e FROM Employee e WHERE e.name = :name"),
        @NamedQuery(name = "Employee.findByGender", query = "SELECT e FROM Employee e WHERE e.gender = :gender"),
 })
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id")
    private String id;
    @Column(name = "BadgeNumber")
    private String badgeNumber;
    @Column(name = "UserName")
    private String userName;
    @Basic(optional = false)
    @Column(name = "Name")
    private String name;
    @Column(name = "Gender")
    private String gender;
    @Column(name = "JobPosition")
    private String jobPosition;
    @Column(name = "JobGrade")
    private String jobGrade;
    @Column(name = "JobCategory")
    private String jobCategory;
    @Column(name = "Department")
    private String department;
    @Column(name = "Inactive")
    private Boolean inactive;
    @Column(name = "EMail")
    private String eMail;
    @Column(name = "CreatedBy")
    private String createdBy;
    @Column(name = "CreatedDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @JoinColumn(name = "PositionCategory", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private PositionCategory positionCategory;
    @OneToMany(mappedBy = "employeeId")
    private Collection<UniformRequisition> uniformRequisitionCollection;

    public Employee() {
        this.id = UUID.randomUUID().toString();
    }

    public Employee(String id) {
        this.id = id;
    }

    public Employee(String id, String badgeNumber, String name) {
        this.id = id;
        this.badgeNumber = badgeNumber;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }


    public String getBadgeNumber() {
        return badgeNumber;
    }

    public void setBadgeNumber(String badgeNumber) {
        this.badgeNumber = badgeNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getJobPosition() {
        return jobPosition;
    }

    public void setJobPosition(String jobPosition) {
        this.jobPosition = jobPosition;
    }

    public String getJobGrade() {
        return jobGrade;
    }

    public void setJobGrade(String jobGrade) {
        this.jobGrade = jobGrade;
    }

    public String getJobCategory() {
        return jobCategory;
    }

    public void setJobCategory(String jobCategory) {
        this.jobCategory = jobCategory;
    }

    public Boolean getInactive() {
        return inactive;
    }

    public void setInactive(Boolean inactive) {
        this.inactive = inactive;
    }

    public Collection<UniformRequisition> getUniformRequisitionCollection() {
        return uniformRequisitionCollection;
    }

    public void setUniformRequisitionCollection(Collection<UniformRequisition> uniformRequisitionCollection) {
        this.uniformRequisitionCollection = uniformRequisitionCollection;
    }


    public PositionCategory getPositionCategory() {
        return positionCategory;
    }

    public void setPositionCategory(PositionCategory positionCategory) {
        this.positionCategory = positionCategory;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
        if (!(object instanceof Employee)) {
            return false;
        }
        Employee other = (Employee) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Employee[ id=" + id + " ]";
    }


}
