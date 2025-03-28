package com.sem2.SimCore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.sem2.Events.Event;
import com.sem2.Events.Orders.OrderArrival;
import com.sem2.FurnitureCompany.AssemblyStation;
import com.sem2.FurnitureCompany.Employee;
import com.sem2.FurnitureCompany.Order;
import com.sem2.FurnitureCompany.Enums.EmployeeState;
import com.sem2.FurnitureCompany.Enums.EmployeeType;
import com.sem2.FurnitureCompany.Enums.Position;
import com.sem2.Generators.ContinuousGenerator;
import com.sem2.Generators.DiscreteGenerator;
import com.sem2.Generators.ExponentialGenerator;
import com.sem2.Generators.TriangularGenerator;
import com.sem2.Statistics.Statistic;
import com.sem2.UserInterface.UserInterface;
public class FurnitureCompany extends EventSimulationCore{
    private int lastStationId = 0;
    private ArrayList<Order> allActiveOrders = new ArrayList<>();
    private Statistic orderFinishTime = new Statistic();

    public Statistic getOrderFinishTime() {
        return orderFinishTime;
    }
    public ArrayList<Order> getAllActiveOrders() {
        return allActiveOrders;
    }
    private ArrayList<Employee> employeesA = new ArrayList<>();
    public ArrayList<Employee> getEmployeesA() {
        return employeesA;
    }
    private ArrayList<Employee> employeesB = new ArrayList<>();
    public ArrayList<Employee> getEmployeesB() {
        return employeesB;
    }
    private ArrayList<Employee> employeesC = new ArrayList<>();

    public ArrayList<Employee> getEmployeesC() {
        return employeesC;
    }
    private ArrayList<Order> waitingOrdersQueue = new ArrayList<>();
    private ArrayList<Order> varnishingWaitQueue = new ArrayList<>();
    private ArrayList<Order> assemblingWaitQueue = new ArrayList<>();
    private ArrayList<Order> fittingWaitQueue = new ArrayList<>();

    private ArrayList<Employee> availableEmployeesA = new ArrayList<>();
    private ArrayList<Employee> availableEmployeesB = new ArrayList<>();
    private ArrayList<Employee> availableEmployeesC = new ArrayList<>();

    private ArrayList<AssemblyStation> availableAssemblyStations = new ArrayList<>();
    private ArrayList<AssemblyStation> allAssemblyStations = new ArrayList<>();

    public ArrayList<AssemblyStation> getAllAssemblyStations() {
        return allAssemblyStations;
    }
    private Random seedGenerator = new Random();
    private ExponentialGenerator orderArrivalGen = new ExponentialGenerator(seedGenerator, 1800); //30 minut * 60s = 1800s
    private TriangularGenerator storageMoveTimeGen = new TriangularGenerator(seedGenerator, 60, 480, 120);
    private TriangularGenerator stationMoveTimeGen = new TriangularGenerator(seedGenerator, 120, 500, 150);

    private TriangularGenerator materialPrepTimGen = new TriangularGenerator(seedGenerator, 300, 900, 500);
    //Stoly
    private ContinuousGenerator tableCutTimeGen = new ContinuousGenerator(seedGenerator, List.of(new double[]{10.0 * 60, 25.0 * 60},new double[]{25.0 * 60, 50.0 * 60}), List.of(0.6, 0.4));
    private ContinuousGenerator tableVarnTimeGen = new ContinuousGenerator(seedGenerator, List.of(new double[]{200.0 * 60, 611.0 *60}), List.of(1.0));
    private ContinuousGenerator tableAssembleTimeGen = new ContinuousGenerator(seedGenerator, List.of(new double[]{30.0*60, 61.0*60}), List.of(1.0));
    //Stolicky
    private ContinuousGenerator chairCutTimeGen = new ContinuousGenerator(seedGenerator, List.of(new double[]{12.0*60, 17.0*60}), List.of(1.0));
    private ContinuousGenerator chairVarnTimeGen = new ContinuousGenerator(seedGenerator, List.of(new double[]{210.0 * 60, 541.0*60}), List.of(1.0));
    private ContinuousGenerator chairAssembleTimeGen = new ContinuousGenerator(seedGenerator, List.of(new double[]{14.0*60, 25.0*60}), List.of(1.0));
    //Skrine
    private ContinuousGenerator wardrobeCutTimeGen = new ContinuousGenerator(seedGenerator, List.of(new double[]{15.0*60, 81.0*60}), List.of(1.0));
    private ContinuousGenerator wardrobeVarnTimeGen = new ContinuousGenerator(seedGenerator, List.of(new double[]{600.0*60, 701.0*60}), List.of(1.0));
    private ContinuousGenerator wardrobeAssembleTimeGen = new ContinuousGenerator(seedGenerator, List.of(new double[]{35.0*60, 76.0*60}), List.of(1.0));
    private ContinuousGenerator wardrobeFittingTimeGen = new ContinuousGenerator(seedGenerator, List.of(new double[]{15.0*60, 26.0*60}), List.of(1.0));

    private DiscreteGenerator orderTypeGen = new DiscreteGenerator(seedGenerator, List.of(new int[]{0, 100}), List.of(1.0));

    public FurnitureCompany(double maxSimulationTime, int numberOfAEmployees, int numberOfBEmployees, int numberOfCEmployees) {
        super(maxSimulationTime);
        for (int i = 0; i < numberOfAEmployees; i++) {
            employeesA.add(new Employee(EmployeeType.A));
        }
        for (int i = 0; i < numberOfBEmployees; i++) {
            employeesB.add(new Employee(EmployeeType.B));
        }
        for (int i = 0; i < numberOfCEmployees; i++) {
            employeesC.add(new Employee(EmployeeType.C));
        }

        for (Employee employee : employeesA) {
            availableEmployeesA.add(employee);
        }
        for (Employee employee : employeesB) {
            availableEmployeesB.add(employee);
        }
        for (Employee employee : employeesC) {
            availableEmployeesC.add(employee);
        }
        
    }
    
    public double getMatPrepTime() {
        return materialPrepTimGen.getSample();
    }
    public double getStationMoveTime() {
        return stationMoveTimeGen.getSample();
    }
    public double getStorageMoveTime() {
        return storageMoveTimeGen.getSample();
    }
    public double getOrderArrivalTime() {
        return orderArrivalGen.getSample();
    }
    public boolean isAAvailable() {
        return !availableEmployeesA.isEmpty();
    }
    public Employee getAAvailable() {
        return availableEmployeesA.remove(0);
    }
    public boolean isBAvailable() {
        return !availableEmployeesB.isEmpty();
    }
    public Employee getBAvailable() {
        return availableEmployeesB.remove(0);
    }
    public boolean isCAvailable() {
        return !availableEmployeesC.isEmpty();
    }
    public Employee getCAvailable() {
        return availableEmployeesC.remove(0);
    }
    public int getOrderType() {
        return orderTypeGen.getSample();
    }
    public void addOrder(Order order) {
        waitingOrdersQueue.add(order);
    }
    public boolean isOrderWaitingForCutting() {
        return !waitingOrdersQueue.isEmpty();
    }
    public double getCuttingTime(Order order) {
        switch (order.getType()) {
            case TABLE:
                return tableCutTimeGen.getSample();
            case CHAIR:
                return chairCutTimeGen.getSample();
            case WARDROBE:
                return wardrobeCutTimeGen.getSample();
            default:
                return 0;
        }
    }
    public void freeEmployee(Employee employee) {
        employee.setState(EmployeeState.IDLE);
        switch (employee.getType()) {
            case A:
                availableEmployeesA.add(employee);
                break;
            case B:
                availableEmployeesB.add(employee);
                break;
            case C:
                availableEmployeesC.add(employee);
                break;
            default:
                break;
        }
    }
    public void addOrderForVarnish(Order order) {
        varnishingWaitQueue.add(order);
    }
    public Order getOrderWaitingForCutting() {
        return waitingOrdersQueue.remove(0);
    }
    public double getFittingTime() {
        return wardrobeFittingTimeGen.getSample();
    }
    public double getVarnishingTime(Order order) {
        switch (order.getType()) {
            case TABLE:
                return tableVarnTimeGen.getSample();
            case CHAIR:
                return chairVarnTimeGen.getSample();
            case WARDROBE:
                return wardrobeVarnTimeGen.getSample();
            default:
                return 0;
        }
    }
    public double getAssembleTime(Order order) {
        switch (order.getType()) {
            case TABLE:
                return tableAssembleTimeGen.getSample();
            case CHAIR:
                return chairAssembleTimeGen.getSample();
            case WARDROBE:
                return wardrobeAssembleTimeGen.getSample();
            default:
                return 0;
        }
    }
    public boolean isOrderWaitingForVarnish() {
        return !varnishingWaitQueue.isEmpty();
    }
    public Order getOrderWaitingForVarnish() {
        return varnishingWaitQueue.remove(0);
    }
    public void addOrderForAssembly(Order order) {
        assemblingWaitQueue.add(order);
    }
    public boolean isOrderWaitingForAssembly() {
        return !assemblingWaitQueue.isEmpty();
    }
    public void addOrderForFitting(Order order) {
        fittingWaitQueue.add(order);
    }
    public boolean isOrderWaitingForFitting() {
        return !fittingWaitQueue.isEmpty();
    }
    public Order getOrderWaitingForFitting() {
        return fittingWaitQueue.remove(0);
    }
    public Order getOrderWaitingForAssembly() {
        return assemblingWaitQueue.remove(0);
    }
    public boolean isAvailableStation() {
        return !availableAssemblyStations.isEmpty();
    }
    public void addFreeStation(AssemblyStation station) {
        availableAssemblyStations.add(station);
    }
    public AssemblyStation getBestAssemblyStation(){
        AssemblyStation bestStation = availableAssemblyStations.get(0);
        for (AssemblyStation station : availableAssemblyStations) {
            if (station.getId() < bestStation.getId()) {
                bestStation = station;
            }
        }
        return bestStation;
    }
    public int getLastStationId() {
        return lastStationId;
    }
    public void setLastStationId(int lastStationId) {
        this.lastStationId = lastStationId;
    }
    @Override
    protected void beforeSimRun() {
        super.beforeSimRun();
        OrderArrival orderArrival = new OrderArrival( getCurrentTime() + getOrderArrivalTime(), this);
        addEvent(orderArrival);
    }
    @Override
    protected void afterSimRun() {
        super.afterSimRun();
        waitingOrdersQueue.clear();
        varnishingWaitQueue.clear();
        assemblingWaitQueue.clear();
        fittingWaitQueue.clear();
        availableEmployeesA.clear();
        availableEmployeesB.clear();
        availableEmployeesC.clear();
        availableAssemblyStations.clear();
        for (Employee employee : employeesA) {
            employee.setState(EmployeeState.IDLE);
            employee.setPosition(Position.STORAGE);
        }
        for (Employee employee : employeesB) {
            employee.setState(EmployeeState.IDLE);
            employee.setPosition(Position.STORAGE);
        }
        for (Employee employee : employeesC) {
            employee.setState(EmployeeState.IDLE);
            employee.setPosition(Position.STORAGE);
        }
        allActiveOrders.clear();
        allAssemblyStations.clear();
        System.out.println("Average order processing time: " + orderFinishTime.getAverage() + " seconds.");
        System.out.println("Average order processing time: " + orderFinishTime.getAverage()/60 + " minutes.");
        System.out.println("Average order processing time: " + orderFinishTime.getAverage()/3600 + " hours.");
    }
    public void addActiveOrder(Order order) {
        allActiveOrders.add(order);
    }
    public void removeActiveOrder(Order order) {
        allActiveOrders.remove(order);
    }
    public void addStation(AssemblyStation station) {
        allAssemblyStations.add(station);
    }
    @Override
    protected void afterSimulation() {
        super.afterSimulation();
        System.out.println("Simulation finished.");
        System.out.println("Average order processing time: " + orderFinishTime.getAverage() + " seconds.");
        System.out.println("Average order processing time: " + orderFinishTime.getAverage()/60 + " minutes.");
        System.out.println("Average order processing time: " + orderFinishTime.getAverage()/3600 + " hours.");
    }
}
