package com.sem2.Events.Cutting;

import com.sem2.Events.EmpFurnitureEvent;
import com.sem2.Events.Moving.MoveToStorage;
import com.sem2.FurnitureCompany.Employee;
import com.sem2.FurnitureCompany.Order;
import com.sem2.FurnitureCompany.Enums.EmployeeState;
import com.sem2.FurnitureCompany.Enums.OrderState;
import com.sem2.SimCore.EventSimulationCore;
import com.sem2.SimCore.FurnitureCompany;

public class PrepareMaterial extends EmpFurnitureEvent{

    public PrepareMaterial(double time, EventSimulationCore simulationCore, Employee employee, Order order) {
        super(time, simulationCore, employee, order);
    }
    @Override
    public void execute() {
        FurnitureCompany sim = (FurnitureCompany) getSimulationCore();
        getOrder().setState(OrderState.MATERIAL_PREPARED);
        getEmployee().setState(EmployeeState.MOVING);
        MoveToStorage movefromStorage = new MoveToStorage(getTime() + sim.getStorageMoveTime(), sim, getEmployee(), getOrder());
        sim.addEvent(movefromStorage);
    }
}
