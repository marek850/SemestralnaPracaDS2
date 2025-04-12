package com.sem2.SimCore;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import com.sem2.Events.SystemEvent;
import com.sem2.Events.Orders.OrderArrival;
import com.sem2.FurnitureCompany.AssemblyStation;
import com.sem2.FurnitureCompany.Employee;
import com.sem2.FurnitureCompany.Order;
import com.sem2.FurnitureCompany.Enums.EmployeeState;
import com.sem2.FurnitureCompany.Enums.EmployeeType;
import com.sem2.Generators.ContinuousGenerator;
import com.sem2.Generators.DiscreteGenerator;
import com.sem2.Generators.ExponentialGenerator;
import com.sem2.Generators.TriangularGenerator;
import com.sem2.Statistics.Statistic;
import com.sem2.Statistics.WeightStatistics;
public class FurnitureCompany extends EventSimulationCore{
    private int lastStationId = 0;
    private int lastEmployeeId = 0;
    public int getLastEmployeeId() {
        return lastEmployeeId;
    }
    public void setLastEmployeeId(int lastEmployeeId) {
        this.lastEmployeeId = lastEmployeeId;
    }
    private ArrayList<Order> allActiveOrders = new ArrayList<>();
    private Statistic orderFinishTime = new Statistic();
    private Statistic globalFinishTime = new Statistic();
    private WeightStatistics waitingOrders = new WeightStatistics();
    private boolean isDone = false;
    public boolean isDone() {
		return isDone;
	}
	public WeightStatistics getWaitingOrders() {
		return waitingOrders;
	}
	public void setWaitingOrders(WeightStatistics waitingOrders) {
		this.waitingOrders = waitingOrders;
	}
	private Statistic globalWaitingOrders = new Statistic();
    private double waitingOrderQueueChange;
    public double getWaitingOrderQueueChange() {
		return waitingOrderQueueChange;
	}
	public void setWaitingOrderQueueChange(double waitingOrderQueueChange) {
		this.waitingOrderQueueChange = waitingOrderQueueChange;
	}
	public Statistic getGlobalWaitingOrders() {
		return globalWaitingOrders;
	}
	public void setGlobalWaitingOrders(Statistic globalWaitingOrders) {
		this.globalWaitingOrders = globalWaitingOrders;
	}
	private Statistic workloadA = new Statistic();
    public Statistic getWorkloadA() {
		return workloadA;
	}
	public void setWorkloadA(Statistic workloadA) {
		this.workloadA = workloadA;
	}
	private Statistic workloadB = new Statistic();
    public Statistic getWorkloadB() {
		return workloadB;
	}
	public void setWorkloadB(Statistic workloadB) {
		this.workloadB = workloadB;
	}
	private Statistic workloadC = new Statistic();
    public Statistic getWorkloadC() {
		return workloadC;
	}
	public void setWorkloadC(Statistic workloadC) {
		this.workloadC = workloadC;
	}
	public Statistic getGlobalFinishTime() {
        return globalFinishTime;
    }
    public void setGlobalFinishTime(Statistic globalFinishTime) {
        this.globalFinishTime = globalFinishTime;
    }
    
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
    public ArrayList<Order> getWaitingOrdersQueue() {
        return waitingOrdersQueue;
    }
    private ArrayList<Order> varnishingWaitQueue = new ArrayList<>();
    public ArrayList<Order> getVarnishingWaitQueue() {
        return varnishingWaitQueue;
    }
    private ArrayList<Order> assemblingWaitQueue = new ArrayList<>();
    public ArrayList<Order> getAssemblingWaitQueue() {
        return assemblingWaitQueue;
    }
    private ArrayList<Order> fittingWaitQueue = new ArrayList<>();

    public ArrayList<Order> getFittingWaitQueue() {
        return fittingWaitQueue;
    }
    private ArrayList<Employee> availableEmployeesA = new ArrayList<>();
    public ArrayList<Employee> getAvailableEmployeesA() {
        return availableEmployeesA;
    }
    private ArrayList<Employee> availableEmployeesB = new ArrayList<>();
    public ArrayList<Employee> getAvailableEmployeesB() {
        return availableEmployeesB;
    }
    private ArrayList<Employee> availableEmployeesC = new ArrayList<>();

    public ArrayList<Employee> getAvailableEmployeesC() {
        return availableEmployeesC;
    }
    private PriorityQueue<AssemblyStation> availableAssemblyStations = new PriorityQueue<AssemblyStation>();
    public PriorityQueue<AssemblyStation> getAvailableAssemblyStations() {
        return availableAssemblyStations;
    }
    private ArrayList<AssemblyStation> allAssemblyStations = new ArrayList<>();

    public ArrayList<AssemblyStation> getAllAssemblyStations() {
        return allAssemblyStations;
    }
    private double waitingOrderQueueChangeTime;
    public double getWaitingOrderQueueChangeTime() {
		return waitingOrderQueueChangeTime;
	}
	public void setWaitingOrderQueueChangeTime(double waitingOrderQueueChangeTime) {
		this.waitingOrderQueueChangeTime = waitingOrderQueueChangeTime;
	}
    private int seed = new Random().nextInt(1000000);
	private Random seedGenerator = new Random(seed);
    
    private ExponentialGenerator orderArrivalGen = new ExponentialGenerator(seedGenerator, 1800); //30 minut * 60s = 1800s
    private TriangularGenerator storageMoveTimeGen = new TriangularGenerator(seedGenerator, 60, 480, 120);
    private TriangularGenerator stationMoveTimeGen = new TriangularGenerator(seedGenerator, 120, 500, 150);

    private TriangularGenerator materialPrepTimGen = new TriangularGenerator(seedGenerator, 300, 900, 500);
    //Stoly
    private ContinuousGenerator tableCutTimeGen = new ContinuousGenerator(seedGenerator, List.of(new double[]{600, 1500},new double[]{1500, 3000}), List.of(0.6, 0.4));
    private ContinuousGenerator tableVarnTimeGen = new ContinuousGenerator(seedGenerator, List.of(new double[]{12000, 36600}), List.of(1.0));
    private ContinuousGenerator tableAssembleTimeGen = new ContinuousGenerator(seedGenerator, List.of(new double[]{1800, 3600}), List.of(1.0));
    //Stolicky
    private ContinuousGenerator chairCutTimeGen = new ContinuousGenerator(seedGenerator, List.of(new double[]{720, 960}), List.of(1.0));
    private ContinuousGenerator chairVarnTimeGen = new ContinuousGenerator(seedGenerator, List.of(new double[]{12600, 32400}), List.of(1.0));
    private ContinuousGenerator chairAssembleTimeGen = new ContinuousGenerator(seedGenerator, List.of(new double[]{840, 1440}), List.of(1.0));
    //Skrine
    private ContinuousGenerator wardrobeCutTimeGen = new ContinuousGenerator(seedGenerator, List.of(new double[]{900, 4800}), List.of(1.0));
    private ContinuousGenerator wardrobeVarnTimeGen = new ContinuousGenerator(seedGenerator, List.of(new double[]{36000, 42000}), List.of(1.0));
    private ContinuousGenerator wardrobeAssembleTimeGen = new ContinuousGenerator(seedGenerator, List.of(new double[]{2100, 4500}), List.of(1.0));
    private ContinuousGenerator wardrobeFittingTimeGen = new ContinuousGenerator(seedGenerator, List.of(new double[]{900, 1500}), List.of(1.0));
    private boolean slowdownRatio = false;
    public Random orderIDGen = new Random(seedGenerator.nextInt());
    public boolean isSlowdownRatio() {
        return slowdownRatio;
    }
    private DiscreteGenerator orderTypeGen = new DiscreteGenerator(seedGenerator, List.of(new int[]{0, 100}), List.of(1.0));

    public FurnitureCompany(double maxSimulationTime, int numberOfAEmployees, int numberOfBEmployees, int numberOfCEmployees) {
        super(maxSimulationTime);
        System.out.println("Seed: " + seed);
        waitingOrderQueueChangeTime = 0;
        for (int i = 0; i < numberOfAEmployees; i++) {
            employeesA.add(new Employee(lastEmployeeId, EmployeeType.A));
            lastEmployeeId++;
        }
        for (int i = 0; i < numberOfBEmployees; i++) {
            employeesB.add(new Employee(lastEmployeeId, EmployeeType.B));
            lastEmployeeId++;
        }
        for (int i = 0; i < numberOfCEmployees; i++) {
            employeesC.add(new Employee(lastEmployeeId, EmployeeType.C));
            lastEmployeeId++;
        }
    }
    
    public double getMatPrepTime() {
        return /* 120; */materialPrepTimGen.getSample();
    }
    public double getStationMoveTime() {
        return /* 200; */stationMoveTimeGen.getSample();
    }
    public double getStorageMoveTime() {
        return /* 150; */storageMoveTimeGen.getSample();
    }
    public double getOrderArrivalTime() {
        return /* 100; */orderArrivalGen.getSample();
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
    public void addOrderWaitingForCut(Order order) {
        waitingOrdersQueue.add(order);
    }
    public boolean isOrderWaitingForCutting() {
        return !waitingOrdersQueue.isEmpty();
    }
    public double getCuttingTime(Order order) {
        switch (order.getType()) {
            case TABLE:
                return /* 600; */tableCutTimeGen.getSample();
            case CHAIR:
                return /* 720; */chairCutTimeGen.getSample();
            case WARDROBE:
                return /* 900; */wardrobeCutTimeGen.getSample();
            default:
                throw new IllegalArgumentException("Unknown order type: " + order.getType());
        }
    }
    public void freeEmployee(Employee employee) {
        employee.setState(EmployeeState.IDLE);
        employee.setWorking(false, getCurrentTime());
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
                throw new IllegalArgumentException("Unknown employee type: " + employee.getType());
        }
    }
    public void addOrderForVarnish(Order order) {
        varnishingWaitQueue.add(order);
    }
    public Order getOrderWaitingForCutting() {
        return waitingOrdersQueue.remove(0);
    }
    public double getFittingTime() {
        return /* 900; */wardrobeFittingTimeGen.getSample();
    }
    public double getVarnishingTime(Order order) {
        switch (order.getType()) {
            case TABLE:
                return /* 1000; */tableVarnTimeGen.getSample();
            case CHAIR:
                return /* 1000; */chairVarnTimeGen.getSample();
            case WARDROBE:
                return /* 1000; */wardrobeVarnTimeGen.getSample();
            default:
                throw new IllegalArgumentException("Unknown order type: " + order.getType());
        }
    }
    public double getAssembleTime(Order order) {
        switch (order.getType()) {
            case TABLE:
                return /* 1800; */tableAssembleTimeGen.getSample();
            case CHAIR:
                return /* 800; */chairAssembleTimeGen.getSample();
            case WARDROBE:
                return/*  1000; */wardrobeAssembleTimeGen.getSample();
            default:
                throw new IllegalArgumentException("Unknown order type: " + order.getType());
        }
    }
    public void updateWorkloadStats(){
        for (Employee employee : employeesA) {
            employee.getWorkloadStat().addValue(employee.getWorkload(getCurrentTime()));
        }
        for (Employee employee : employeesB) {
            employee.getWorkloadStat().addValue(employee.getWorkload(getCurrentTime()));
        }
        for (Employee employee : employeesC) {
            employee.getWorkloadStat().addValue(employee.getWorkload(getCurrentTime()));
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
        return availableAssemblyStations.poll();
    }
    public int getLastStationId() {
        return lastStationId;
    }
    public void setLastStationId(int lastStationId) {
        this.lastStationId = lastStationId;
    }
    @Override
    protected void beforeSimulation() {
        
    }
    @Override
    protected void beforeSimRun() {
        waitingOrdersQueue.clear();
        varnishingWaitQueue.clear();
        assemblingWaitQueue.clear();
        fittingWaitQueue.clear();
        availableEmployeesA.clear();
        availableEmployeesB.clear();
        availableEmployeesC.clear();
        availableAssemblyStations.clear();
        allActiveOrders.clear();
        allAssemblyStations.clear();
        orderFinishTime.reset();
        eventCalendar.clear();
        waitingOrders.reset();
        setCurrentTime(0);
        for (Employee employee : employeesA) {
            employee.reset();
            availableEmployeesA.add(employee);
        }
        for (Employee employee : employeesB) {
            employee.reset();
            availableEmployeesB.add(employee);
        }
        for (Employee employee : employeesC) {
            employee.reset();
            availableEmployeesC.add(employee);
        }
        waitingOrderQueueChangeTime = 0;
        OrderArrival orderArrival = new OrderArrival(getCurrentTime() + getOrderArrivalTime(), this);
        SystemEvent systemEvent = new SystemEvent(getCurrentTime(), this);
        addEvent(systemEvent);
        addEvent(orderArrival);
    }
    @Override
    protected void afterSimRun() {
        super.afterSimRun();
        
        if (!isStop()) {
            globalFinishTime.addValue(orderFinishTime.getAverage());
            globalWaitingOrders.addValue(waitingOrders.getWeightedAverage());
            updateWorkloadStats();
            workloadA.addValue(getAverageGroupWorkload(EmployeeType.A));
            workloadB.addValue(getAverageGroupWorkload(EmployeeType.B));
            workloadC.addValue(getAverageGroupWorkload(EmployeeType.C));
        }
        
        
    }
    public double getAverageGroupWorkload(EmployeeType type) {
        double sum = 0;
        int count = 0;
        switch (type) {
            case A:
                for (Employee employee : employeesA) {
                    sum += employee.getWorkload(getCurrentTime());
                    count++;
                }
                break;
            case B:
                for (Employee employee : employeesB) {
                    sum += employee.getWorkload(getCurrentTime());
                    count++;
                }
                break;
            case C:
                for (Employee employee : employeesC) {
                    sum += employee.getWorkload(getCurrentTime());
                    count++;
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown employee type: " + type);
        }
        return sum / count;
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
        isDone = true;
        refreshGUI();
        isDone = false;
        /* System.out.println("Simulation finished.");
        System.out.println("Average order processing time: " + globalFinishTime.getAverage() + " seconds.");
        System.out.println("Average order processing time: " + globalFinishTime.getAverage()/60 + " minutes.");
        System.out.println("Average order processing time: " + globalFinishTime.getAverage()/3600 + " hours."); 
        double[] confidenceInterval = globalFinishTime.getConfidenceInterval95();
        System.out.println("Reliability interval: <" + confidenceInterval[0]/3600 + ", " + confidenceInterval[1]/3600 + ">"); */
    }
}
