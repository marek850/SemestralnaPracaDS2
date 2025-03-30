package com.sem2.FurnitureCompany;

import com.sem2.FurnitureCompany.Enums.Process;

public class AssemblyStation implements Comparable<AssemblyStation> {
    private int id;
    public AssemblyStation(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    private Process currentProcess;
    public Process getCurrentProcess() {
        return currentProcess;
    }
    public void setCurrentProcess(Process currentProcess) {
        this.currentProcess = currentProcess;
    }
    @Override
    public int compareTo(AssemblyStation o) {
        return Integer.compare(this.id, o.id);
    }
    
}