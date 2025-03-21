package com.apps.biteandsip.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "authorities", uniqueConstraints = {@UniqueConstraint(columnNames = "authority")})
public class Authority implements Serializable, GrantedAuthority {
    private static final Long serialVersionUUID = -1289119L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String authority;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "authority_menu",
            joinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "menu_item_id", referencedColumnName = "id"))
    private Set<Menu> menuItems;

    public Authority() {
    }

    public Authority(String authority) {
        this.authority = authority;
    }

    public Authority(String authority, Set<Menu> menuItems) {
        this.authority = authority;
        this.menuItems = menuItems;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthority() {
        return authority;
    }

    public Set<Menu> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(Set<Menu> menuItems) {
        this.menuItems = menuItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Authority authority1 = (Authority) o;
        return Objects.equals(id, authority1.id) && Objects.equals(authority, authority1.authority) && Objects.equals(menuItems, authority1.menuItems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, authority, menuItems);
    }

    @Override
    public String toString() {
        return "Authority{" +
                "id=" + id +
                ", authority='" + authority + '\'' +
                ", menuItems=" + menuItems +
                '}';
    }
}
