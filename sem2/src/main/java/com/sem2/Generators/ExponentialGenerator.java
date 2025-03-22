package com.sem2.Generators;

import java.util.Random;

public class ExponentialGenerator extends AbstractGenerator<Double> {
    private final double lambda;

    public ExponentialGenerator(Random seedGenerator, double mean) {
        super(seedGenerator);
        if (mean <= 0) {
            throw new IllegalArgumentException("Mean must be greater than 0");
        }
        this.lambda = 1.0 / mean;
    }
    
    @Override
    public Double getSample() {
        double rand = probabilityGenerator.nextDouble();
        return -Math.log(1 - rand) / lambda;
    }
}
