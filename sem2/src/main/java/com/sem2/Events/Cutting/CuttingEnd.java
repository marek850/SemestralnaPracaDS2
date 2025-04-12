package com.sem2.Events.Cutting;

import com.sem2.Events.EmpFurnitureEvent;
import com.sem2.Events.Assembling.Fitting;
import com.sem2.Events.Moving.MoveToStation;
import com.sem2.Events.Moving.MoveToStorage;
import com.sem2.Events.Varnishing.VarnishingEnd;
import com.sem2.FurnitureCompany.AssemblyStation;
import com.sem2.FurnitureCompany.Employee;
import com.sem2.FurnitureCompany.Order;
import com.sem2.FurnitureCompany.Enums.EmployeeState;
import com.sem2.FurnitureCompany.Enums.OrderState;
import com.sem2.FurnitureCompany.Enums.Position;
import com.sem2.FurnitureCompany.Enums.Process;
import com.sem2.SimCore.EventSimulationCore;
import com.sem2.SimCore.FurnitureCompany;

public class CuttingEnd extends EmpFurnitureEvent{

    public CuttingEnd(double time, EventSimulationCore simulationCore, Employee employee, Order order) {
        super(time, simulationCore, employee, order);
    }
    @Override
    public void execute() {
        FurnitureCompany sim = (FurnitureCompany) getSimulationCore();
        Employee finishedEmployee = getEmployee();
        finishedEmployee.setState(EmployeeState.IDLE);
        getOrder().setState(OrderState.CUT);

        //Ak caka objednvka na rezanie tak rovno utilizujem zamestnanca skupiny A
        if (sim.isOrderWaitingForCutting()) {
            Order waitingOrder = sim.getOrderWaitingForCutting();
            sim.getWaitingOrders().addData(sim.getWaitingOrdersQueue().size(), getTime() - sim.getWaitingOrderQueueChangeTime());
            sim.setWaitingOrderQueueChangeTime(getTime());
            if (sim.isAvailableStation()) {
                waitingOrder.setStation(sim.getBestAssemblyStation());
            } else {
                AssemblyStation station = new AssemblyStation(sim.getLastStationId() + 1);
                sim.setLastStationId(station.getId());
                sim.addStation(station);
                waitingOrder.setStation(station);
            }
            waitingOrder.getStation().setCurrentProcess(Process.NONE);
            finishedEmployee.setState(EmployeeState.MOVING);

            MoveToStorage moveToStorage = new MoveToStorage(getTime() + sim.getStorageMoveTime(), sim, finishedEmployee, waitingOrder);
            sim.addEvent(moveToStorage);
        } else {
            sim.freeEmployee(finishedEmployee);
        }

        //Primarne spracujem objednavku cakajucu na montaz kovani
        if (sim.isCAvailable() && sim.isOrderWaitingForFitting()) {
            Order waitingOrder = sim.getOrderWaitingForFitting();
            waitingOrder.getStation().setCurrentProcess(Process.NONE);
            Employee freeEmployee = sim.getCAvailable();
            freeEmployee.setWorking(true, getTime());
            if (freeEmployee.getCurrentPosition() == Position.ASSEMBLY_STATION && freeEmployee.getStation() == waitingOrder.getStation()) {
                freeEmployee.setState(EmployeeState.FITTING);
                waitingOrder.setState(OrderState.BEING_FITTED);
                waitingOrder.getStation().setCurrentProcess(Process.FITTING);
                Fitting fitting = new Fitting(getTime() + sim.getFittingTime(), sim, freeEmployee, waitingOrder);
                sim.addEvent(fitting);
            } else if(freeEmployee.getCurrentPosition() == Position.ASSEMBLY_STATION && freeEmployee.getStation() != waitingOrder.getStation()){
                freeEmployee.setState(EmployeeState.MOVING);
                MoveToStation moveToStation = new MoveToStation(getTime() + sim.getStationMoveTime(), sim, freeEmployee, waitingOrder);
                sim.addEvent(moveToStation);
            } else {
                freeEmployee.setState(EmployeeState.MOVING);
                MoveToStorage moveToStorage = new MoveToStorage(getTime() + sim.getStorageMoveTime(), sim, freeEmployee, waitingOrder);
                sim.addEvent(moveToStorage);
            }
        }
       
        else if (sim.isCAvailable()){
            Employee freeEmployee = sim.getCAvailable();
            freeEmployee.setWorking(true, getTime());
            if (freeEmployee.getCurrentPosition() == Position.ASSEMBLY_STATION && freeEmployee.getStation() == getOrder().getStation()) {
                freeEmployee.setState(EmployeeState.VARNISHING);
                getOrder().setState(OrderState.BEING_VARNISHED);
                getOrder().getStation().setCurrentProcess(Process.VARNISHING);
                VarnishingEnd varnish = new VarnishingEnd(getTime() + sim.getVarnishingTime(getOrder()), sim, freeEmployee, getOrder());
                sim.addEvent(varnish);
            } else if(freeEmployee.getCurrentPosition() == Position.ASSEMBLY_STATION && freeEmployee.getStation() != getOrder().getStation()){
                freeEmployee.setState(EmployeeState.MOVING);
                
                MoveToStation moveToStation = new MoveToStation(getTime() + sim.getStationMoveTime(), sim, freeEmployee, getOrder());
                sim.addEvent(moveToStation);
            } else if (freeEmployee.getCurrentPosition() == Position.STORAGE) {
                freeEmployee.setState(EmployeeState.MOVING);
                MoveToStorage moveToStorage = new MoveToStorage(getTime() + sim.getStorageMoveTime(), sim, freeEmployee, getOrder());
                sim.addEvent(moveToStorage);
            }
        } else{
            getOrder().setState(OrderState.WAITING_FOR_VARNISH);
            getOrder().getStation().setCurrentProcess(Process.NONE);
            sim.addOrderForVarnish(getOrder());
        }
    }
}
