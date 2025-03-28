package com.sem2.Events.Cutting;

import com.sem2.Events.EmpFurnitureEvent;
import com.sem2.Events.Moving.MoveToStorage;
import com.sem2.Events.Varnishing.VarnishingStart;
import com.sem2.FurnitureCompany.Employee;
import com.sem2.FurnitureCompany.Order;
import com.sem2.FurnitureCompany.Enums.EmployeeState;
import com.sem2.FurnitureCompany.Enums.OrderState;
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
        sim.freeEmployee(getEmployee());
        getOrder().setState(OrderState.WAITING_FOR_VARNISH);
        sim.addOrderForVarnish(getOrder());
        if (sim.isCAvailable() && sim.isOrderWaitingForVarnish()) {
            Employee freeEmployee = sim.getCAvailable();
            setOrder(sim.getOrderWaitingForVarnish());
            VarnishingStart varnishingStart = new VarnishingStart(getTime(), sim, freeEmployee, getOrder());
            sim.addEvent(varnishingStart);
            getOrder().setState(OrderState.BEING_VARNISHED);
        } 

        if(sim.isAAvailable() && sim.isOrderWaitingForCutting()){
            Order order = sim.getOrderWaitingForCutting();
            Employee freeEmployee = sim.getAAvailable();
            freeEmployee.setState(EmployeeState.PREPARING_MATERIAL);
            order.setState(OrderState.PREPARING_MATERIAL);
            MoveToStorage moveToStorageTask = new MoveToStorage(getTime(), sim, freeEmployee, order);
            sim.addEvent(moveToStorageTask);
        }
        sim.refreshGUI();
    }
}
