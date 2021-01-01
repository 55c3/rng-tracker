# RNG Tracker Mod
RNG display and manipulator mod for Minecraft.

## Installation

Download and install the [fabric mod loader](https://fabricmc.net/use/) and the [fabric api](https://www.curseforge.com/minecraft/mc-mods/fabric-api).

Put the fabric-api `.jar` and this mod `.jar` file into your minecraft mods folder.

## Usage

Type `/worldrng` to see the current world RNG value displayed in the top left corner.

Rename any mob to `rng` using a nametag to see that entities current RNG value.

The call number refers to the number of times the next random number was requested from that particular RNG in a single tick.

## Upcoming
- custom RNG function support

## Build from Source
- clone the [repository](https://github.com/55c3/rng-tracker.git)
- `gradlew build`