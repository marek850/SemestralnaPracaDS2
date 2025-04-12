package com.sem2.Events.Cutting;

import com.sem2.Events.EmpFurnitureEvent;
import com.sem2.Events.Moving.MoveToStorage;
import com.sem2.FurnitureCompany.Employee;
import com.sem2.FurnitureCompany.Order;
import com.sem2.FurnitureCompany.Enums.EmployeeState;
import com.sem2.FurnitureCompany.Enums.OrderState;
import com.sem2.SimCore.EventSimulationCore;
import com.sem2.SimCore.FurnitureCompany;

public class PrevzatieObjednavky extends EmpFurnitureEvent{

    public PrevzatieObjednavky(double time, EventSimulationCore simulationCore, Employee employee, Order order) {
        super(time, simulationCore, employee, order);
        
    }
    @Override
    public void execute() {
        super.execute();
        FurnitureCompany sim = (FurnitureCompany) getSimulationCore();
        switch (getEmployee().getCurrentPosition()) {
                //Ak je uz v sklade nemusi sa presuvat
            case STORAGE:
                getOrder().setState(OrderState.PREPARING_MATERIAL);
                getEmployee().setState(EmployeeState.PREPARING_MATERIAL);
                sim.addEvent(new PrepareMaterial(getTime() + sim.getMatPrepTime(), sim, getEmployee(), getOrder()));
                break;
            //Ak nie je v sklade musi sa presunut
            case ASSEMBLY_STATION:
                getEmployee().setState(EmployeeState.MOVING);
                sim.addEvent(new MoveToStorage(getTime() + sim.getStorageMoveTime(), sim, getEmployee(), getOrder()));
                break;
            default:
                break;
    }
}
    
}
