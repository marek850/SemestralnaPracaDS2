package com.sem2.Events.TicketSelling;

import com.sem2.SimCore.EventSimulationCore;
import com.sem2.SimCore.SimulationCore;
import com.sem2.SimCore.TicketsSim;

public class ServiceStart extends StationEvent {
    public ServiceStart(double time, EventSimulationCore simulationCore) {
        super(time, simulationCore);
    }
    public void execute() {
        TicketsSim sim = (TicketsSim) getSimulationCore();
        double time = this.getTime();
        sim.setServerBusy(true);
        //System.out.println("ServiceStart time: " + time);
        
        double generatedTime = sim.getServiceTime();
        ServiceEnd serviceEnd = new ServiceEnd(time + generatedTime, this.getSimulationCore());
        serviceEnd.setCustomer(this.getCustomer());
        sim.addEvent(serviceEnd);
        sim.setCurrentTime(this.getTime());
        sim.getStatistic().addValue(time - this.getCustomer().getArrivalTime());

        //System.out.println("Customer service time: "  + (time + generatedTime));
        
    }
    
}
