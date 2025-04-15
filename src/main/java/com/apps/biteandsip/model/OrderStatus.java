package com.apps.biteandsip.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.io.Serializable;
import java.util.Objects;

@Entity
public class OrderStatus implements Serializable {
    private static final Long serialVersionUUID = -27283L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int stateOrder;

    private String state;

    private String nextState;

    private boolean terminalState;

    public OrderStatus() {
    }

    public OrderStatus(int stateOrder, String state, String nextState, boolean terminalState) {
        this.stateOrder = stateOrder;
        this.state = state;
        this.nextState = nextState;
        this.terminalState = terminalState;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isTerminalState() {
        return terminalState;
    }

    public void setTerminalState(boolean terminalState) {
        this.terminalState = terminalState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderStatus that = (OrderStatus) o;
        return stateOrder == that.stateOrder && terminalState == that.terminalState && Objects.equals(id, that.id) && Objects.equals(state, that.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, stateOrder, state, terminalState);
    }

    @Override
    public String toString() {
        return "OrderStatus{" +
                "id=" + id +
                ", stateOrder=" + stateOrder +
                ", state='" + state + '\'' +
                ", terminalState=" + terminalState +
                '}';
    }

    public int getStateOrder() {
        return stateOrder;
    }

    public void setStateOrder(int stateOrder) {
        this.stateOrder = stateOrder;
    }

    public String getNextState() {
        return nextState;
    }

    public void setNextState(String nextState) {
        this.nextState = nextState;
    }
}
