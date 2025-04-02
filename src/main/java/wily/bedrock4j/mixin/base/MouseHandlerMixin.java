package wily.bedrock4j.mixin.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.bedrock4j.Bedrock4JClient;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Inject(method = "xpos", at = @At("HEAD"), cancellable = true)
    private void xpos(CallbackInfoReturnable<Double> cir) {
        if (Bedrock4JClient.controllerManager.isCursorDisabled) cir.setReturnValue(-1d);
    }

    @Inject(method = "xpos", at = @At("HEAD"), cancellable = true)
    private void ypos(CallbackInfoReturnable<Double> cir) {
        if (Bedrock4JClient.controllerManager.isCursorDisabled) cir.setReturnValue(-1d);
    }

    @Inject(method = {"onMove","onScroll"}, at = @At("HEAD"), cancellable = true)
    private void onMove(long l, double d, double e, CallbackInfo ci) {
        onChange(l,ci);
    }

    @Inject(method = "onPress", at = @At("HEAD"), cancellable = true)
    private void onPress(long l, int i, int j, int k, CallbackInfo ci) {
        onChange(l,ci);
    }

    @Inject(method = "releaseMouse", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/InputConstants;grabOrReleaseMouse(JIDD)V", shift = At.Shift.AFTER))
    private void releaseMouse(CallbackInfo ci){
        Bedrock4JClient.controllerManager.enableCursorAndScheduleReset();
        Bedrock4JClient.controllerManager.updateCursorInputMode();
    }

    @Unique
    private void onChange(long window, CallbackInfo ci){
        if (window == Minecraft.getInstance().getWindow().getWindow()) {
            if (!Bedrock4JClient.controllerManager.isControllerSimulatingInput) Bedrock4JClient.controllerManager.setControllerTheLastInput(false);
            if (Bedrock4JClient.controllerManager.isCursorDisabled) {
                if (!Bedrock4JClient.controllerManager.getCursorMode().isNever())
                    Bedrock4JClient.controllerManager.enableCursor();
                else ci.cancel();
            }
        }
    }

    //? if >=1.21.5 {
    /*@Inject(method = "getScaledXPos(Lcom/mojang/blaze3d/platform/Window;)D", at = @At("HEAD"), cancellable = true)
    private void getScaledXPos(CallbackInfoReturnable<Double> cir) {
        if (Legacy4JClient.controllerManager.isCursorDisabled) cir.setReturnValue(-1d);
    }

    @Inject(method = "getScaledYPos(Lcom/mojang/blaze3d/platform/Window;)D", at = @At("HEAD"), cancellable = true)
    private void getScaledYPos(CallbackInfoReturnable<Double> cir) {
        if (Legacy4JClient.controllerManager.isCursorDisabled) cir.setReturnValue(-1d);
    }
    *///?}
}
