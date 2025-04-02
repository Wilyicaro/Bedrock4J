package wily.bedrock4j.mixin.base;

import net.minecraft.client.tutorial.Tutorial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.bedrock4j.client.BedrockOptions;

@Mixin(Tutorial.class)
public class TutorialMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void tick(CallbackInfo ci) {
        if (!BedrockOptions.vanillaTutorial.get()) ci.cancel();
    }
}
