package com.sem2.Events.Varnishing;

import com.sem2.Events.EmpFurnitureEvent;
import com.sem2.Events.Assembling.AssemblyEnd;
import com.sem2.Events.Moving.MoveToStation;
import com.sem2.Events.Moving.MoveToStorage;
import com.sem2.FurnitureCompany.Employee;
import com.sem2.FurnitureCompany.Order;
import com.sem2.FurnitureCompany.Enums.EmployeeState;
import com.sem2.FurnitureCompany.Enums.OrderState;
import com.sem2.FurnitureCompany.Enums.Position;
import com.sem2.FurnitureCompany.Enums.Process;
import com.sem2.SimCore.EventSimulationCore;
import com.sem2.SimCore.FurnitureCompany;

public class VarnishingEnd extends EmpFurnitureEvent{

    public VarnishingEnd(double time, EventSimulationCore simulationCore, Employee employee, Order order) {
        super(time, simulationCore, employee, order);
    }
    @Override
    public void execute() {
        FurnitureCompany sim = (FurnitureCompany) getSimulationCore();

        getOrder().setState(OrderState.VARNISHED);
        getEmployee().setState(EmployeeState.IDLE);
        getOrder().getStation().setCurrentProcess(Process.NONE);
        Employee finishedEmployee = getEmployee();

        if (sim.isOrderWaitingForFitting()) {
            Order waitingOrder = sim.getOrderWaitingForFitting();
            finishedEmployee.setState(EmployeeState.MOVING);
            MoveToStation moveToStation = new MoveToStation(getTime()+sim.getStationMoveTime(), sim, finishedEmployee, waitingOrder);
            sim.addEvent(moveToStation);
        } 
        else if (sim.isOrderWaitingForVarnish()){
            Order varnishOrder = sim.getOrderWaitingForVarnish();
            finishedEmployee.setState(EmployeeState.MOVING);
            //System.out.println("idem odtialto");
            MoveToStation moveToStation = new MoveToStation(getTime()+sim.getStationMoveTime(), sim, finishedEmployee, varnishOrder);
            sim.addEvent(moveToStation);
        } else{
            sim.freeEmployee(finishedEmployee);
        }

        if (sim.isBAvailable()) {
            Employee freeEmployee = sim.getBAvailable();
            freeEmployee.setWorking(true, getTime());
            if (freeEmployee.getCurrentPosition() == Position.ASSEMBLY_STATION && freeEmployee.getStation() == getOrder().getStation()) {
                freeEmployee.setState(EmployeeState.ASSEMBLING);
                getOrder().setState(OrderState.BEING_ASSEMBLED);
                getOrder().getStation().setCurrentProcess(Process.ASSEMBLING);
                AssemblyEnd assemblyEnd = new AssemblyEnd(getTime() + sim.getAssembleTime(getOrder()), sim, freeEmployee, getOrder());
                sim.addEvent(assemblyEnd);
            } else if(freeEmployee.getCurrentPosition() == Position.ASSEMBLY_STATION && freeEmployee.getStation() != getOrder().getStation()){
                freeEmployee.setState(EmployeeState.MOVING);
                MoveToStation moveToStation = new MoveToStation(getTime() + sim.getStationMoveTime(), sim, freeEmployee, getOrder());
                sim.addEvent(moveToStation);
            } else if (freeEmployee.getCurrentPosition() == Position.STORAGE) {
                freeEmployee.setState(EmployeeState.MOVING);
                MoveToStorage moveToStorage = new MoveToStorage(getTime() + sim.getStorageMoveTime(), sim, freeEmployee, getOrder());
                sim.addEvent(moveToStorage);
            } else{
                throw new IllegalStateException("Employee B must be in assembly station or storage when he is available-VarnishingEnd 8");
            }
        }
        else{
            getOrder().setState(OrderState.WAITING_FOR_ASSEMBLY);
            getOrder().getStation().setCurrentProcess(Process.NONE);
            sim.addOrderForAssembly(getOrder());
        }
    }
}
