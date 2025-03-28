package com.sem2.Events.Orders;

import com.sem2.Events.BasicFurnitureEvent;
import com.sem2.Events.Moving.MoveToStorage;
import com.sem2.Events.TicketSelling.CustomerArrival;
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

public class OrderArrival extends BasicFurnitureEvent{

    public OrderArrival(double time, EventSimulationCore simulationCore) {
        super(time, simulationCore);
       
    }

    @Override
    public void execute() {
        super.execute();
        FurnitureCompany sim = (FurnitureCompany) getSimulationCore();
        
        Order order = creatOrder(sim);
        order.setState(OrderState.PENDING);
        order.setArrivalTime(getTime());
        sim.addActiveOrder(order);
        if (sim.isAvailableStation()) {
            order.setStationID(sim.getBestAssemblyStation());
        } else {
            AssemblyStation station = new AssemblyStation(sim.getLastStationId() + 1);
            sim.setLastStationId(station.getId());
            station.setCurrentProcess(Process.CUTTING);
            sim.addStation(station);
            order.setStationID(station);
        }
        if (sim.isAAvailable()) {
            Employee availableEmployee = sim.getAAvailable();
            availableEmployee.setState(EmployeeState.MOVING);
            MoveToStorage moveToStorage = new MoveToStorage(getTime(), sim, availableEmployee, order);
            sim.addEvent(moveToStorage);
        } else {
            sim.addOrder(order);
        }

        OrderArrival orderArrival = new OrderArrival(getTime() + sim.getOrderArrivalTime(), this.getSimulationCore());
        sim.addEvent(orderArrival);
        sim.refreshGUI();
        sim.setCurrentTime(getTime());
    }

    private Order creatOrder(FurnitureCompany sim) {
        int orderTypeDecider = sim.getOrderType();
        FurnitureType orderType = null;
        if (orderTypeDecider >= 0 && orderTypeDecider < 50) {
            orderType = FurnitureType.TABLE;
        } else if (orderTypeDecider < 65) {
            orderType = FurnitureType.CHAIR;
        } else {
            orderType = FurnitureType.WARDROBE;
            
        }
        return new Order(orderType);
    }
}
