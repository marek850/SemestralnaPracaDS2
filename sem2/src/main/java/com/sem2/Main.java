package com.sem2;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import com.sem2.Generators.AbstractGenerator;
import com.sem2.Generators.ExponentialGenerator;
import com.sem2.Generators.TriangularGenerator;
import com.sem2.SimCore.FurnitureCompany;

public class Main {
    public static void main(String[] args) {
        Random seed = new Random();
        
        // Triangular distribution parameters
        double min = 300, max = 900, mode = 500;
        TriangularGenerator triangularGen = new TriangularGenerator(seed, min, max, mode);
        
        // Exponential distribution parameter (mean)
        double mean = 2.0;
        ExponentialGenerator exponentialGen = new ExponentialGenerator(seed, mean);
        
        generateSamples(triangularGen, "triangular_data.txt");
        generateSamples(exponentialGen, "exponential_data.txt"); 
         /* TicketsSim sim = new TicketsSim(Double.MAX_VALUE);
        sim.runSimulation(1); */
        FurnitureCompany sim = new FurnitureCompany(7171200,1,2,16);
        sim.setTimeFactor(0.0);
        sim.runSimulation(1000);/* 
        Statistic stat = new Statistic();
        stat.setSum(1.8921920632621964E11);
        stat.setSumSquare(1.0089383261992968E16);
        stat.setCount(3961550);
        System.out.println("Average: " + stat.getAverage());
        System.out.println("Reliability interval: <" + stat.getConfidenceInterval95()[0]+ ", " + stat.getConfidenceInterval95()[1]+ ">");
         */
    }
    
    private static void generateSamples(AbstractGenerator<Double> generator, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            for (int i = 0; i < 50000; i++) {
                writer.write(generator.getSample() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}