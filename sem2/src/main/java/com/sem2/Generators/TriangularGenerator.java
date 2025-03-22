package com.sem2.Generators;

import java.util.Random;

public class TriangularGenerator extends AbstractGenerator<Double> {
    private final double min;
    private final double max;
    private final double mid;

    public TriangularGenerator(Random seedGenerator, double min, double max, double mid) {
        super(seedGenerator);
        if (min > mid || mid > max) {
            throw new IllegalArgumentException("Mode must be between min and max");
        }
        this.min = min;
        this.max = max;
        this.mid = mid;
    }
    
    @Override
    public Double getSample() {
        double rand = probabilityGenerator.nextDouble();
        double c = (mid - min) / (max - min);
        if (rand < c) {
            return min + Math.sqrt(rand * (max - min) * (mid - min));
        } else {
            return max - Math.sqrt((1 - rand) * (max - min) * (max - mid));
        }
    }
}
