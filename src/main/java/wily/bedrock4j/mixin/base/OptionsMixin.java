package wily.bedrock4j.mixin.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.bedrock4j.Bedrock4JClient;
import wily.bedrock4j.client.GlobalPacks;
import wily.bedrock4j.client.BedrockOptions;
import wily.bedrock4j.client.PackAlbum;

import java.util.function.BooleanSupplier;

@Mixin(Options.class)
public abstract class OptionsMixin {

    @Shadow protected Minecraft minecraft;

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;<init>(Ljava/lang/String;ILjava/lang/String;)V", ordinal = 5),index = 0)
    protected String initKeyCraftingName(String string) {
        return "bedrock4j.key.inventory";
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;<init>(Ljava/lang/String;Lnet/minecraft/client/OptionInstance$TooltipSupplier;Lnet/minecraft/client/OptionInstance$CaptionBasedToString;Lnet/minecraft/client/OptionInstance$ValueSet;Ljava/lang/Object;Ljava/util/function/Consumer;)V", ordinal = /*? if >=1.21.2 {*//*7*//*?} else {*/6/*?}*/),index = 4)
    protected Object initChatSpacingOption(Object object) {
        return 1.0d;
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/ToggleKeyMapping;<init>(Ljava/lang/String;ILjava/lang/String;Ljava/util/function/BooleanSupplier;)V", ordinal = 0),index = 3)
    protected BooleanSupplier initKeyShift(BooleanSupplier booleanSupplier) {
        return ()-> (minecraft == null || minecraft.player == null || (!minecraft.player.getAbilities().flying && minecraft.player.getVehicle() == null && (!minecraft.player.isInWater() || minecraft.player.onGround()))) && (booleanSupplier.getAsBoolean() && !Bedrock4JClient.controllerManager.isControllerTheLastInput() || BedrockOptions.controllerToggleCrouch.get() && Bedrock4JClient.controllerManager.isControllerTheLastInput());
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/ToggleKeyMapping;<init>(Ljava/lang/String;ILjava/lang/String;Ljava/util/function/BooleanSupplier;)V", ordinal = 1),index = 3)
    protected BooleanSupplier initKeySprint(BooleanSupplier booleanSupplier) {
        return ()-> booleanSupplier.getAsBoolean() && !Bedrock4JClient.controllerManager.isControllerTheLastInput() || BedrockOptions.controllerToggleSprint.get() && Bedrock4JClient.controllerManager.isControllerTheLastInput();
    }

    @Inject(method = "loadSelectedResourcePacks",at = @At("HEAD"), cancellable = true)
    private void loadSelectedResourcePacks(PackRepository packRepository, CallbackInfo ci){
        PackAlbum.init();
        GlobalPacks.STORAGE.load();
        GlobalPacks.globalResources.get().applyPacks(packRepository, PackAlbum.getDefaultResourceAlbum().packs());
        PackAlbum.updateSavedResourcePacks();
        ci.cancel();
    }

}
