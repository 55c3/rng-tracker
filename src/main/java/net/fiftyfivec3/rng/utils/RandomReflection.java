package net.fiftyfivec3.rng.utils;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class RandomReflection {
    public static Field seed;

    static {
        try {
            seed = Random.class.getDeclaredField("seed");
            seed.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static AtomicLong getSeed(Random random) {
        try {
            return (AtomicLong) seed.get(random);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return  null;
    }
}
