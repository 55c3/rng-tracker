# RNG Tracker Mod
RNG display and manipulator mod for Minecraft.

## Installation

Download and install the [fabric mod loader](https://fabricmc.net/use/) and the [fabric api](https://www.curseforge.com/minecraft/mc-mods/fabric-api).

Put the fabric-api `.jar` and [this mod `.jar`](https://github.com/55c3/rng-tracker/releases) file into your minecraft mods folder.

## Usage

Use the `/rng` command.
- `/rng display on/off` to see the current world RNG value displayed in the top left corner.
- `/rng compile` to compile and use a random function written in a Minecraft book (see below).
- `/rng default` to reset the rng algorithm to the default one.

Rename any mob to `rng` using a nametag to see that entities current RNG value.

The call number refers to the number of times the next random number was requested from that particular RNG in a single tick.

## Custom RNG Functions (Beta)
While holding a written book, type `/rng compile`.

In the written book, define the Java function body of:

`int next(AtomicLong seed, int bits)`

You must set seed to a new value and return some rng value. A 48 bit mask is defined for you. Example,

```
long s = (seed.get() + 1) & mask;
seed.set(s);
return (int) (s & ((1L << bits) - 1));
```

RNGs must past a basic check to help prevent hangs but don't rely on it.

In your mods folder a `DynamicRandom.java` and `DynamicRandom.class` will be created. Be careful when copying someone else's RNG function as it will execute arbitrary Java code.

## Build from Source
- clone the [repository](https://github.com/55c3/rng-tracker.git)
- `gradlew build`