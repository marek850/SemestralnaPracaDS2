package com.sem2.Events.Assembling;

import com.sem2.Events.EmpFurnitureEvent;
import com.sem2.Events.Moving.MoveToStation;
import com.sem2.Events.Orders.FinalizeOrder;
import com.sem2.FurnitureCompany.Employee;
import com.sem2.FurnitureCompany.Order;
import com.sem2.FurnitureCompany.Enums.EmployeeState;
import com.sem2.FurnitureCompany.Enums.EmployeeType;
import com.sem2.FurnitureCompany.Enums.OrderState;
import com.sem2.FurnitureCompany.Enums.Process;
import com.sem2.SimCore.EventSimulationCore;
import com.sem2.SimCore.FurnitureCompany;
import com.sem2.SimCore.SimulationCore;

public class Fitting extends EmpFurnitureEvent{

    public Fitting(double time, EventSimulationCore simulationCore, Employee employee, Order order) {
        super(time, simulationCore, employee, order);
    }
    @Override
    public void execute() {
        getOrder().passedEvents += "Fitting,";
        if (getOrder().getID() == 1631598044) {
            System.out.println();
        }
        FurnitureCompany sim = (FurnitureCompany) getSimulationCore();
        getEmployee().setState(EmployeeState.IDLE);
        getOrder().setState(OrderState.FITTED);
        getOrder().getStation().setCurrentProcess(Process.NONE);
        Employee finishedEmployee = getEmployee();

        if (sim.isOrderWaitingForFitting()) {
            Order waitingOrder = sim.getOrderWaitingForFitting();
            if(waitingOrder.getState() != OrderState.ASSEMBLED && waitingOrder.getState() != OrderState.WAITING_FOR_FITTING) {
                throw new IllegalStateException("Order must be waiting for fitting when arriving -Fitting 1");
            }
            finishedEmployee.setState(EmployeeState.MOVING);
            
            MoveToStation move = new MoveToStation(getTime() + sim.getStationMoveTime(), sim, finishedEmployee, waitingOrder);
            sim.addEvent(move);
        } else if(sim.isOrderWaitingForVarnish()){
            Order waitingOrder = sim.getOrderWaitingForVarnish();
            finishedEmployee.setState(EmployeeState.MOVING);
            if (waitingOrder.getState() != OrderState.CUT && waitingOrder.getState() != OrderState.WAITING_FOR_VARNISH) {
                throw new IllegalStateException("Order must be waiting for varnish when arriving -Fitting 2");
            }
            MoveToStation move = new MoveToStation(getTime() + sim.getStationMoveTime(), sim, finishedEmployee, waitingOrder);
            sim.addEvent(move);
        } else{
            sim.freeEmployee(finishedEmployee);
        }
        FinalizeOrder finalizeOrder = new FinalizeOrder(getTime(), sim, null, getOrder());
        sim.addEvent(finalizeOrder);
    }
}
