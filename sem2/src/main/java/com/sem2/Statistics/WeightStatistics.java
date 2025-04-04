package com.sem2.Statistics;

public class WeightStatistics extends Statistic { 
    private double weightSum;
    private double weight;
    public WeightStatistics() {
        super();
        this.weightSum = 0;
        this.weight = 0;
    }
    public void reset(){
        super.reset();
        this.weightSum = 0;
        this.weight = 0;
    }
    public void addData(double value, double weight) {
        this.weightSum += value * weight;
        this.weight += weight;
    }
    public double getWeight() {
        return this.weight;
    }
    public double getWeightedAverage() {
        return this.weightSum / this.weight;
    }
}

