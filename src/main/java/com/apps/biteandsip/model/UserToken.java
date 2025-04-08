package com.apps.biteandsip.model;

import com.apps.biteandsip.enums.UserTokenStatus;
import com.apps.biteandsip.enums.UserTokenType;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "user_tokens")
public class UserToken implements Serializable {
    private static final Long serialVersionUUID = -19291L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String token;

    @Enumerated(EnumType.STRING)
    private UserTokenStatus status;

    @Enumerated(EnumType.STRING)
    private UserTokenType type;

    private LocalDateTime tokenExpirationDate;

    public UserToken() {
    }

    public UserToken(Long userId, String token, UserTokenStatus status, UserTokenType type, LocalDateTime tokenExpirationDate) {
        this.userId = userId;
        this.token = token;
        this.status = status;
        this.type = type;
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

    public UserTokenStatus getStatus() {
        return status;
    }

    public void setStatus(UserTokenStatus status) {
        this.status = status;
    }

    public UserTokenType getType() {
        return type;
    }

    public void setType(UserTokenType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserToken userToken = (UserToken) o;
        return Objects.equals(id, userToken.id) && Objects.equals(userId, userToken.userId) && Objects.equals(token, userToken.token) && status == userToken.status && type == userToken.type && Objects.equals(tokenExpirationDate, userToken.tokenExpirationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, token, status, type, tokenExpirationDate);
    }

    @Override
    public String toString() {
        return "UserToken{" +
                "id=" + id +
                ", userId=" + userId +
                ", token='" + token + '\'' +
                ", status=" + status +
                ", type=" + type +
                ", tokenExpirationDate=" + tokenExpirationDate +
                '}';
    }
}
