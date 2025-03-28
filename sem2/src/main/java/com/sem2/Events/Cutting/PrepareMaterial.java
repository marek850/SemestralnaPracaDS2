package com.sem2.Events.Cutting;

import com.sem2.Events.EmpFurnitureEvent;
import com.sem2.Events.Moving.MoveToStation;
import com.sem2.FurnitureCompany.Employee;
import com.sem2.FurnitureCompany.Order;
import com.sem2.SimCore.EventSimulationCore;
import com.sem2.SimCore.FurnitureCompany;
import com.sem2.SimCore.SimulationCore;

public class PrepareMaterial extends EmpFurnitureEvent{

    public PrepareMaterial(double time, EventSimulationCore simulationCore, Employee employee, Order order) {
        super(time, simulationCore, employee, order);
    }
    @Override
    public void execute() {
        super.execute();
        FurnitureCompany sim = (FurnitureCompany) getSimulationCore();
        double prepTime = getTime() + sim.getMatPrepTime();
        MoveToStation moveToStation = new MoveToStation(prepTime, sim, getEmployee(), getOrder());
        sim.addEvent(moveToStation);
    }
}
