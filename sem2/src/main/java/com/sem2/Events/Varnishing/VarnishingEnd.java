package com.sem2.Events.Varnishing;

import com.sem2.Events.EmpFurnitureEvent;
import com.sem2.Events.Assembling.AssemblyStart;
import com.sem2.FurnitureCompany.Employee;
import com.sem2.FurnitureCompany.Order;
import com.sem2.FurnitureCompany.Enums.EmployeeState;
import com.sem2.FurnitureCompany.Enums.OrderState;
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
        sim.freeEmployee(getEmployee());
        getOrder().setState(OrderState.WAITING_FOR_ASSEMBLY);
        sim.addOrderForAssembly(getOrder());
        if (sim.isBAvailable() && sim.isOrderWaitingForAssembly()) {
            Employee freeEmployee = sim.getBAvailable();
            AssemblyStart assemblyStart = new AssemblyStart(getTime(), sim, freeEmployee, getOrder());
            sim.addEvent(assemblyStart);
            getOrder().setState(OrderState.BEING_VARNISHED);
        } 
        sim.refreshGUI();
    }
}
