package com.sem2.FurnitureCompany;

import java.util.Random;

import com.sem2.FurnitureCompany.Enums.FurnitureType;
import com.sem2.FurnitureCompany.Enums.OrderState;

public class Order {
    private FurnitureType type;
    private int ID;
    private double arrivalTime;
    public String passedEvents = "";
    public double getArrivalTime() {
        return arrivalTime;
    }
    public void setArrivalTime(double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
    public int getID() {
        return ID;
    }
    public void setID(int iD) {
        ID = iD;
    }
    private OrderState state;
    private AssemblyStation station;
    public AssemblyStation getStation() {
        return station;
    }
    public void setStation(AssemblyStation station) {
        this.station = station;
    }
    public Order(int id, FurnitureType furnitureType) {
        type = furnitureType;
        ID = id;
        arrivalTime = 0;
    }
    public OrderState getState() {
        return state;
    }
    public void setState(OrderState state) {
        this.state = state;
    }
    public FurnitureType getType() {
        return type;
    }
    public void setType(FurnitureType type) {
        this.type = type;
    }
}
