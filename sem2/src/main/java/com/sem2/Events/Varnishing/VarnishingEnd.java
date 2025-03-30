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
        super.execute();
        FurnitureCompany sim = (FurnitureCompany) getSimulationCore();
        if (sim.isBAvailable()) {
            Employee availableEmployee = sim.getBAvailable();
            AssemblyStart assemblyStart = new AssemblyStart(getTime(), sim, availableEmployee, getOrder());
            sim.addEvent(assemblyStart);
        } else{
            getOrder().setState(OrderState.WAITING_FOR_ASSEMBLY);
            getOrder().getStation().setCurrentProcess(Process.NONE);
            sim.addOrderForAssembly(getOrder());
        }
        if (sim.isOrderWaitingForFitting()) {
            Order order = sim.getOrderWaitingForFitting();
            getEmployee().setState(EmployeeState.MOVING);
            double moveTime = 0;
            switch (getEmployee().getCurrentPosition()) {
                case STORAGE:
                    moveTime = getTime() + sim.getStorageMoveTime();
                    getEmployee().setPosition(Position.ASSEMBLY_STATION);
                    break;
                case ASSEMBLY_STATION:
                    if (getEmployee().getStation() == order.getStation()) {
                        moveTime = getTime();
                    } else {
                        moveTime = getTime() + sim.getStationMoveTime();
                    }
                    break;
                default:
                    break;
            }
            order.getStation().setCurrentProcess(Process.NONE);
            MoveToStation moveToFitting = new MoveToStation(moveTime, sim, getEmployee(), order);
            sim.addEvent(moveToFitting);
        } else if(sim.isOrderWaitingForVarnish()){
            Order order = sim.getOrderWaitingForVarnish();
            VarnishingStart startVarnish = new VarnishingStart(getTime(), sim, getEmployee(), order);
            sim.addEvent(startVarnish);
        } 
    }
}
