/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.zemen.udms.model;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author Yosef.Girma
 */
@Entity
@Table(name = "AccessLog")
@NamedQueries({
    @NamedQuery(name = "AccessLog.findAll", query = "SELECT a FROM AccessLog a"),
    @NamedQuery(name = "AccessLog.findById", query = "SELECT a FROM AccessLog a WHERE a.id = :id"),
    @NamedQuery(name = "AccessLog.findByDate", query = "SELECT a FROM AccessLog a WHERE a.date = :date"),
    @NamedQuery(name = "AccessLog.findByOperation", query = "SELECT a FROM AccessLog a WHERE a.operation = :operation")})
public class AccessLog implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private String id;
    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    @Column(name = "username")
    private String username;
    @Column(name = "operation")
    private String operation;
    @Column(name = "object_info")
    private String objectInfo;

    public AccessLog() {
        this.id = UUID.randomUUID().toString();
    }

    public AccessLog(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getObjectInfo() {
        return objectInfo;
    }

    public void setObjectInfo(String objectInfo) {
        this.objectInfo = objectInfo;
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
        if (!(object instanceof AccessLog)) {
            return false;
        }
        AccessLog other = (AccessLog) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.AccessLog[ id=" + id + " ]";
    }
    
}
