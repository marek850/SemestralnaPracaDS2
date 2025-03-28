package com.sem2.Events.Assembling;

import com.sem2.Events.EmpFurnitureEvent;
import com.sem2.Events.Orders.FinalizeOrder;
import com.sem2.FurnitureCompany.Employee;
import com.sem2.FurnitureCompany.Order;
import com.sem2.FurnitureCompany.Enums.OrderState;
import com.sem2.SimCore.EventSimulationCore;
import com.sem2.SimCore.FurnitureCompany;
import com.sem2.SimCore.SimulationCore;

public class Fitting extends EmpFurnitureEvent{

    public Fitting(double time, EventSimulationCore simulationCore, Employee employee, Order order) {
        super(time, simulationCore, employee, order);
    }
    @Override
    public void execute() {
        super.execute();
        FurnitureCompany sim = (FurnitureCompany) getSimulationCore();
        FinalizeOrder finalizeOrder = new FinalizeOrder(getTime(), sim, getEmployee(), getOrder());
        sim.addEvent(finalizeOrder);
        getOrder().setState(OrderState.FINISHED);
        sim.getOrderFinishTime().addValue(getTime() - getOrder().getArrivalTime());
        sim.freeEmployee(getEmployee());
    }
}
