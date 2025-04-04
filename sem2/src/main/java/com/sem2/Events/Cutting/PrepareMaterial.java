package com.sem2.Events.Cutting;

import com.sem2.Events.EmpFurnitureEvent;
import com.sem2.Events.Moving.MoveToStation;
import com.sem2.Events.Moving.MoveToStorage;
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
        getOrder().passedEvents += "MaterialPrepare,";
        if (getOrder().getID() == 1631598044) {
            System.out.println();
        }
        FurnitureCompany sim = (FurnitureCompany) getSimulationCore();
        if (getEmployee().getState() != EmployeeState.PREPARING_MATERIAL) {
            throw new IllegalStateException("Employee must be preparing material when arriving -PrepareMaterial 1");
            
        }
        if(getOrder().getState() != OrderState.PREPARING_MATERIAL) {
            throw new IllegalStateException("Order must be preparing material when arriving -PrepareMaterial 2");
            
        }
        getOrder().setState(OrderState.MATERIAL_PREPARED);

        getEmployee().setState(EmployeeState.MOVING);
        
        MoveToStorage movefromStorage = new MoveToStorage(getTime() + sim.getStorageMoveTime(), sim, getEmployee(), getOrder());
        sim.addEvent(movefromStorage);
    }
}
