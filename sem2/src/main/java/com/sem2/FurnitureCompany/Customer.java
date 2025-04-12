package com.sem2.FurnitureCompany;

public class Customer implements Comparable<Customer> {
    private double arrivalTime;

    public Customer(double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
    public double getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
    @Override
    public int compareTo(Customer o) {
        return Double.compare(this.getArrivalTime(), o.getArrivalTime());
    }
}
