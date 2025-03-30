package com.sem2.Events.Assembling;

import com.sem2.Events.EmpFurnitureEvent;
import com.sem2.Events.Moving.MoveToStation;
import com.sem2.Events.Orders.FinalizeOrder;
import com.sem2.FurnitureCompany.Employee;
import com.sem2.FurnitureCompany.Order;
import com.sem2.FurnitureCompany.Enums.EmployeeState;
import com.sem2.FurnitureCompany.Enums.FurnitureType;
import com.sem2.FurnitureCompany.Enums.OrderState;
import com.sem2.FurnitureCompany.Enums.Position;
import com.sem2.FurnitureCompany.Enums.Process;
import com.sem2.SimCore.EventSimulationCore;
import com.sem2.SimCore.FurnitureCompany;
import com.sem2.SimCore.SimulationCore;

public class AssemblyEnd extends EmpFurnitureEvent {

    public AssemblyEnd(double time, EventSimulationCore simulationCore, Employee employee, Order order) {
        super(time, simulationCore, employee, order);
    }
    @Override
    public void execute() {
        super.execute();
        FurnitureCompany sim = (FurnitureCompany) getSimulationCore();

        switch (getOrder().getType()) {
            case WARDROBE:
                if(sim.isCAvailable()){
                    Employee employee = sim.getCAvailable();
                    employee.setState(EmployeeState.MOVING);
                    double moveTime = 0;
                    switch (getEmployee().getCurrentPosition()) {
                        case STORAGE:
                            moveTime = getTime() + sim.getStorageMoveTime();
                            getEmployee().setPosition(Position.ASSEMBLY_STATION);
                            break;
                        case ASSEMBLY_STATION:
                            if(getEmployee().getStation() == getOrder().getStation()){
                                moveTime = getTime();
                            }
                            else{
                                moveTime = getTime() + sim.getStationMoveTime();
                            }
                            break;
                        default:
                            break;
                    }
                    MoveToStation moveToStation = new MoveToStation(moveTime, sim, employee, getOrder());
                    sim.addEvent(moveToStation);
                }else {
                    getOrder().setState(OrderState.WAITING_FOR_FITTING);
                    getOrder().getStation().setCurrentProcess(Process.NONE);
                    sim.addOrderForFitting(getOrder());
                }
                break;
        
            default:
                FinalizeOrder finalizeOrder = new FinalizeOrder(getTime(), sim, getEmployee(), getOrder());
                sim.addEvent(finalizeOrder);
                break;
        }

        if (sim.isOrderWaitingForAssembly()) {
            Order order = sim.getOrderWaitingForAssembly();
            AssemblyStart assemblyStart = new AssemblyStart(getTime(), sim, getEmployee(), order);
            sim.addEvent(assemblyStart);
        }else
        {
            getEmployee().setState(EmployeeState.IDLE);
            getOrder().setState(OrderState.FINISHED);
            sim.freeEmployee(getEmployee());
        }
        sim.refreshGUI();
    }
}