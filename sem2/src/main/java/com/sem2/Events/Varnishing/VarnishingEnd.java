package com.sem2.Events.Varnishing;

import com.sem2.Events.EmpFurnitureEvent;
import com.sem2.Events.Assembling.AssemblyEnd;
import com.sem2.Events.Assembling.Fitting;
import com.sem2.Events.Moving.MoveToStation;
import com.sem2.Events.Moving.MoveToStorage;
import com.sem2.FurnitureCompany.Employee;
import com.sem2.FurnitureCompany.Order;
import com.sem2.FurnitureCompany.Enums.EmployeeState;
import com.sem2.FurnitureCompany.Enums.EmployeeType;
import com.sem2.FurnitureCompany.Enums.OrderState;
import com.sem2.FurnitureCompany.Enums.Position;
import com.sem2.FurnitureCompany.Enums.Process;
import com.sem2.SimCore.EventSimulationCore;
import com.sem2.SimCore.FurnitureCompany;
import com.sem2.SimCore.SimulationCore;

public class VarnishingEnd extends EmpFurnitureEvent{

    public VarnishingEnd(double time, EventSimulationCore simulationCore, Employee employee, Order order) {
        super(time, simulationCore, employee, order);
    }
    @Override
    public void execute() {
        FurnitureCompany sim = (FurnitureCompany) getSimulationCore();
        if (getOrder().getID() == 1631598044) {
            System.out.println();
        }
        getOrder().passedEvents += "VarnishEnd,";
        if (getEmployee().getState() != EmployeeState.VARNISHING) {
            throw new IllegalStateException("Employee must be varnishing when arriving -VarnishingEnd 1");
            
        }
        if (getOrder().getState() != OrderState.BEING_VARNISHED) {
            throw new IllegalStateException("Order must be varnishing when arriving -VarnishingEnd 2");
            
        }

        getOrder().setState(OrderState.VARNISHED);
        getEmployee().setState(EmployeeState.IDLE);
        getOrder().getStation().setCurrentProcess(Process.NONE);
        Employee finishedEmployee = getEmployee();

        if (sim.isOrderWaitingForFitting()) {
            Order waitingOrder = sim.getOrderWaitingForFitting();
            if(waitingOrder.getState() != OrderState.ASSEMBLED && waitingOrder.getState() != OrderState.WAITING_FOR_FITTING) {
                throw new IllegalStateException("Order must be waiting for fitting when arriving -VarnishingEnd 3");
                
            }
            finishedEmployee.setState(EmployeeState.MOVING);
            MoveToStation moveToStation = new MoveToStation(getTime()+sim.getStationMoveTime(), sim, finishedEmployee, waitingOrder);
            sim.addEvent(moveToStation);
        } 
        else if (sim.isOrderWaitingForVarnish()){
            Order varnishOrder = sim.getOrderWaitingForVarnish();
            if (varnishOrder.getID() == 1631598044) {
                System.out.println();
            }
            if (varnishOrder.getState() != OrderState.CUT && varnishOrder.getState() != OrderState.WAITING_FOR_VARNISH) {
                throw new IllegalStateException("Order must be waiting for varnish when arriving -VarnishingEnd 4");
                
            }
            finishedEmployee.setState(EmployeeState.MOVING);
            //System.out.println("idem odtialto");
            MoveToStation moveToStation = new MoveToStation(getTime()+sim.getStationMoveTime(), sim, finishedEmployee, varnishOrder);
            sim.addEvent(moveToStation);
        } else{
            sim.freeEmployee(finishedEmployee);
        }

        if (sim.isBAvailable()) {
            Employee freeEmployee = sim.getBAvailable();
            if (freeEmployee.getState() != EmployeeState.IDLE) {
                throw new IllegalStateException("Employee B must be idle when he is available-VarnishingEnd 5");
                
            }
            if (freeEmployee.getType() != EmployeeType.B) {
                throw new IllegalStateException("Employee B must be type B when he is available-VarnishingEnd 6");
                
            }
            freeEmployee.setWorking(true, getTime());
            if (freeEmployee.getCurrentPosition() == Position.ASSEMBLY_STATION && freeEmployee.getStation() == getOrder().getStation()) {
                freeEmployee.setState(EmployeeState.ASSEMBLING);
                getOrder().setState(OrderState.BEING_ASSEMBLED);
                getOrder().getStation().setCurrentProcess(Process.ASSEMBLING);
                AssemblyEnd assemblyEnd = new AssemblyEnd(getTime() + sim.getAssembleTime(getOrder()), sim, freeEmployee, getOrder());
                sim.addEvent(assemblyEnd);
            } else if(freeEmployee.getCurrentPosition() == Position.ASSEMBLY_STATION && freeEmployee.getStation() != getOrder().getStation()){
                freeEmployee.setState(EmployeeState.MOVING);
                if(getOrder().getState() != OrderState.VARNISHED && getOrder().getState() != OrderState.WAITING_FOR_ASSEMBLY) {
                    throw new IllegalStateException("Order must be varnished here-VarnishingEnd 7");
                    
                }
                MoveToStation moveToStation = new MoveToStation(getTime() + sim.getStationMoveTime(), sim, freeEmployee, getOrder());
                sim.addEvent(moveToStation);
            } else if (freeEmployee.getCurrentPosition() == Position.STORAGE) {
                freeEmployee.setState(EmployeeState.MOVING);
                if(getOrder().getState() != OrderState.VARNISHED && getOrder().getState() != OrderState.WAITING_FOR_ASSEMBLY) {
                    throw new IllegalStateException("Order must be varnished here-VarnishingEnd 7");
                }
                MoveToStorage moveToStorage = new MoveToStorage(getTime() + sim.getStorageMoveTime(), sim, freeEmployee, getOrder());
                sim.addEvent(moveToStorage);
            } else{
                throw new IllegalStateException("Employee B must be in assembly station or storage when he is available-VarnishingEnd 8");
            }
        }
        else{
            getOrder().setState(OrderState.WAITING_FOR_ASSEMBLY);
            getOrder().getStation().setCurrentProcess(Process.NONE);
            getOrder().passedEvents+= "assemblyQueue,";
            sim.addOrderForAssembly(getOrder());
        }
    }
}
