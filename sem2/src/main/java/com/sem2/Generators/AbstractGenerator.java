package com.sem2.Generators;
import java.util.*;

public abstract class AbstractGenerator<T> {
    protected final Random seedGenerator;
    protected final Random probabilityGenerator;
    protected final List<Random> intervalGenerators;

    public AbstractGenerator(Random seedGenerator) {
        this.seedGenerator = seedGenerator;
        this.probabilityGenerator = new Random(seedGenerator.nextLong());
        this.intervalGenerators = new ArrayList<>();
    }

    public abstract T getSample();
}