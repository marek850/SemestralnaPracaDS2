package com.sem2.FurnitureCompany.Enums;

public enum EmployeeState {
    IDLE("Nečinný"), 
    MOVING("Presúva sa"), 
    PREPARING_MATERIAL("Pripravuje materiál"), 
    CUTTING("Reže"), 
    KOKOT("Kokot"),
    VARNISHING("Lakuje"), 
    ASSEMBLING("Skladá"), 
    FITTING("Montuje kovania");

    private final String displayName;

    EmployeeState(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
