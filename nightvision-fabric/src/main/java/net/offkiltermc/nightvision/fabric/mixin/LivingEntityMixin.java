package net.offkiltermc.nightvision.fabric.mixin;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.offkiltermc.nightvision.fabric.client.NightVisionClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Unique
    private static final MobEffectInstance nightVision = new MobEffectInstance(MobEffects.NIGHT_VISION, -1, 0);

    @Inject(method = "hasEffect", at=@At("HEAD"), cancellable = true)
    private void fakeHasEffect(MobEffect mobEffect, CallbackInfoReturnable<Boolean> cir)
    {
        if (mobEffect == MobEffects.NIGHT_VISION && NightVisionClient.valueIsOn) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getEffect", at=@At("HEAD"), cancellable = true)
    private void fakeGetEffect(MobEffect mobEffect, CallbackInfoReturnable<MobEffectInstance> cir)
    {
        if (mobEffect == MobEffects.NIGHT_VISION && NightVisionClient.valueIsOn) {
            cir.setReturnValue(nightVision);
        }
    }
}
