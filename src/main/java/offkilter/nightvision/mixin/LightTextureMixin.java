package offkilter.nightvision.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import offkilter.nightvision.client.NightVisionClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightTexture.class)
public class LightTextureMixin
{
    @Redirect(method = "updateLightTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;hasEffect(Lnet/minecraft/world/effect/MobEffect;)Z"))
    private boolean fakeMobEffect(LocalPlayer instance, MobEffect mobEffect)
    {
        if (mobEffect == MobEffects.NIGHT_VISION && NightVisionClient.valueIsOn) {
            return true;
        } else {
            return instance.hasEffect(mobEffect);
        }
    }
    @Redirect(method = "updateLightTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;getNightVisionScale(Lnet/minecraft/world/entity/LivingEntity;F)F"))
    private float fakeNightVisionScale(LivingEntity livingEntity, float f)
    {
        if (NightVisionClient.valueIsOn) {
            return 1.0f;
        } else {
            return GameRenderer.getNightVisionScale(livingEntity, f);
        }
    }
}
