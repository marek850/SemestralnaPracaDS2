package com.sem2.Events.Cutting;

import com.sem2.Events.EmpFurnitureEvent;
import com.sem2.Events.Moving.MoveToStorage;
import com.sem2.Events.Varnishing.VarnishingStart;
import com.sem2.FurnitureCompany.AssemblyStation;
import com.sem2.FurnitureCompany.Employee;
import com.sem2.FurnitureCompany.Order;
import com.sem2.FurnitureCompany.Enums.EmployeeState;
import com.sem2.FurnitureCompany.Enums.FurnitureType;
import com.sem2.FurnitureCompany.Enums.OrderState;
import com.sem2.FurnitureCompany.Enums.Process;
import com.sem2.SimCore.EventSimulationCore;
import com.sem2.SimCore.FurnitureCompany;
import com.sem2.SimCore.SimulationCore;

public class CuttingEnd extends EmpFurnitureEvent{

    public CuttingEnd(double time, EventSimulationCore simulationCore, Employee employee, Order order) {
        super(time, simulationCore, employee, order);
    }
    @Override
    public void execute() {
        super.execute();
        FurnitureCompany sim = (FurnitureCompany) getSimulationCore();
        if (getOrder().getType() == FurnitureType.CHAIR) {
            sim.getChairCutting().addValue(getTime() - getOrder().getArrivalTime());
        }
        
        if (sim.isOrderWaitingForCutting()) {
            Order waitingOrder = sim.getOrderWaitingForCutting();
            waitingOrder.setState(OrderState.PREPARING_MATERIAL);
            if (sim.isAvailableStation()) {
                waitingOrder.setStation(sim.getBestAssemblyStation());
                waitingOrder.getStation().setCurrentProcess(Process.NONE);
            }else {
                waitingOrder.setStation(new AssemblyStation(sim.getLastStationId() + 1));
                sim.setLastStationId(waitingOrder.getStation().getId());
                waitingOrder.getStation().setCurrentProcess(Process.NONE);
            }
            
        } else{
            getEmployee().setState(EmployeeState.IDLE);
            sim.freeEmployee(getEmployee());
        }
        if (sim.isCAvailable()) {
            Employee handlingEmployee = sim.getCAvailable();
            VarnishingStart varnishingStart = new VarnishingStart(getTime() + sim.getVarnishingTime(getOrder()), sim, handlingEmployee, getOrder());
            sim.addEvent(varnishingStart);
        } else {
            getOrder().setState(OrderState.WAITING_FOR_VARNISH);
            sim.addOrderForVarnish(getOrder());
        }
        sim.refreshGUI();
    }
}
