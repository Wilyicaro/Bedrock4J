package wily.bedrock4j.mixin.base;

//? if <1.21.2 {
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.player.Input;
//?} else {
/*import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.player.ClientInput;
*///?}
import net.minecraft.client.player.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import wily.factoryapi.FactoryAPIClient;
import wily.bedrock4j.Bedrock4JClient;
import wily.bedrock4j.client.BedrockOptions;
import wily.bedrock4j.client.controller.BindingState;
import wily.bedrock4j.client.controller.ControllerBinding;

@Mixin(KeyboardInput.class)
public class KeyboardControllerInputMixin extends /*? if <1.21.2 {*/Input/*?} else {*//*ClientInput*//*?}*/ {
    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/KeyboardInput;calculateImpulse(ZZ)F", ordinal = 0))
    private float calculateForwardImpulse(float original) {
        BindingState.Axis leftStick = Bedrock4JClient.controllerManager.getButtonState(ControllerBinding.LEFT_STICK);
        return leftStick.pressed && (/*? if <1.21.2 {*/up || down/*?} else {*//*keyPresses.forward() || keyPresses.backward()*//*?}*/ ) ? BedrockOptions.smoothMovement.get() && (BedrockOptions.forceSmoothMovement.get() || FactoryAPIClient.hasModOnServer) ? -leftStick.getSmoothY() : (leftStick.y > 0 ? -1 : 1) : original;
    }
    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/KeyboardInput;calculateImpulse(ZZ)F", ordinal = 1))
    private float calculateLeftImpulse(float original) {
        BindingState.Axis leftStick = Bedrock4JClient.controllerManager.getButtonState(ControllerBinding.LEFT_STICK);
        return leftStick.pressed && (/*? if <1.21.2 {*/left || right/*?} else {*//*keyPresses.left() || keyPresses.right()*//*?}*/) ? BedrockOptions.smoothMovement.get() && (BedrockOptions.forceSmoothMovement.get() || FactoryAPIClient.hasModOnServer) ?  -leftStick.getSmoothX() : (leftStick.x > 0 ? -1 : 1) : original;
    }
    //? if >=1.21.5 {
    /*@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec2;normalized()Lnet/minecraft/world/phys/Vec2;"))
    private Vec2 normalize(Vec2 instance, Operation<Vec2> original) {
        return Legacy4JClient.controllerManager.isControllerTheLastInput() && FactoryAPIClient.hasModOnServer ? instance : original.call(instance);
    }
    *///?}
}
