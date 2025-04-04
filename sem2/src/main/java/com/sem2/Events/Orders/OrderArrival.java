package com.sem2.Events.Orders;

import com.sem2.Events.BasicFurnitureEvent;
import com.sem2.Events.Assembling.Fitting;
import com.sem2.Events.Cutting.PrepareMaterial;
import com.sem2.Events.Moving.MoveToStation;
import com.sem2.Events.Moving.MoveToStorage;
import com.sem2.Events.Varnishing.VarnishingEnd;
import com.sem2.FurnitureCompany.AssemblyStation;
import com.sem2.FurnitureCompany.Employee;
import com.sem2.FurnitureCompany.Order;
import com.sem2.FurnitureCompany.Enums.EmployeeState;
import com.sem2.FurnitureCompany.Enums.EmployeeType;
import com.sem2.FurnitureCompany.Enums.FurnitureType;
import com.sem2.FurnitureCompany.Enums.OrderState;
import com.sem2.FurnitureCompany.Enums.Position;
import com.sem2.FurnitureCompany.Enums.Process;
import com.sem2.SimCore.EventSimulationCore;
import com.sem2.SimCore.FurnitureCompany;

public class OrderArrival extends BasicFurnitureEvent{

    public OrderArrival(double time, EventSimulationCore simulationCore) {
        super(time, simulationCore);
       
    }

    @Override
    public void execute() {
        FurnitureCompany sim = (FurnitureCompany) getSimulationCore();
        Order order = createOrder(sim);
        sim.addActiveOrder(order);
        order.setArrivalTime(getTime());
        order.passedEvents += "OrderArrival,";
        if (order.getID() == 1631598044) {
            System.out.println();
        }
        if (sim.isAAvailable()) {
            Employee handlingEmployee = sim.getAAvailable();
            if (handlingEmployee.getState() != EmployeeState.IDLE) {
                throw new IllegalStateException("Employee A must be idle when he is available-OrderArrival 1");
            }
            if (sim.isAvailableStation()) {
                AssemblyStation bestAssemblyStation = sim.getBestAssemblyStation();
                if (bestAssemblyStation.getCurrentProcess() != Process.NONE) {
                    throw new IllegalStateException("Assembly station must be idle when it is available-OrderArrival 2");
                    
                }
                order.setStation(bestAssemblyStation);
            } else {
                AssemblyStation station = new AssemblyStation(sim.getLastStationId() + 1);
                station.setCurrentProcess(Process.NONE);
                sim.setLastStationId(station.getId());
                sim.addStation(station);
                order.setStation(station);
            }
            handlingEmployee.setWorking(true, getTime());
            
            switch (handlingEmployee.getCurrentPosition()) {
                //Ak je uz v sklade nemusi sa presuvat
                case STORAGE:
                if (handlingEmployee.getState() != EmployeeState.IDLE) {
                    throw new IllegalStateException("Employee A must be idle when he is in storage-OrderArrival 3");
                    
                }
                    order.setState(OrderState.PREPARING_MATERIAL);
                    handlingEmployee.setState(EmployeeState.PREPARING_MATERIAL);
                    sim.addEvent(new PrepareMaterial(getTime() + sim.getMatPrepTime(), sim, handlingEmployee, order));
                    break;
                //Ak nie je v sklade musi sa presunut
                case ASSEMBLY_STATION:
                    if (handlingEmployee.getState() != EmployeeState.IDLE || handlingEmployee.getStation() == null) {
                        throw new IllegalStateException("Employee A must be idle when he is in assembly station and station cannot be null-OrderArrival 4");
                        
                    }
                    handlingEmployee.setState(EmployeeState.MOVING);
                    if (order.getState() != OrderState.PENDING) {
                        throw new IllegalStateException("Order must be preparing material when employee A is in assembly station-OrderArrival 5");
                        
                    }
                    sim.addEvent(new MoveToStorage(getTime() + sim.getStorageMoveTime(), sim, handlingEmployee, order));
                    break;
                default:
                    break;
            }
            
            
        } else {
            if (order.getState() != OrderState.PENDING) {
                throw new IllegalStateException("Order must be in PENDING state when employee A is not available-OrderArrival 5");
            }
            order.passedEvents+= "cuttingQueue,";
            sim.getWaitingOrders().addData(sim.getWaitingOrdersQueue().size(), getTime() - sim.getWaitingOrderQueueChangeTime());
            sim.setWaitingOrderQueueChangeTime(getTime());
            sim.addOrderWaitingForCut(order);
        }
        sim.addEvent(new OrderArrival(getTime() + sim.getOrderArrivalTime() /* 18000 */, sim));
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
        Order newOrder = new Order(sim.orderIDGen.nextInt(), orderType);
        newOrder.setState(OrderState.PENDING);
        return newOrder;
    }
}
