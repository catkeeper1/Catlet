package org.ckr.msdemo.entity;


import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;


@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    private Timestamp createdAt;
    private String createdBy;
    private String createdByName;

    private Timestamp updatedAt;
    private String updatedBy;
    private String updatedByName;

    private Long versionNo;


    @Column(name = "CREATED_AT")
    public Timestamp getCreatedAt() {
        return createdAt;
    }


    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }


    @Column(name = "CREATED_BY", length = 200)
    public String getCreatedBy() {
        return createdBy;
    }


    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }


    @Column(name = "CREATED_BY_DESC", length = 200)
    public String getCreatedByDesc() {
        return createdByName;
    }


    public void setCreatedByDesc(String createdByName) {
        this.createdByName = createdByName;
    }

    @Column(name = "UPDATED_AT")
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }


    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Column(name = "UPDATED_BY")
    public String getUpdatedBy() {
        return updatedBy;
    }


    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Column(name = "UPDATED_BY_DESC")
    public String getUpdatedByDesc() {
        return updatedByName;
    }


    public void setUpdatedByDesc(String updatedByName) {
        this.updatedByName = updatedByName;
    }

    @Version
    @Column(name = "VERSION_NO")
    public Long getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(Long versionNo) {
        this.versionNo = versionNo;
    }

}
