package com.github.spongeguy.villageculture.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LlamaEntity.class)
public abstract class vcltr_LlamaEntityMixin extends PathAwareEntity {
    public vcltr_LlamaEntityMixin(EntityType<? extends LlamaEntity> type, World world) {
        super(type, world);
    }


    @Inject(method = "initGoals", at = @At("TAIL"))
    protected void injectGoals(CallbackInfo ci) {
        this.goalSelector.add(5, new TemptGoal(this, 1.25, Ingredient.ofItems(Items.BEETROOT), false));
    }

}
