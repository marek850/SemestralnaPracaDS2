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
        
        // Určujeme čas podľa timeFactor:
        double simulatedTimeToRealTime;
        if (timeFactor == Constants.MAX_SPEED) {
            // Maximálna rýchlosť - bez spomalenia, ihneď
            simulatedTimeToRealTime = 0.0;
        } else {
            // Ostatné režimy
            simulatedTimeToRealTime = 0.2 * timeFactor;
        }

        try {
            if (simulatedTimeToRealTime > 0) {
                // Vykonáme spomalenie v reálnom čase
                Thread.sleep((long)(simulatedTimeToRealTime * 1000)); // sleep je v milisekundách
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getSimulationCore().refreshGUI();
        getSimulationCore().setCurrentTime(getTime());
        // Pridáme nový SystemEvent s upraveným časom
        SystemEvent newEvent = new SystemEvent(getTime() + 0.2, getSimulationCore());
        getSimulationCore().addEvent(newEvent);
    }
    
    
}
