// Based on KaptainWutax's WorldRandomMixin rng counting code

package net.fiftyfivec3.rng.utils;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class RandomTracker extends Random {
    private static final long multiplier = 0x5DEECE66DL;
    private static final long mask = (1L << 48) - 1;
    private static final long addend = 0xBL;

    public int calls = 0;
    private AtomicLong internalSeed = null;
    public static RandomInterface function = null;

    public RandomTracker() {
        super();
        if (internalSeed == null) internalSeed = new AtomicLong(RandomReflection.getSeed(this).get());
    }
    public RandomTracker(long seed) {
        super(seed);
        if (internalSeed == null) internalSeed = new AtomicLong((seed ^ multiplier) & mask);
    }

    @Override
    protected int next(int bits) {
        calls++;
        if (function != null) { // not thread-safe
            return function.next(internalSeed, bits);
        } else {
            return super.next(bits);
        }
    }

    @Override
    public synchronized void setSeed(long seed) {
        super.setSeed(seed);
        if (this.internalSeed == null) internalSeed = new AtomicLong();
        this.internalSeed.set((seed ^ multiplier) & mask);
    }

    public long getSeed() {
        try {
            return function == null ? ((AtomicLong) RandomReflection.seed.get(this)).get() : internalSeed.get();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
