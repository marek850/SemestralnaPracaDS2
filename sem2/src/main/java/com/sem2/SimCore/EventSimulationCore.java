package com.sem2.SimCore;
import java.util.PriorityQueue;

import com.sem2.Events.Event;
public abstract class EventSimulationCore {
    private PriorityQueue<Event> eventCalendar;
    private Integer currentSimulationTime;
    
    public void setStop(boolean stop) {
    }
    public final void runSimulation(int numberOfReplications) {
        
        for (int i = 0; i < numberOfReplications; i++) {
            if (this.stop) {
                break;
            }
            beforeSimRun();
            executeSimRun();
            afterSimRun();
        }
        afterSimulation();
        
    }
    protected void afterSimulation(){
    }
    protected void beforeSimRun() {
    }

    protected void afterSimRun() {
    }
    protected abstract void executeSimRun();
}