package com.sem2.Events.Orders;

import java.io.FileWriter;
import java.io.IOException;

import com.sem2.Events.BasicFurnitureEvent;
import com.sem2.Events.EmpFurnitureEvent;
import com.sem2.FurnitureCompany.Employee;
import com.sem2.FurnitureCompany.Order;
import com.sem2.FurnitureCompany.Enums.OrderState;
import com.sem2.FurnitureCompany.Enums.Process;
import com.sem2.SimCore.EventSimulationCore;
import com.sem2.SimCore.FurnitureCompany;
import com.sem2.SimCore.SimulationCore;

public class FinalizeOrder extends EmpFurnitureEvent{

    public FinalizeOrder(double time, EventSimulationCore simulationCore, Employee employee, Order order) {
        super(time, simulationCore, employee, order);
    }

    @Override
    public void execute() {
        getOrder().passedEvents += "FinalizeOrder";
        FurnitureCompany sim = (FurnitureCompany) getSimulationCore();
        
        /* try (FileWriter writer = new FileWriter("order_log.txt", true)) {
            writer.write(getOrder().passedEvents + "\n");
            writer.write("Objednavka typu " + getOrder().getType() + " cas spracovania: " + (getTime() - getOrder().getArrivalTime()) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        } */
        if (getOrder().getID() == 381) {
            System.out.println();
        }
        getOrder().setState(OrderState.FINISHED);
        getOrder().getStation().setCurrentProcess(Process.NONE);
        /* if (getTime() - getOrder().getArrivalTime() > 600000) {
            System.out.println(getOrder().getID());
            throw new IllegalStateException("Order processing time is too long- FinalizeOrder 1");
        } */
        //System.out.println("Objednavka poradie: "  + getOrder().getID()+ " bola uspesne spracovana. Cas spracovania: " + (getTime() - getOrder().getArrivalTime())/3600);
        sim.orderTracker++;
        sim.getOrderFinishTime().addValue(getTime() - getOrder().getArrivalTime());
        sim.addFreeStation(getOrder().getStation());
        sim.removeActiveOrder(getOrder());
    }
}
