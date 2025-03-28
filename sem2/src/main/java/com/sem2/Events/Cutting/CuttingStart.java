package com.sem2.Events.Cutting;

import com.sem2.Events.EmpFurnitureEvent;
import com.sem2.FurnitureCompany.Employee;
import com.sem2.FurnitureCompany.Order;
import com.sem2.SimCore.EventSimulationCore;
import com.sem2.SimCore.FurnitureCompany;
import com.sem2.SimCore.SimulationCore;

public class CuttingStart extends EmpFurnitureEvent{

    public CuttingStart(double time, EventSimulationCore simulationCore, Employee employee, Order order) {
        super(time, simulationCore, employee, order);
    }
    @Override
    public void execute() {
        super.execute();
        FurnitureCompany sim = (FurnitureCompany) getSimulationCore();
        CuttingEnd cuttingEnd = new CuttingEnd(getTime() + sim.getCuttingTime(getOrder()), sim, getEmployee(), getOrder());
        sim.addEvent(cuttingEnd);
        sim.refreshGUI();
    }
}
