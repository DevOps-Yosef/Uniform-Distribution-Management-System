/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.zemen.udms.dto;

import java.io.Serializable;

public class UniformIssueDetailDTO implements Serializable {

    private String id;
    private Integer imsGrnDetailId;
    private String size;
    private UniformIssueDTO uniformIssue;
    private UniformRequisitionDetailDTO uniformRequisitionDetail;

    
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
    public UniformIssueDTO getUniformIssue() {
        return uniformIssue;
    }
    public void setUniformIssue(UniformIssueDTO uniformIssue) {
        this.uniformIssue = uniformIssue;
    }
    public UniformRequisitionDetailDTO getUniformRequisitionDetail() {
        return uniformRequisitionDetail;
    }
    public void setUniformRequisitionDetail(UniformRequisitionDetailDTO uniformRequisitionDetail) {
        this.uniformRequisitionDetail = uniformRequisitionDetail;
    }
    public Integer getImsGrnDetailId() {
        return imsGrnDetailId;
    }
    public void setImsGrnDetailId(Integer imsGrnDetailId) {
        this.imsGrnDetailId = imsGrnDetailId;
    }


    
}
