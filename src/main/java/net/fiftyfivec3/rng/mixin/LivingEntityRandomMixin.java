// Based on KaptainWutax's WorldRandomMixin

package net.fiftyfivec3.rng.mixin;

import net.fiftyfivec3.rng.utils.RandomTracker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(LivingEntity.class)
public abstract class LivingEntityRandomMixin extends Entity {
    private RandomTracker tracker;
    public LivingEntityRandomMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void ctor(EntityType<?> type, World world, CallbackInfo ci) {
        try {
            Field randomField = Entity.class.getDeclaredFields()[42];
            randomField.setAccessible(true);
            tracker = new RandomTracker();
            randomField.set(this, tracker); //Override with our custom random.
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    private void nextTick(CallbackInfo ci) {
        if (this.world instanceof ServerWorld && this.hasCustomName() && this.getCustomName().asString().startsWith("rng")) {
            long seed = tracker.getSeed();
            String data = String.format("rng: %20d (calls %d)", seed, tracker.calls);
            this.setCustomName(new LiteralText(data));
            this.setCustomNameVisible(true);
            tracker.calls = 0;
        }
    }
}
