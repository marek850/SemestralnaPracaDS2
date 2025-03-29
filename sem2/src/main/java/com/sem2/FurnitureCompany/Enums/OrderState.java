package com.sem2.FurnitureCompany.Enums;

public enum OrderState {
    PENDING("Čaká"),
    PREPARING_MATERIAL("Príprava materiálu"),
    BEING_CUT("Reže sa"),
    WAITING_FOR_VARNISH("Čaká na lakovanie"),
    BEING_VARNISHED("Lakuje sa"),
    WAITING_FOR_ASSEMBLY("Čaká na skladanie"),
    BEING_ASSEMBLED("Skladá sa"),
    ASSEMBLED("Poskladaná"),
    WAITING_FOR_FITTING("Čaká na montáž kovania"),
    BEING_FITTED("Montujú kovania"),
    FINISHED("Dokončená");

    private final String displayName;

    OrderState(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
