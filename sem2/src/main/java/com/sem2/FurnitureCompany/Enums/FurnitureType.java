package com.sem2.FurnitureCompany.Enums;

public enum FurnitureType {
    CHAIR("Stolička"), 
    TABLE("Stôl"), 
    WARDROBE("Skriňa");

    private final String displayName;

    FurnitureType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
