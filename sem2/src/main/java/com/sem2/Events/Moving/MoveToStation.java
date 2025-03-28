package com.sem2.Events.Moving;

import com.sem2.Events.EmpFurnitureEvent;
import com.sem2.Events.Assembling.AssemblyEnd;
import com.sem2.Events.Assembling.Fitting;
import com.sem2.Events.Cutting.CuttingStart;
import com.sem2.Events.Varnishing.VarnishingEnd;
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

public class MoveToStation extends EmpFurnitureEvent{

    public MoveToStation(double time, EventSimulationCore simulationCore, Employee employee, Order order) {
        super(time, simulationCore, employee, order);
    }
    @Override
    public void execute() {
        super.execute();
        FurnitureCompany sim = (FurnitureCompany) getSimulationCore();
        double moveTime = 0;
        switch (getEmployee().getCurrentPosition()) {
            case STORAGE:
                moveTime = getTime() + sim.getStorageMoveTime();
                getEmployee().setPosition(Position.ASSEMBLY_STATION);

                break;
            case ASSEMBLY_STATION:
                moveTime = getTime() + sim.getStationMoveTime();
                break;
            default:
                break;
        }

        switch (getEmployee().getType()) {
            case A:
                CuttingStart cuttingStart = new CuttingStart(moveTime, sim, getEmployee(), getOrder());
                getOrder().setState(OrderState.BEING_CUT);
                sim.addEvent(cuttingStart);
                break;
            case B:
                AssemblyEnd assemblyEnd = new AssemblyEnd(getTime() + sim.getAssembleTime(getOrder()), sim, getEmployee(), getOrder());
                getEmployee().setState(EmployeeState.ASSEMBLING);
                getOrder().setState(OrderState.BEING_ASSEMBLED);
                sim.addEvent(assemblyEnd);
                break;
            case C:
                if (getOrder().getState() == OrderState.ASSEMBLED && getOrder().getType() == FurnitureType.WARDROBE) {
                    getEmployee().setState(EmployeeState.FITTING);
                    getOrder().getStation().setCurrentProcess(Process.FITTING);
                    Fitting fitting = new Fitting(getTime() + sim.getFittingTime(), sim, getEmployee(), getOrder());
                    sim.addEvent(fitting);
                } else {
                    getEmployee().setState(EmployeeState.VARNISHING);
                    VarnishingEnd varnishingEnd = new VarnishingEnd(getTime() + sim.getVarnishingTime(getOrder()), sim, getEmployee(), getOrder());
                    getOrder().setState(OrderState.BEING_VARNISHED);
                    sim.addEvent(varnishingEnd);
                }
                break;
            default:
                break;
        }
        sim.refreshGUI();
    }
    
}
