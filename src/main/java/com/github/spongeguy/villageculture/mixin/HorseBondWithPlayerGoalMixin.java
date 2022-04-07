package com.github.spongeguy.villageculture.mixin;

import net.minecraft.entity.ai.goal.HorseBondWithPlayerGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HorseBondWithPlayerGoal.class)
public abstract class HorseBondWithPlayerGoalMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void tickInject(CallbackInfo ci) {

    }
}
