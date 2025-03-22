package com.apps.biteandsip.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.io.Serializable;
import java.util.Objects;

@Entity
public class Settings implements Serializable {
    private static final Long serialVersionUUID = -27382L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paramName;
    private String paramValue;

    public Settings() {
    }

    public Settings(Long id, String paramName, String paramValue) {
        this.id = id;
        this.paramName = paramName;
        this.paramValue = paramValue;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Settings settings = (Settings) o;
        return Objects.equals(id, settings.id) && Objects.equals(paramName, settings.paramName) && Objects.equals(paramValue, settings.paramValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, paramName, paramValue);
    }

    @Override
    public String toString() {
        return "Settings{" +
                "id=" + id +
                ", paramName='" + paramName + '\'' +
                ", paramValue='" + paramValue + '\'' +
                '}';
    }
}
