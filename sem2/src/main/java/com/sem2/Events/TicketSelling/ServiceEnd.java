package com.sem2.Events.TicketSelling;

import com.sem2.FurnitureCompany.Customer;
import com.sem2.SimCore.EventSimulationCore;
import com.sem2.SimCore.TicketsSim;

public class ServiceEnd extends StationEvent {
    public ServiceEnd(double time, EventSimulationCore simulationCore) {
        super(time, simulationCore);
    }
    public void execute() {
        TicketsSim sim = (TicketsSim) getSimulationCore();
        //System.out.println("ServiceEnd time: " + this.getTime());
        double time = this.getTime();
        sim.setServerBusy(false);
        if(!sim.isCustomerQueueEmpty()){
            Customer customer = sim.getCustomer();
            ServiceStart serviceStart = new ServiceStart(time, this.getSimulationCore());
            serviceStart.setCustomer(customer);
            sim.addEvent(serviceStart);

            sim.getTicketsStatistics().addData(sim.getQueueLength(), time - sim.getLastQueueChangeTime());
            sim.setQueueLength(sim.getQueueLength()-1);
            sim.setLastQueueChangeTime(this.getTime());
        } 
        System.out.printf("Average time in line: %.15f%n", sim.getStatistic().getAverage());
        System.out.printf("Average num of people in line: %.15f%n", sim.getTicketsStatistics().getWeightedAverage());
        sim.setCurrentTime(time);
    }
    
}
