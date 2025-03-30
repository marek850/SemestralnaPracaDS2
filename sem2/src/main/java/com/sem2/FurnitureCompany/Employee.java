package com.sem2.FurnitureCompany;

import java.lang.reflect.Type;

import com.sem2.FurnitureCompany.Enums.EmployeeState;
import com.sem2.FurnitureCompany.Enums.EmployeeType;
import com.sem2.FurnitureCompany.Enums.Position;

public class Employee {
    private EmployeeState state;
    
    private Position position;
    private EmployeeType type;
    private AssemblyStation station;
    public AssemblyStation getStation() {
        return station;
    }
    public void setStation(AssemblyStation station) {
        this.station = station;
    }
    public Employee(EmployeeType type) {
        this.type = type;
        this.state = EmployeeState.IDLE;
        this.position = Position.STORAGE;
        this.station = null;
    }
    public EmployeeType getType() {
        return type;
    }
    public Position getCurrentPosition() {
        return position;
    }
    public void setPosition(Position position) {
        this.position = position;
    }
    public EmployeeState getState() {
        return state;
    }
    public void setState(EmployeeState state) {
        this.state = state;
    }
    
}
