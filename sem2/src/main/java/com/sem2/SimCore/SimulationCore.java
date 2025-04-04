package com.sem2.SimCore;

import java.util.ArrayList;

import com.sem2.UserInterface.UserInterface;

public abstract class SimulationCore {
    
    private boolean stop = false;
    protected int numberOfReplications = 0;
    public boolean isStop() {
        return stop;
    }
    public void setStop(boolean stop) {
        this.stop = stop;
    }
    public final void runSimulation(int numberOfReplications) {
        this.numberOfReplications = numberOfReplications;
        beforeSimulation();
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
    protected void beforeSimulation() {
    }
    protected void afterSimRun() {
    }
    protected abstract void executeSimRun();
}