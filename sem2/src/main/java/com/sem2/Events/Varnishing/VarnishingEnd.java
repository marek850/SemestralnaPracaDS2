package com.sem2.Events.Varnishing;

import com.sem2.Events.EmpFurnitureEvent;
import com.sem2.Events.Assembling.AssemblyStart;
import com.sem2.Events.Assembling.Fitting;
import com.sem2.Events.Moving.MoveToStation;
import com.sem2.FurnitureCompany.Employee;
import com.sem2.FurnitureCompany.Order;
import com.sem2.FurnitureCompany.Enums.EmployeeState;
import com.sem2.FurnitureCompany.Enums.OrderState;
import com.sem2.FurnitureCompany.Enums.Position;
import com.sem2.SimCore.EventSimulationCore;
import com.sem2.SimCore.FurnitureCompany;
import com.sem2.SimCore.SimulationCore;

public class VarnishingEnd extends EmpFurnitureEvent{

    public VarnishingEnd(double time, EventSimulationCore simulationCore, Employee employee, Order order) {
        super(time, simulationCore, employee, order);
    }
    @Override
    public void execute() {
        super.execute();
        System.out.println("Varnishing end");
        FurnitureCompany sim = (FurnitureCompany) getSimulationCore();
        //uvolnim pracovnika a zaradim objednavku do fronty na skladanie
        sim.freeEmployee(getEmployee());
        getOrder().setState(OrderState.WAITING_FOR_ASSEMBLY);
        sim.addOrderForAssembly(getOrder());

        //naplanujem skladanie objedmnavky ak je nejaky zamestnanec skupiny B volny        
        if (sim.isBAvailable()) {
            AssemblyStart assemblyStart = new AssemblyStart(getTime(), sim, sim.getBAvailable(), getOrder());
            sim.addEvent(assemblyStart);
        } 
        sim.refreshGUI();

        if (sim.isCAvailable()) {
            Employee employee = sim.getCAvailable();
            //prioritne naplanujem montaz kovani ak je nejaky zamestnanec skupiny C volny
            if (sim.isOrderWaitingForFitting()) {
                Order order = sim.getOrderWaitingForFitting();
                double moveTime = 0;
                switch (employee.getCurrentPosition()) {
                    case STORAGE:
                        moveTime = getTime() + sim.getStorageMoveTime();
                        employee.setPosition(Position.ASSEMBLY_STATION);
                        break;
                    case ASSEMBLY_STATION:
                        moveTime = getTime() + sim.getStationMoveTime();
                        break;
                    default:
                        break;
                }
                employee.setState(EmployeeState.MOVING);
                MoveToStation moveToStation = new MoveToStation(moveTime, sim, employee, order);
                sim.addEvent(moveToStation);
            } else {
                // ak necaka objednavka na montaz kovani tak naplanujem montaz
                AssemblyStart assemblyStart = new AssemblyStart(getTime(), sim, employee, sim.getOrderWaitingForAssembly());
                sim.addEvent(assemblyStart);
            }
        }
    }
}
