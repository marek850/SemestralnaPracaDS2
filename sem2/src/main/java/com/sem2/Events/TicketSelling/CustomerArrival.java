package com.sem2.Events.TicketSelling;

import com.sem2.FurnitureCompany.Customer;
import com.sem2.SimCore.EventSimulationCore;
import com.sem2.SimCore.TicketsSim;

public class CustomerArrival extends StationEvent {
    public CustomerArrival(double time, EventSimulationCore simulationCore) {
        super(time, simulationCore);
    }
    public void execute() {
        TicketsSim sim = (TicketsSim) getSimulationCore();
        //System.out.println("CustomerArrival time: " + this.getTime());
        double time = this.getTime();
        this.setCustomer(new Customer(time));
        if (sim.isServerBusy()) {
            sim.addCustomerToQueue(this.getCustomer());

            sim.getTicketsStatistics().addData(sim.getQueueLength(), time - sim.getLastQueueChangeTime());

            sim.setQueueLength(sim.getQueueLength()+1);
            sim.setLastQueueChangeTime(this.getTime());

        } else {
            ServiceStart serviceStart = new ServiceStart(time, this.getSimulationCore());
            serviceStart.setCustomer(this.getCustomer());
            sim.addEvent(serviceStart);
        }
        sim.setCurrentTime(time);
        CustomerArrival customerArrival = new CustomerArrival(time + sim.getArrivalTime(), this.getSimulationCore());
        sim.addEvent(customerArrival);
        
    }    
}
