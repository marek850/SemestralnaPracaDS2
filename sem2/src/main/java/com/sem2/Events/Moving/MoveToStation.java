package com.sem2.Events.Moving;

import com.sem2.Events.EmpFurnitureEvent;
import com.sem2.Events.Assembling.AssemblyEnd;
import com.sem2.Events.Assembling.Fitting;
import com.sem2.Events.Varnishing.VarnishingEnd;
import com.sem2.FurnitureCompany.Employee;
import com.sem2.FurnitureCompany.Order;
import com.sem2.FurnitureCompany.Enums.EmployeeState;
import com.sem2.FurnitureCompany.Enums.OrderState;
import com.sem2.FurnitureCompany.Enums.Position;
import com.sem2.FurnitureCompany.Enums.Process;
import com.sem2.SimCore.EventSimulationCore;
import com.sem2.SimCore.FurnitureCompany;

public class MoveToStation extends EmpFurnitureEvent{

    public MoveToStation(double time, EventSimulationCore simulationCore, Employee employee, Order order) {
        super(time, simulationCore, employee, order);
    }
    @Override
    public void execute() {
        FurnitureCompany sim = (FurnitureCompany) getSimulationCore();
        getEmployee().setPosition(Position.ASSEMBLY_STATION);
        getEmployee().setStation(getOrder().getStation());
        getEmployee().setState(EmployeeState.IDLE);
        if (getOrder().getState() == OrderState.CUT || getOrder().getState() == OrderState.WAITING_FOR_VARNISH) {
            getEmployee().setState(EmployeeState.VARNISHING);
                getOrder().setState(OrderState.BEING_VARNISHED);
                getOrder().getStation().setCurrentProcess(Process.VARNISHING);
                VarnishingEnd varnishingEnd = new VarnishingEnd(getTime() + sim.getVarnishingTime(getOrder()), sim, getEmployee(), getOrder());
                sim.addEvent(varnishingEnd);
            
            
        }else if (getOrder().getState() == OrderState.VARNISHED || getOrder().getState() == OrderState.WAITING_FOR_ASSEMBLY) {
            getEmployee().setState(EmployeeState.ASSEMBLING);
            getOrder().setState(OrderState.BEING_ASSEMBLED);
            getOrder().getStation().setCurrentProcess(Process.ASSEMBLING);
            AssemblyEnd assemblyEnd = new AssemblyEnd(getTime() + sim.getAssembleTime(getOrder()), sim, getEmployee(), getOrder());
            sim.addEvent(assemblyEnd);
            
        } else if (getOrder().getState() == OrderState.ASSEMBLED || getOrder().getState() == OrderState.WAITING_FOR_FITTING) {
            getEmployee().setState(EmployeeState.FITTING);
            getOrder().setState(OrderState.BEING_FITTED);
            getOrder().getStation().setCurrentProcess(Process.FITTING);
            Fitting fitting = new Fitting(getTime() + sim.getFittingTime(), sim, getEmployee(), getOrder());
            sim.addEvent(fitting);
            
        } else{
            throw new IllegalStateException("Order must be in state CUT, VARNISHED, ASSEMBLED or WAITING_FOR_ASSEMBLY when arriving -MoveToStation 1" + getOrder().getState());
        } 
    }
    
}
