package com.sem2.Events.Cutting;

import com.sem2.Events.EmpFurnitureEvent;
import com.sem2.Events.Moving.MoveToStorage;
import com.sem2.Events.Varnishing.VarnishingStart;
import com.sem2.FurnitureCompany.AssemblyStation;
import com.sem2.FurnitureCompany.Employee;
import com.sem2.FurnitureCompany.Order;
import com.sem2.FurnitureCompany.Enums.EmployeeState;
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
        System.out.println("Cutting end");
        FurnitureCompany sim = (FurnitureCompany) getSimulationCore();
        sim.freeEmployee(getEmployee());
        getOrder().setState(OrderState.WAITING_FOR_VARNISH);
        sim.addOrderForVarnish(getOrder());
        if (sim.isCAvailable()) {
            VarnishingStart varnishingStart = new VarnishingStart(getTime(), sim, sim.getCAvailable(), sim.getOrderWaitingForVarnish());
            sim.addEvent(varnishingStart);
        } 

        if(sim.isAAvailable() && sim.isOrderWaitingForCutting()){
            Order order = sim.getOrderWaitingForCutting();
            if (sim.isAvailableStation()) {
                order.setStation(sim.getBestAssemblyStation());
            } else {
                AssemblyStation station = new AssemblyStation(sim.getLastStationId() + 1);
                sim.setLastStationId(station.getId());
                station.setCurrentProcess(Process.CUTTING);
                sim.addStation(station);
                order.setStation(station);
            }
            MoveToStorage moveToStorageTask = new MoveToStorage(getTime(), sim, sim.getAAvailable(), order);
            sim.addEvent(moveToStorageTask);
        }
        sim.refreshGUI();
    }
}
