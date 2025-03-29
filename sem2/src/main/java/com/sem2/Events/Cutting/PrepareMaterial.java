package com.sem2.Events.Cutting;

import com.sem2.Events.EmpFurnitureEvent;
import com.sem2.Events.Moving.MoveToStation;
import com.sem2.FurnitureCompany.Employee;
import com.sem2.FurnitureCompany.Order;
import com.sem2.FurnitureCompany.Enums.EmployeeState;
import com.sem2.FurnitureCompany.Enums.OrderState;
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
        System.out.println("Prepare material start");
        FurnitureCompany sim = (FurnitureCompany) getSimulationCore();
        double prepTime = getTime() + sim.getMatPrepTime();
        getEmployee().setState(EmployeeState.PREPARING_MATERIAL);
        getOrder().setState(OrderState.PREPARING_MATERIAL);
        MoveToStation moveToStation = new MoveToStation(prepTime, sim, getEmployee(), getOrder());
        sim.addEvent(moveToStation);
    }
}
