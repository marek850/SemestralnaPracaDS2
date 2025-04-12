package com.sem2.Events;

import com.sem2.SimCore.EventSimulationCore;

public class SystemEvent extends Event{

    public SystemEvent(double time, EventSimulationCore simulationCore) {
        super(time, simulationCore);
    }

    @Override
    public int compareTo(Event o) {
        return Double.compare(this.getTime(), o.getTime());
    }

    @Override
    public void execute() {
        double timeFactor = getSimulationCore().getTimeFactor();
        if (timeFactor > 0) {
            try {
                Thread.sleep((long)(1000/timeFactor)); 
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            getSimulationCore().refreshGUI();
            SystemEvent newEvent = new SystemEvent(getTime() + 1, getSimulationCore());
            getSimulationCore().addEvent(newEvent);
        }
    }
    
    
}
