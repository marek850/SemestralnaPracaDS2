package com.sem2.SimCore;
import java.util.ArrayList;
import java.util.PriorityQueue;

import com.sem2.Constants.Constants;
import com.sem2.Events.Event;
import com.sem2.Events.SystemEvent;
import com.sem2.Statistics.Statistic;
import com.sem2.Statistics.TicketsStatistics;
import com.sem2.UserInterface.UserInterface;



public class EventSimulationCore extends SimulationCore {
    protected PriorityQueue<Event> eventCalendar;
    private double currentTime;
    private double maxSimulationTime;
    private boolean stop;
    private boolean pause;
    private ArrayList<UserInterface> userInterfaces = new ArrayList<>();
    private double timeFactor = 0.0;
   
    EventSimulationCore(double maxSimulationTime) {
        eventCalendar = new PriorityQueue<Event>();
        currentTime = 0;
        this.maxSimulationTime = maxSimulationTime;
        stop = false;
        pause = false;
        
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
    public void setTimeFactor(double timeFactor) {
        this.timeFactor = timeFactor;
    }

    public double getTimeFactor() {
        return timeFactor;
    }
    
    public void setRealTimeMode() {
        setTimeFactor(Constants.REAL_TIME);
    }

    public void setFastTimeMode() {
        setTimeFactor(Constants.FAST_TIME);
    }

    public void setMaxSpeedMode() {
        setTimeFactor(Constants.MAX_SPEED);
    }
    public PriorityQueue<Event> getEventCalendar() {
        return eventCalendar;
    }
    public void addEvent(Event event) {
        eventCalendar.add(event);
    }
    public void setEventCalendar(PriorityQueue<Event> eventCalendar) {
        this.eventCalendar = eventCalendar;
    }

    public double getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(double currentTime) {
        this.currentTime = currentTime;
    }

    public double getMaxSimulationTime() {
        return maxSimulationTime;
    }

    public void setMaxSimulationTime(double maxSimulationTime) {
        this.maxSimulationTime = maxSimulationTime;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }
    @Override
    protected void beforeSimRun() {
       
        super.beforeSimRun();
        //eventCalendar.add(new SystemEvent(currentTime, this));
    }
    @Override
    protected void executeSimRun() {
        while (eventCalendar.size() > 0 && currentTime < maxSimulationTime && !stop) {
            while (pause) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Event event = eventCalendar.poll();
            if (event.getTime() < maxSimulationTime) {
                if (event.getTime() >= currentTime - Constants.epsilon) {
                    event.execute();
                } else {
                    throw new RuntimeException("Event time is less than current time: " + event.getTime() + " < " + currentTime);
                }
                
            }
        }

    }
    @Override
    protected void afterSimRun() {
        super.afterSimRun();
        eventCalendar.clear();
        currentTime = 0;
    }
}
