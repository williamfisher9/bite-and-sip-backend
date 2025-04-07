package com.apps.biteandsip.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "password_tokens")
public class PasswordToken implements Serializable {
    private static final Long serialVersionUUID = -19291L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId;

    private String token;

    private LocalDateTime tokenExpirationDate;

    public PasswordToken() {
    }

    public PasswordToken(Long userId, String token, LocalDateTime tokenExpirationDate) {
        this.userId = userId;
        this.token = token;
        this.tokenExpirationDate = tokenExpirationDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getTokenExpirationDate() {
        return tokenExpirationDate;
    }

    public void setTokenExpirationDate(LocalDateTime tokenExpirationDate) {
        this.tokenExpirationDate = tokenExpirationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PasswordToken that = (PasswordToken) o;
        return Objects.equals(id, that.id) && Objects.equals(userId, that.userId) && Objects.equals(token, that.token) && Objects.equals(tokenExpirationDate, that.tokenExpirationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, token, tokenExpirationDate);
    }

    @Override
    public String toString() {
        return "PasswordToken{" +
                "id=" + id +
                ", userId=" + userId +
                ", token='" + token + '\'' +
                ", tokenExpirationDate=" + tokenExpirationDate +
                '}';
    }
}
