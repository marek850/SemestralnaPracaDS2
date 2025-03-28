package com.sem2.SimCore;

import java.util.ArrayList;

import com.sem2.UserInterface.UserInterface;

public abstract class SimulationCore {
    private ArrayList<UserInterface> userInterfaces = new ArrayList<>();
    private boolean stop = false;
    public void setStop(boolean stop) {
        this.stop = stop;
    }
    public final void runSimulation(int numberOfReplications) {
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
    public void addUserInterface(UserInterface userInterface) {
        userInterfaces.add(userInterface);
    }
    public void removeUserInterface(UserInterface userInterface) {
        userInterfaces.remove(userInterface);
    }
    public void refreshGUI() {
        for (UserInterface userInterface : userInterfaces) {
            userInterface.refresh(this);
        }
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