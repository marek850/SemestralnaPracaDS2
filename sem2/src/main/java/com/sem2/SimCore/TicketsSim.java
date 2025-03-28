package com.sem2.SimCore;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;

import com.sem2.Events.Event;
import com.sem2.Events.TicketSelling.CustomerArrival;
import com.sem2.FurnitureCompany.Customer;
import com.sem2.Generators.ExponentialGenerator;
import com.sem2.Statistics.Statistic;
import com.sem2.Statistics.TicketsStatistics;

public class TicketsSim extends EventSimulationCore{
    private ArrayList<Customer> customers = new ArrayList<Customer>();
    private Random seedGenerator = new Random();
    private boolean serverBusy = false;
    private ExponentialGenerator arrivalGenerator;
    private ExponentialGenerator serviceGenerator;
    private double lastQueueChangeTime;
    private int queueLength;
    private Statistic statistic;
    private Statistic runStatistic;
    private TicketsStatistics ticketsStatistics;
    private TicketsStatistics runTicketsStatistics;
    public TicketsSim(double maxSimulationTime) {
        super(maxSimulationTime);
        arrivalGenerator = new ExponentialGenerator(seedGenerator, 5);
        serviceGenerator = new ExponentialGenerator(seedGenerator, 4);
        this.lastQueueChangeTime = 0;
        this.queueLength = 0;
        statistic = new Statistic();
        runStatistic = new Statistic();
        ticketsStatistics = new TicketsStatistics();
        runTicketsStatistics = new TicketsStatistics();
    }
    public TicketsStatistics getRunTicketsStatistics() {
        return runTicketsStatistics;
    }
    public void setRunTicketsStatistics(TicketsStatistics runTicketsStatistics) {
        this.runTicketsStatistics = runTicketsStatistics;
    }
    public int getQueueLength() {
        return queueLength;
    }
    public void setQueueLength(int queueLength) {
        this.queueLength = queueLength;
    }
        public double getLastQueueChangeTime() {
        return lastQueueChangeTime;
    }
    public void setLastQueueChangeTime(double lastQueueChangeTime) {
        this.lastQueueChangeTime = lastQueueChangeTime;
    }
    public TicketsStatistics getTicketsStatistics() {
        return ticketsStatistics;
    }
    public void setTicketsStatistics(TicketsStatistics ticketsStatistics) {
        this.ticketsStatistics = ticketsStatistics;
    }
    public Statistic getStatistic() {
        return statistic;
    }

    public Statistic getRunStatistic() {
        return runStatistic;
    }
    public boolean isCustomerQueueEmpty() {
        return customers.isEmpty();
    }
    public boolean isServerBusy() {
        return serverBusy;
    }
    public void addCustomerToQueue(Customer customer) {
        customers.add(customer);
    }
    public void setServerBusy(boolean b) {
        serverBusy = b;
    }
    public void addEvent(Event event) {
        if (serverBusy) {
            
        }
        eventCalendar.add(event);
    }
    public double getServiceTime() {
        return serviceGenerator.getSample();
    }
    public Customer getCustomer() {
        return customers.remove(0);
    }
    public double getArrivalTime() {
        return arrivalGenerator.getSample();
    }
    @Override
    protected void afterSimRun() {
        
        /* this.getTicketsStatistics().reset();
        this.getStatistic().reset();
        this.setCurrentTime(0);
        this.lastQueueChangeTime = 0; */
    }
    @Override
    protected void beforeSimulation() {
        CustomerArrival customerArrival = new CustomerArrival(arrivalGenerator.getSample(), this);
        addEvent(customerArrival);
    }
    /* @Override
    protected void beforeSimRun() {
        CustomerArrival customerArrival = new CustomerArrival(arrivalGenerator.getSample(), this);
        addEvent(customerArrival);
    } */
    @Override
    protected void afterSimulation() {
        System.out.println("Average time in line after simulation: " + getRunStatistic().getAverage());
        System.out.println("Average num of people in line after simulation: " + getRunTicketsStatistics().getWeightedAverage());
    }
}
