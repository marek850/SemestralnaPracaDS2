package com.sem2.Events.Assembling;

import com.sem2.Events.EmpFurnitureEvent;
import com.sem2.Events.Moving.MoveToStation;
import com.sem2.FurnitureCompany.Employee;
import com.sem2.FurnitureCompany.Order;
import com.sem2.FurnitureCompany.Enums.EmployeeState;
import com.sem2.FurnitureCompany.Enums.Process;
import com.sem2.SimCore.EventSimulationCore;
import com.sem2.SimCore.FurnitureCompany;
import com.sem2.SimCore.SimulationCore;

public class AssemblyStart extends EmpFurnitureEvent{

    public AssemblyStart(double time, EventSimulationCore simulationCore, Employee employee, Order order) {
        super(time, simulationCore, employee, order);
    }
    @Override
    public void execute() {
        super.execute();
        FurnitureCompany sim = (FurnitureCompany) getSimulationCore();
        getEmployee().setState(EmployeeState.MOVING);
        double moveTime = 0;
        switch (getEmployee().getCurrentPosition()) {
            case STORAGE:
                moveTime = getTime() + sim.getStorageMoveTime();
                
                break;
            case ASSEMBLY_STATION:
                moveTime = getTime() + sim.getStationMoveTime();
                
                break;
            default:
                break;
        }
        getOrder().getStation().setCurrentProcess(Process.ASSEMBLING);
        MoveToStation moveToAssemble = new MoveToStation(moveTime, sim, getEmployee(), getOrder());
        sim.addEvent(moveToAssemble);
        sim.refreshGUI();
    }
    
}
