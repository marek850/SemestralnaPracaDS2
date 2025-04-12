package com.sem2.Events;

import com.sem2.FurnitureCompany.Employee;
import com.sem2.FurnitureCompany.Order;
import com.sem2.SimCore.EventSimulationCore;
import com.sem2.SimCore.FurnitureCompany;

public class BasicFurnitureEvent extends Event {
    private Order order;
    

    private Employee employee;
    
    public BasicFurnitureEvent(double time, EventSimulationCore simulationCore) {
        super(time, simulationCore);
    }

    @Override
    public int compareTo(Event o) {
        return Double.compare(this.getTime(), o.getTime());
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
    public void execute() {
        FurnitureCompany sim = (FurnitureCompany) getSimulationCore();
        sim.setCurrentTime(getTime());
    }
    
}
