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
        Order order = createOrder(sim);
        sim.addActiveOrder(order);
        order.setArrivalTime(getTime());
        if (sim.isAvailableStation()) {
            order.setStation(sim.getBestAssemblyStation());
        } else {
            AssemblyStation station = new AssemblyStation(sim.getLastStationId() + 1);
            sim.setLastStationId(station.getId());
            //station.setCurrentProcess(Process.CUTTING);
            sim.addStation(station);
            order.setStation(station);
            //order.setState(OrderState.BEING_CUT);
        }
        if (sim.isAAvailable()) {
            Employee handlingEmployee = sim.getAAvailable();
            order.getStation().setCurrentProcess(Process.CUTTING);
            order.setState(OrderState.BEING_CUT);
            
            sim.addEvent(new MoveToStorage(getTime(), sim, handlingEmployee, order));
        } else {
            sim.addOrder(order);
            order.setState(OrderState.PENDING);
        }
        sim.addEvent(new OrderArrival(getTime() + sim.getOrderArrivalTime(), sim));
    }

    private Order createOrder(FurnitureCompany sim) {
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
