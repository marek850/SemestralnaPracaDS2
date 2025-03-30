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
        

        switch (getEmployee().getType()) {
            //ak je zamestnanec zo skupiny A tak modelujem presun
            case A:
                double moveTime = 0;
                switch (getEmployee().getCurrentPosition()) {
                    case STORAGE:
                        moveTime = getTime() + sim.getStorageMoveTime();
                        getEmployee().setPosition(Position.ASSEMBLY_STATION);

                        break;
                    case ASSEMBLY_STATION:
                        if (getEmployee().getStation() == getOrder().getStation()) {
                            moveTime = getTime();
                        }else{
                            moveTime = getTime() + sim.getStationMoveTime();
                        }
                        break;
                    default:
                        break;
                }
                getEmployee().setState(EmployeeState.MOVING);
                CuttingStart cuttingStart = new CuttingStart(moveTime, sim, getEmployee(), getOrder());
                sim.addEvent(cuttingStart);
                break;
            //ak je zamestnanec zo skupiny B tak modelujem skladanie
            case B:
                getEmployee().setState(EmployeeState.ASSEMBLING);
                getOrder().setState(OrderState.BEING_ASSEMBLED);
                AssemblyEnd assemblyEnd = new AssemblyEnd(getTime() + sim.getAssembleTime(getOrder()), sim, getEmployee(), getOrder());
                sim.addEvent(assemblyEnd);
                break;
            //ak je zamestnanec zo skupiny C tak modelujem montaz alebo lakovanie
            case C:
                if (getOrder().getState() == OrderState.WAITING_FOR_FITTING && getOrder().getType() == FurnitureType.WARDROBE) {
                    getEmployee().setState(EmployeeState.FITTING);
                    getOrder().getStation().setCurrentProcess(Process.FITTING);
                    getOrder().setState(OrderState.BEING_FITTED);
                    Fitting fitting = new Fitting(getTime() + sim.getFittingTime(), sim, getEmployee(), getOrder());
                    sim.addEvent(fitting);
                } else {
                    getEmployee().setState(EmployeeState.VARNISHING);
                    getOrder().setState(OrderState.BEING_VARNISHED);
                    getOrder().getStation().setCurrentProcess(Process.VARNISHING);
                    VarnishingEnd varnishingEnd = new VarnishingEnd(getTime() + sim.getVarnishingTime(getOrder()), sim, getEmployee(), getOrder());    
                    sim.addEvent(varnishingEnd);
                }
                break;
            default:
                break;
        }
        sim.refreshGUI();
    }
    
}
