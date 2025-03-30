package com.sem2.Events;

import com.sem2.Constants.Constants;
import com.sem2.SimCore.EventSimulationCore;
import com.sem2.SimCore.SimulationCore;

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
        
        double simulatedTimeToRealTime;
        if (Math.abs(timeFactor - Constants.MAX_SPEED) < Constants.epsilon) {
            simulatedTimeToRealTime = 0.0;
        } else {
            simulatedTimeToRealTime = 0.5 * timeFactor;
        }
        try {
            if (simulatedTimeToRealTime > 0) {
                Thread.sleep((long)(simulatedTimeToRealTime * 1000)); 
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getSimulationCore().refreshGUI();
        SystemEvent newEvent = new SystemEvent(getTime() + 0.5, getSimulationCore());
        getSimulationCore().addEvent(newEvent);
    }
    
    
}
