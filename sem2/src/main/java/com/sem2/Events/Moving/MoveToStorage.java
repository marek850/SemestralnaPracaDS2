package com.sem2.Events.Moving;

import com.sem2.Events.EmpFurnitureEvent;
import com.sem2.Events.Assembling.AssemblyEnd;
import com.sem2.Events.Assembling.Fitting;
import com.sem2.Events.Cutting.CuttingEnd;
import com.sem2.Events.Cutting.PrepareMaterial;
import com.sem2.Events.Varnishing.VarnishingEnd;
import com.sem2.FurnitureCompany.Employee;
import com.sem2.FurnitureCompany.Order;
import com.sem2.FurnitureCompany.Enums.EmployeeState;
import com.sem2.FurnitureCompany.Enums.OrderState;
import com.sem2.FurnitureCompany.Enums.Position;
import com.sem2.FurnitureCompany.Enums.Process;
import com.sem2.SimCore.EventSimulationCore;
import com.sem2.SimCore.FurnitureCompany;
public class MoveToStorage extends EmpFurnitureEvent{

    public MoveToStorage(double time, EventSimulationCore simulationCore, Employee employee, Order order) {
        super(time, simulationCore, employee, order);
        
    }
    @Override
    public void execute() {
        FurnitureCompany sim = (FurnitureCompany) getSimulationCore();
        if (getOrder().getState() == OrderState.PREBERA_OBJEDNAVKU) {
                getEmployee().setPosition(Position.STORAGE);
                getEmployee().setStation(null);
                getEmployee().setState(EmployeeState.PREPARING_MATERIAL);
                getOrder().setState(OrderState.PREPARING_MATERIAL);
                PrepareMaterial prepareMaterial = new PrepareMaterial(getTime() + sim.getMatPrepTime(), sim, getEmployee(), getOrder());
                sim.addEvent(prepareMaterial);
        } else if(getOrder().getState() == OrderState.MATERIAL_PREPARED) {
                getEmployee().setPosition(Position.ASSEMBLY_STATION);
                getEmployee().setStation(getOrder().getStation());
                getEmployee().setState(EmployeeState.CUTTING);
                getOrder().setState(OrderState.BEING_CUT);
                getOrder().getStation().setCurrentProcess(Process.CUTTING);
                CuttingEnd cuttingEnd = new CuttingEnd(getTime() + sim.getCuttingTime(getOrder()), sim, getEmployee(), getOrder());
                sim.addEvent(cuttingEnd);
        } else if(getOrder().getState() == OrderState.CUT || getOrder().getState() == OrderState.WAITING_FOR_VARNISH) {
            getEmployee().setState(EmployeeState.VARNISHING);
                getEmployee().setPosition(Position.ASSEMBLY_STATION);
                getEmployee().setStation(getOrder().getStation());
                getOrder().setState(OrderState.BEING_VARNISHED);
                getOrder().getStation().setCurrentProcess(Process.VARNISHING);
                VarnishingEnd varnishingEnd = new VarnishingEnd(getTime() + sim.getVarnishingTime(getOrder()), sim, getEmployee(), getOrder());
                sim.addEvent(varnishingEnd);
        } 
        else if(getOrder().getState() == OrderState.VARNISHED || getOrder().getState() == OrderState.WAITING_FOR_ASSEMBLY){
            getEmployee().setState(EmployeeState.ASSEMBLING);
                getEmployee().setPosition(Position.ASSEMBLY_STATION);
                getEmployee().setStation(getOrder().getStation());
                getOrder().setState(OrderState.BEING_ASSEMBLED);
                getOrder().getStation().setCurrentProcess(Process.ASSEMBLING);
                AssemblyEnd assemblyEnd = new AssemblyEnd(getTime() + sim.getAssembleTime(getOrder()), sim, getEmployee(), getOrder());
                sim.addEvent(assemblyEnd);
        } else if(getOrder().getState() == OrderState.ASSEMBLED || getOrder().getState() == OrderState.WAITING_FOR_FITTING) {
            getEmployee().setState(EmployeeState.FITTING);
                getEmployee().setPosition(Position.ASSEMBLY_STATION);
                getEmployee().setStation(getOrder().getStation());
                getOrder().setState(OrderState.BEING_FITTED);
                getOrder().getStation().setCurrentProcess(Process.FITTING);
                Fitting fit = new Fitting(getTime() + sim.getFittingTime(), sim, getEmployee(), getOrder());
                sim.addEvent(fit);
        } else{
            throw new IllegalStateException("Invalid order state MoveToStorage: " + getOrder().getState());
        }
    }   
    
}
