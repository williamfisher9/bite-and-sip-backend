package com.apps.biteandsip.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.io.Serializable;

@Entity
public class Settings implements Serializable {
    private static final Long serialVersionUUID = -27382L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paramName;
    private String paramValue;
    private String paramDesc;

    public Settings() {
    }

    public Settings(Long id, String paramName, String paramValue, String paramDesc) {
        this.id = id;
        this.paramName = paramName;
        this.paramValue = paramValue;
        this.paramDesc = paramDesc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public String getParamDesc() {
        return paramDesc;
    }

    public void setParamDesc(String paramDesc) {
        this.paramDesc = paramDesc;
    }
}
