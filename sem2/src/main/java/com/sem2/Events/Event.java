package com.sem2.Events;

import com.sem2.SimCore.EventSimulationCore;

public abstract class Event implements Comparable<Event> {
    private double time;
    private EventSimulationCore simulationCore;

    public Event(double time, EventSimulationCore simulationCore) {
        this.simulationCore = simulationCore;
        this.time = time;
    }
    public double getTime() {
        return time;
    }
    abstract public void execute();
    public EventSimulationCore getSimulationCore() {
        return simulationCore;
    }
}
