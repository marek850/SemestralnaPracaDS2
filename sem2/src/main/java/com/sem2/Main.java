package com.sem2;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import com.sem2.Generators.AbstractGenerator;
import com.sem2.Generators.ExponentialGenerator;
import com.sem2.Generators.TriangularGenerator;

public class Main {
    public static void main(String[] args) {
        Random seed = new Random();
        
        // Triangular distribution parameters
        double min = 1.0, max = 10.0, mode = 5.0;
        TriangularGenerator triangularGen = new TriangularGenerator(seed, min, max, mode);
        
        // Exponential distribution parameter (mean)
        double mean = 2.0;
        ExponentialGenerator exponentialGen = new ExponentialGenerator(seed, mean);
        
        generateSamples(triangularGen, "triangular_data.txt");
        generateSamples(exponentialGen, "exponential_data.txt");
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