package net.fiftyfivec3.rng.utils;

import java.util.concurrent.atomic.AtomicLong;

public interface RandomInterface {
    public static final long mask = (1L << 48) - 1;
    int next(AtomicLong seed, int bits);
}
