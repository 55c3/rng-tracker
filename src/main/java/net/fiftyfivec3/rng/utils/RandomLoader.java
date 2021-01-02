package net.fiftyfivec3.rng.utils;

import net.fiftyfivec3.rng.Config;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.atomic.AtomicLong;

public class RandomLoader {
    private static final String className = "DynamicRandom";

    public static boolean compile(String code, PlayerEntity player) {
        Constructor<?> constructor = createFunction(code, player);
        System.out.println("Constructed");
        if (constructor == null) return false;
        RandomInterface test = create(constructor);
        System.out.println("RI made");
        if (test == null) return false;
        AtomicLong seed = new AtomicLong(0x55c3);
        int v1 = test.next(seed, 32);
        int v2 = test.next(seed, 32);
        System.out.println("Testing: " + v1 + " " + v2 + " " + seed.get() + ".");
        if (seed.get() == 0x55c3 || v1 == v2) return false;
        RandomTracker.function = test;
        System.out.println("Passed");
        return true;
    }

    private static Constructor<?> createFunction(String code, PlayerEntity player) {
        try {
            String directory = new File(RandomLoader.class.getProtectionDomain().getCodeSource().getLocation()
                    .toURI()).getParent() + '/';
            String fileName = directory + className + ".java";
            FileWriter fw = new FileWriter(fileName, false);
            BufferedWriter bf = new BufferedWriter(fw);
            bf.write("import java.util.concurrent.atomic.AtomicLong;\nimport net.fiftyfivec3.rng.utils.RandomInterface;\npublic class DynamicRandom implements RandomInterface {\npublic int next(AtomicLong seed, int bits) {\n");
            bf.write(code);
            bf.write("\n}\n}");
            bf.flush();
            bf.close();

            String jar = new File(RandomLoader.class.getProtectionDomain().getCodeSource().getLocation()
                    .toURI()).getPath();

            String[] args = {"javac", fileName, "-cp",(Config.isWindows ? ".;" : ".:") + jar};
            Process process = Runtime.getRuntime().exec(args);

            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = errorReader.readLine()) != null) {
                player.sendMessage(new LiteralText(line).formatted(Formatting.RED), false);
                System.out.println(line);
            }

            URL[] cp = {new File(directory).toURI().toURL()};
            URLClassLoader loader = new URLClassLoader(cp, RandomLoader.class.getClassLoader());

            return loader.loadClass(className).getConstructor();
        } catch (URISyntaxException | IOException | ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static RandomInterface create(Constructor<?> constructor) {
        try {
            return (RandomInterface) constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
