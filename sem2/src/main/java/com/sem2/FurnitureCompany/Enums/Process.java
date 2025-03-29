package com.sem2.FurnitureCompany.Enums;

public enum Process {
    NONE("Žiadny"),
    CUTTING("Rezanie"),
    VARNISHING("Lakovanie"),
    ASSEMBLING("Skladanie"),
    FITTING("Montáž kovania");

    private final String displayName;

    Process(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
