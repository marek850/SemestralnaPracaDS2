package com.sem2.Events.TicketSelling;

import com.sem2.Events.Event;
import com.sem2.FurnitureCompany.Customer;
import com.sem2.SimCore.EventSimulationCore;
import com.sem2.SimCore.TicketsSim;

public class StationEvent extends Event {
    private Customer customer;
    public StationEvent(double time, EventSimulationCore simulationCore) {
        super(time, simulationCore);
    }
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    public void execute() {
        TicketsSim sim = (TicketsSim) getSimulationCore();
        sim.setCurrentTime(getTime());
    }
    @Override
    public int compareTo(Event o) {
        return Double.compare(this.getTime(), o.getTime());
    }
    
}
