package com.sem2.Events.Moving;

import com.sem2.Events.EmpFurnitureEvent;
import com.sem2.Events.Cutting.PrepareMaterial;
import com.sem2.FurnitureCompany.Employee;
import com.sem2.FurnitureCompany.Order;
import com.sem2.FurnitureCompany.Enums.EmployeeState;
import com.sem2.FurnitureCompany.Enums.OrderState;
import com.sem2.FurnitureCompany.Enums.Position;
import com.sem2.SimCore.EventSimulationCore;
import com.sem2.SimCore.FurnitureCompany;
import com.sem2.SimCore.SimulationCore;
public class MoveToStorage extends EmpFurnitureEvent{

    public MoveToStorage(double time, EventSimulationCore simulationCore, Employee employee, Order order) {
        super(time, simulationCore, employee, order);
        
    }
    @Override
    public void execute() {
        super.execute();
        double moveTime = 0;
        FurnitureCompany sim = (FurnitureCompany) getSimulationCore();
        switch (getEmployee().getCurrentPosition()) {
            case STORAGE:
                moveTime = getTime();
                break;
            case ASSEMBLY_STATION:
                moveTime = getTime() + sim.getStorageMoveTime();
                getEmployee().setPosition(Position.STORAGE);
                break;
            default:
                break;
        }
        getEmployee().setState(EmployeeState.MOVING);
        PrepareMaterial prepareMaterial = new PrepareMaterial(moveTime, sim, getEmployee(), getOrder());
        sim.addEvent(prepareMaterial);
        sim.refreshGUI();
    }   
    
}
