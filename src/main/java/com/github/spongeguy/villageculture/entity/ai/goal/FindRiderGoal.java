package com.github.spongeguy.villageculture.entity.ai.goal;

import com.github.spongeguy.villageculture.IRiderGoal;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.world.World;
import java.util.List;

public class FindRiderGoal extends Goal{
    private static final TargetPredicate vcltr$VALID_RIDER_PREDICATE = TargetPredicate.createNonAttackable().setBaseMaxDistance(32.0).ignoreVisibility();
    private final Class<? extends VillagerEntity> entityClass;
    protected final AbstractDonkeyEntity mob;
    protected final World world;
    private final double speed;
    protected VillagerEntity rider;

    public FindRiderGoal(AbstractDonkeyEntity mob, double speed) {
        this.mob = mob;
        this.world = mob.world;
        this.speed = speed;
        entityClass = VillagerEntity.class;
    }

    @Override
    public boolean canStart() {
        if (!this.mob.hasPassengers() && ((IRiderGoal)this.mob).vcltr$isFindingRider()) {
            return true;
        }
        return false;
    }

    @Override
    public void start() {
        List<? extends VillagerEntity> list = this.world.getTargets(
                VillagerEntity.class,
                vcltr$VALID_RIDER_PREDICATE,
                this.mob,
                this.mob.getBoundingBox().expand(32.0)
        );

        System.out.println(list);
        for (VillagerEntity target : list) {
            if ((!target.hasPassengers() && !target.hasVehicle())) {
                rider = target;
            }
        }
    }

    @Override
    public void tick() {
        if (rider != null && canStart() && !rider.hasVehicle()) {
            this.mob.getLookControl().lookAt(rider, 10.0f, this.mob.getMaxLookPitchChange());
            this.mob.getNavigation().startMovingTo(rider, speed);
            if (this.mob.squaredDistanceTo(rider) < 3) {
                rider.startRiding(this.mob, false);
            }
        }

    }

    @Override
    public boolean shouldContinue() {
        if(rider == null) {
            return false;
        }
        if(rider.hasVehicle()) {
            return false;
        }
        return this.canStart();
    }

    @Override
    public void stop() {
        ((IRiderGoal)this.mob).vcltr$setFindRiderTicks(0);
        this.mob.getNavigation().stop();
        rider = null;
    }

}
