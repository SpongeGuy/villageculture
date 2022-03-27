package com.github.spongeguy.villageculture.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class vcltr_ItemEntityMixin extends Entity {
    public vcltr_ItemEntityMixin(EntityType<? extends ItemEntity> type, World world) {
        super(type, world);
    }

    @Shadow
    private int itemAge;

    boolean vcltr_itemDying = false;

    // data tracking for item lifespan (RAND * RANGE + MIN)
    private static final TrackedData<Integer> vcltr_trackedItemLifespan = DataTracker.registerData(ItemEntity.class, TrackedDataHandlerRegistry.INTEGER);
    @Inject(method = "initDataTracker", at = @At("TAIL"))
    protected void injectDataTracking(CallbackInfo ci) {
        this.dataTracker.startTracking(vcltr_trackedItemLifespan, (int)(Math.random() * (100)) + 5899);
    }

    // sound for item death
    public void playWarpAwaySound(World world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.NEUTRAL, 0.7f, 1.4f + world.random.nextFloat() * 0.2f);
    }

    int vcltr_itemLifespan = this.getDataTracker().get(vcltr_trackedItemLifespan); // accessible version of tracked item lifespan

    @Inject(method = "tick", at = @At("HEAD"))
    protected void injectTick(CallbackInfo ci) {
        if (itemAge >= vcltr_itemLifespan - 199) vcltr_itemDying = true;

        if(vcltr_itemDying) {
            if (itemAge == vcltr_itemLifespan - 1) { // plays warp sound at item despawn
                playWarpAwaySound(world, new BlockPos(this.getX(), this.getY(), this.getZ()));
            }

            for (int i = 0; i < 2; ++i) { // adds portal particles if item is > age 5899
                this.world.addParticle(ParticleTypes.PORTAL,
                        this.getParticleX(0.5),
                        this.getRandomBodyY(),
                        this.getParticleZ(0.5),
                        (this.random.nextDouble() - 0.5) * 2.0,
                        -this.random.nextDouble(),
                        (this.random.nextDouble() - 0.5) * 2.0);
            }

            if (!this.world.isClient() && itemAge == vcltr_itemLifespan) {
                this.discard();
            }
        }
    }
}
