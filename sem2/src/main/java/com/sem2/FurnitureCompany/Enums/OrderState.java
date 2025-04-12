package com.sem2.FurnitureCompany.Enums;

public enum OrderState {
    PENDING("Čaká na rezanie"),
    PREBERA_OBJEDNAVKU("Preberana pracovnikom"),
    PREPARING_MATERIAL("Príprava materiálu"),
    MATERIAL_PREPARED("Materiál pripravený"),
    BEING_CUT("Reže sa"),
    CUT("Narezana"),
    EMPLOYEE_MOVING("Zamestnanec sa presúva"),
    WAITING_FOR_VARNISH("Čaká na lakovanie"),
    BEING_VARNISHED("Lakuje sa"),
    VARNISHED("Nalakovaná"),
    WAITING_FOR_ASSEMBLY("Čaká na skladanie"),
    BEING_ASSEMBLED("Skladá sa"),
    ASSEMBLED("Poskladaná"),
    WAITING_FOR_FITTING("Čaká na montáž kovania"),
    BEING_FITTED("Montujú kovania"),
    FITTED("Namontované kovania"),
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
