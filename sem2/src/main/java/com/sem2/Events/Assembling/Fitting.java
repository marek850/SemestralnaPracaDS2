package com.sem2.Events.Assembling;

import com.sem2.Events.EmpFurnitureEvent;
import com.sem2.Events.Moving.MoveToStation;
import com.sem2.Events.Orders.FinalizeOrder;
import com.sem2.FurnitureCompany.Employee;
import com.sem2.FurnitureCompany.Order;
import com.sem2.FurnitureCompany.Enums.EmployeeState;
import com.sem2.FurnitureCompany.Enums.OrderState;
import com.sem2.FurnitureCompany.Enums.Process;
import com.sem2.SimCore.EventSimulationCore;
import com.sem2.SimCore.FurnitureCompany;

public class Fitting extends EmpFurnitureEvent{

    public Fitting(double time, EventSimulationCore simulationCore, Employee employee, Order order) {
        super(time, simulationCore, employee, order);
    }
    @Override
    public void execute() {
        FurnitureCompany sim = (FurnitureCompany) getSimulationCore();
        getEmployee().setState(EmployeeState.IDLE);
        getOrder().setState(OrderState.FITTED);
        getOrder().getStation().setCurrentProcess(Process.NONE);
        Employee finishedEmployee = getEmployee();

        if (sim.isOrderWaitingForFitting()) {
            Order waitingOrder = sim.getOrderWaitingForFitting();
            finishedEmployee.setState(EmployeeState.MOVING);
            
            MoveToStation move = new MoveToStation(getTime() + sim.getStationMoveTime(), sim, finishedEmployee, waitingOrder);
            sim.addEvent(move);
        } else if(sim.isOrderWaitingForVarnish()){
            Order waitingOrder = sim.getOrderWaitingForVarnish();
            finishedEmployee.setState(EmployeeState.MOVING);
            MoveToStation move = new MoveToStation(getTime() + sim.getStationMoveTime(), sim, finishedEmployee, waitingOrder);
            sim.addEvent(move);
        } else{
            sim.freeEmployee(finishedEmployee);
        }
        FinalizeOrder finalizeOrder = new FinalizeOrder(getTime(), sim, null, getOrder());
        sim.addEvent(finalizeOrder);
    }
}
