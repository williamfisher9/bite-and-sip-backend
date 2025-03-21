package com.apps.biteandsip.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "menu")
public class Menu implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

    private String mainMenu;

    private String menuItem;

    private String menuItemLink;

    public Menu() {
    }

    public Menu(String mainMenu, String menuItem, String menuItemLink) {
        this.mainMenu = mainMenu;
        this.menuItem = menuItem;
        this.menuItemLink = menuItemLink;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(String menuItem) {
        this.menuItem = menuItem;
    }

    public String getMainMenu() {
        return mainMenu;
    }

    public void setMainMenu(String mainMenu) {
        this.mainMenu = mainMenu;
    }

    public String getMenuItemLink() {
        return menuItemLink;
    }

    public void setMenuItemLink(String menuItemLink) {
        this.menuItemLink = menuItemLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Menu menu = (Menu) o;
        return Objects.equals(Id, menu.Id) && Objects.equals(mainMenu, menu.mainMenu) && Objects.equals(menuItem, menu.menuItem) && Objects.equals(menuItemLink, menu.menuItemLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Id, mainMenu, menuItem, menuItemLink);
    }

    @Override
    public String toString() {
        return "Menu{" +
                "Id=" + Id +
                ", mainMenu='" + mainMenu + '\'' +
                ", menuItem='" + menuItem + '\'' +
                ", menuItemLink='" + menuItemLink + '\'' +
                '}';
    }
}
