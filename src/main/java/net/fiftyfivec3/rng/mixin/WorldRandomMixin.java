// Based on KaptainWutax's WorldRandomMixin rng counting code

package net.fiftyfivec3.rng.mixin;

import net.fiftyfivec3.rng.utils.RandomTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class WorldRandomMixin extends World {
    protected WorldRandomMixin(MutableWorldProperties properties, RegistryKey<World> registryRef,
                               DimensionType dimensionType, Supplier<Profiler> profiler, boolean isClient,
                               boolean debugWorld, long seed) {
        super(properties, registryRef, dimensionType, profiler, isClient, debugWorld, seed);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void ctor(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session,
                      ServerWorldProperties properties, RegistryKey<World> registryKey, DimensionType dimensionType,
                      WorldGenerationProgressListener worldGenerationProgressListener, ChunkGenerator chunkGenerator,
                      boolean debugWorld, long l, List<Spawner> list, boolean bl, CallbackInfo ci) {
        try {
            Field randomField = World.class.getDeclaredFields()[19];
            randomField.setAccessible(true);
            randomField.set(this, new RandomTracker()); //Override with our custom random.
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tickStart(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        ((RandomTracker) this.random).calls = 0;
    }
}

