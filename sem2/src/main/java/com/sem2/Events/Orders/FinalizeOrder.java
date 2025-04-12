package com.sem2.Events.Orders;

import com.sem2.Events.EmpFurnitureEvent;
import com.sem2.FurnitureCompany.Employee;
import com.sem2.FurnitureCompany.Order;
import com.sem2.FurnitureCompany.Enums.OrderState;
import com.sem2.FurnitureCompany.Enums.Process;
import com.sem2.SimCore.EventSimulationCore;
import com.sem2.SimCore.FurnitureCompany;

public class FinalizeOrder extends EmpFurnitureEvent{

    public FinalizeOrder(double time, EventSimulationCore simulationCore, Employee employee, Order order) {
        super(time, simulationCore, employee, order);
    }

    @Override
    public void execute() {
        FurnitureCompany sim = (FurnitureCompany) getSimulationCore();
        getOrder().setState(OrderState.FINISHED);
        getOrder().getStation().setCurrentProcess(Process.NONE);
        sim.getOrderFinishTime().addValue(getTime() - getOrder().getArrivalTime());
        sim.addFreeStation(getOrder().getStation());
        sim.removeActiveOrder(getOrder());
    }
}
