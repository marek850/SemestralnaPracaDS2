package com.sem2.Events.Assembling;

import com.sem2.Events.EmpFurnitureEvent;
import com.sem2.Events.Moving.MoveToStation;
import com.sem2.Events.Orders.FinalizeOrder;
import com.sem2.FurnitureCompany.Employee;
import com.sem2.FurnitureCompany.Order;
import com.sem2.FurnitureCompany.Enums.EmployeeState;
import com.sem2.FurnitureCompany.Enums.FurnitureType;
import com.sem2.FurnitureCompany.Enums.OrderState;
import com.sem2.FurnitureCompany.Enums.Process;
import com.sem2.SimCore.EventSimulationCore;
import com.sem2.SimCore.FurnitureCompany;
import com.sem2.SimCore.SimulationCore;

public class AssemblyEnd extends EmpFurnitureEvent {

    public AssemblyEnd(double time, EventSimulationCore simulationCore, Employee employee, Order order) {
        super(time, simulationCore, employee, order);
    }
    @Override
    public void execute() {
        super.execute();
        FurnitureCompany sim = (FurnitureCompany) getSimulationCore();
        System.out.println("Assembly end");
        
        if(getOrder().getType() == FurnitureType.WARDROBE){
            getOrder().setState(OrderState.WAITING_FOR_FITTING);
            sim.addOrderForFitting(getOrder());
            sim.freeEmployee(getEmployee());
        } else {
            getOrder().setState(OrderState.FINISHED);
            FinalizeOrder finalizeOrder = new FinalizeOrder(getTime(), sim, getEmployee(), getOrder());
            sim.addEvent(finalizeOrder);
        }
        if (sim.isCAvailable() && sim.isOrderWaitingForFitting()) {
            Employee freeEmployee = sim.getCAvailable();
            Order order = sim.getOrderWaitingForFitting();
            order.getStation().setCurrentProcess(Process.FITTING);
            double moveTime = 0;
            switch (freeEmployee.getCurrentPosition()) {
                case STORAGE:
                    moveTime = getTime() + sim.getStorageMoveTime();
                    break;
                case ASSEMBLY_STATION:
                    moveTime = getTime() + sim.getStationMoveTime();
                    break;
                default:
                    break;
            }
            freeEmployee.setState(EmployeeState.MOVING);
            MoveToStation moveToFitting = new MoveToStation(moveTime, sim, freeEmployee, order);
            sim.addEvent(moveToFitting);
        }
        if(sim.isOrderWaitingForAssembly() && sim.isBAvailable()){
            Employee freeEmployee = sim.getBAvailable();
            Order order = sim.getOrderWaitingForAssembly();
            order.setState(OrderState.BEING_ASSEMBLED);
            AssemblyStart assemblyStart = new AssemblyStart(getTime(), sim, freeEmployee, order);
            sim.addEvent(assemblyStart);
        }
        sim.refreshGUI();
    }
}