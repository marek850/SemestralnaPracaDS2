package com.sem2.Events.Assembling;

import com.sem2.Events.EmpFurnitureEvent;
import com.sem2.Events.Moving.MoveToStation;
import com.sem2.Events.Moving.MoveToStorage;
import com.sem2.Events.Orders.FinalizeOrder;
import com.sem2.FurnitureCompany.Employee;
import com.sem2.FurnitureCompany.Order;
import com.sem2.FurnitureCompany.Enums.EmployeeState;
import com.sem2.FurnitureCompany.Enums.FurnitureType;
import com.sem2.FurnitureCompany.Enums.OrderState;
import com.sem2.FurnitureCompany.Enums.Position;
import com.sem2.FurnitureCompany.Enums.Process;
import com.sem2.SimCore.EventSimulationCore;
import com.sem2.SimCore.FurnitureCompany;

public class AssemblyEnd extends EmpFurnitureEvent {

    public AssemblyEnd(double time, EventSimulationCore simulationCore, Employee employee, Order order) {
        super(time, simulationCore, employee, order);
    }
    @Override
    public void execute() {
        FurnitureCompany sim = (FurnitureCompany) getSimulationCore();
        getOrder().setState(OrderState.ASSEMBLED);
        getEmployee().setState(EmployeeState.IDLE);
        getOrder().getStation().setCurrentProcess(Process.NONE);
        Employee finishedEmployee = getEmployee();

        if (sim.isOrderWaitingForAssembly()) {
            Order waitingOrder = sim.getOrderWaitingForAssembly();
            waitingOrder.getStation().setCurrentProcess(Process.NONE);
            finishedEmployee.setState(EmployeeState.MOVING);
            MoveToStation moveToStation = new MoveToStation(getTime() + sim.getStationMoveTime(), sim, finishedEmployee, waitingOrder);
            sim.addEvent(moveToStation);
        } else{
            sim.freeEmployee(finishedEmployee);
        }

        if (sim.isCAvailable() && getOrder().getType() == FurnitureType.WARDROBE) {
            Employee freeEmployee = sim.getCAvailable();
            freeEmployee.setWorking(true, getTime());
            if (freeEmployee.getCurrentPosition() == Position.ASSEMBLY_STATION && freeEmployee.getStation() == getOrder().getStation()) {
                freeEmployee.setState(EmployeeState.FITTING);
                getOrder().setState(OrderState.BEING_FITTED);
                getOrder().getStation().setCurrentProcess(Process.FITTING);
                Fitting fitting = new Fitting(getTime() + sim.getFittingTime(), sim, freeEmployee, getOrder());
                sim.addEvent(fitting);
            } else if(freeEmployee.getCurrentPosition() == Position.ASSEMBLY_STATION && freeEmployee.getStation() != getOrder().getStation()){
                freeEmployee.setState(EmployeeState.MOVING);
                MoveToStation moveToStation = new MoveToStation(getTime() + sim.getStationMoveTime(), sim, freeEmployee, getOrder());
                sim.addEvent(moveToStation);
            } else if(freeEmployee.getCurrentPosition() == Position.STORAGE){
                freeEmployee.setState(EmployeeState.MOVING);
                MoveToStorage moveToStorage = new MoveToStorage(getTime() + sim.getStorageMoveTime(), sim, freeEmployee, getOrder());
                sim.addEvent(moveToStorage);
            } else {
                throw new IllegalStateException("Employee type C with waiting order cannot be here-assembling-end4");
            }
        } else if (getOrder().getType() == FurnitureType.WARDROBE) {
            getOrder().setState(OrderState.WAITING_FOR_FITTING);
            getOrder().getStation().setCurrentProcess(Process.NONE);
            sim.addOrderForFitting(getOrder());
        } else {
            FinalizeOrder finalizeOrder = new FinalizeOrder(getTime(), sim,null, getOrder());
            sim.addEvent(finalizeOrder);
        }
    }
}