package wily.bedrock4j.mixin.base;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.bedrock4j.Bedrock4JClient;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @Inject(method = "keyPress", at = @At("HEAD"))
    public void keyPress(long l, int i, int j, int k, int m, CallbackInfo ci) {
        if (l == Minecraft.getInstance().getWindow().getWindow() && !Bedrock4JClient.controllerManager.isControllerSimulatingInput) Bedrock4JClient.controllerManager.setControllerTheLastInput(false);
    }
}
