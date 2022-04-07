package com.github.spongeguy.villageculture.mixin;

import com.github.spongeguy.villageculture.IRiderGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.github.spongeguy.villageculture.entity.ai.goal.FindRiderGoal;

import java.util.List;

@Mixin(LlamaEntity.class)
public abstract class LlamaEntityMixin extends AbstractDonkeyEntity implements IRiderGoal {
    @Shadow private @Nullable LlamaEntity follower;

    public LlamaEntityMixin(EntityType<? extends LlamaEntity> type, World world) {
        super(type, world);
    }

    private static final Ingredient vcltr$RIDER_INGREDIENT = Ingredient.ofItems(Items.BEETROOT);

    public boolean vcltr$isRiderItem(ItemStack stack) {
        return stack.isOf(Items.BEETROOT);
    }

    private static final int vcltr$FIND_RIDER_COOLDOWN = 400;

    int vcltr$findRiderTicks = 0;

    @Inject(method = "initGoals", at = @At("TAIL"))
    protected void injectGoals(CallbackInfo ci) {
        this.goalSelector.add(2, new FindRiderGoal(this, 1.75));
        //this.goalSelector.add(5, new TemptGoal(this, 1.25, Ingredient.ofItems(Items.BEETROOT), false));
    }

    @Override
    public void vcltr$setFindRiderTicks(int t) {
        this.vcltr$findRiderTicks = t;
    }

    public int vcltr$getFindRiderTicks() {
        return vcltr$findRiderTicks;
    }


    public boolean vcltr$hasRider() {
        return this.hasPassengers();
    }

    public boolean vcltr$isFindingRider() {
        return vcltr$findRiderTicks > 0;
    }

    private void vcltr$spawnLlamaRiderParticles() {
        for (int i = 0; i < 20; i++) {
            double d = this.random.nextGaussian() * 0.02;
            double e = this.random.nextGaussian() * 0.02;
            double f = this.random.nextGaussian() * 0.02;
            this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), d, e, f);
        }
    }

    @Override
    public void mobTick() {
        super.mobTick();
        if(!vcltr$isFindingRider()) {
            vcltr$setFindRiderTicks(0);
        }
        if(vcltr$findRiderTicks > 0) {
            if (vcltr$findRiderTicks % 10 == 0) {
                vcltr$spawnLlamaRiderParticles();
            }
            vcltr$findRiderTicks--;
        }
        if(vcltr$hasRider()) {
            vcltr$setFindRiderTicks(0);
        }

    }

    @Override
    public void tickMovement() {
        super.tickMovement();
    }

    public void vcltr$enterFindRiderMode() {
        vcltr$setFindRiderTicks(vcltr$FIND_RIDER_COOLDOWN);
    }


    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        // interact with beetroot while llama does not have rider
        if (this.vcltr$isRiderItem(itemStack) && !vcltr$hasRider() && !vcltr$isFindingRider() && !this.world.isClient) {
            this.playSound(SoundEvents.ENTITY_LLAMA_ANGRY, 1, 1f);
            vcltr$spawnLlamaRiderParticles();

            vcltr$enterFindRiderMode();
            this.emitGameEvent(GameEvent.MOB_INTERACT, this.getCameraBlockPos());
            return ActionResult.SUCCESS;
        }

        // interact with beetroot while llama has rider
        if (this.vcltr$isRiderItem(itemStack) && vcltr$hasRider()) {
            vcltr$spawnLlamaRiderParticles();

            List<Entity> list = getPassengerList();
            for (Entity rider : list) {
                rider.stopRiding();
            }

        }
        return super.interactMob(player, hand);
    }
}

