package com.sem2.Events;

import com.sem2.FurnitureCompany.Employee;
import com.sem2.FurnitureCompany.Order;
import com.sem2.SimCore.EventSimulationCore;
import com.sem2.SimCore.FurnitureCompany;

public class EmpFurnitureEvent extends Event{
    private Employee employee;
    

    private Order order;
    
    public EmpFurnitureEvent(double time, EventSimulationCore simulationCore, Employee employee, Order order) {
        super(time, simulationCore);
        this.employee = employee;
        this.order = order;
    }
    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
    public Order getOrder() {
        return order;
    }
    public void setOrder(Order order) {
        this.order = order;
    }
    @Override
    public int compareTo(Event o) {
        return Double.compare(this.getTime(), o.getTime());
    }

    @Override
    public void execute() {
        FurnitureCompany sim = (FurnitureCompany) getSimulationCore();
        sim.setCurrentTime(getTime());
    }
    
}
