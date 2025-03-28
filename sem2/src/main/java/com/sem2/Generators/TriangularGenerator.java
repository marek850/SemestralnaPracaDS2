package com.sem2.Generators;

import java.util.Random;

public class TriangularGenerator extends AbstractGenerator<Double> {
    private final double min;
    private final double max;
    private final double mode;

    public TriangularGenerator(Random seedGenerator, double min, double max, double mode) {
        super(seedGenerator);
        if (min > mode || mode > max) {
            throw new IllegalArgumentException("Mode must be between min and max");
        }
        this.min = min;
        this.max = max;
        this.mode = mode;
    }
    
    @Override
    public Double getSample() {
        double rand = probabilityGenerator.nextDouble();
        double c = (mode - min) / (max - min);
        if (rand < c) {
            return min + Math.sqrt(rand * (max - min) * (mode - min));
        } else {
            return max - Math.sqrt((1 - rand) * (max - min) * (max - mode));
        }
    }
}
