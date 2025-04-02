package wily.bedrock4j.mixin.base;

import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.bedrock4j.Bedrock4JClient;

@Mixin(Style.class)
public class StyleMixin {
    @Inject(method = "getFont", at = @At("RETURN"), cancellable = true)
    private void getFont(CallbackInfoReturnable<ResourceLocation> cir){
        if(Bedrock4JClient.defaultFontOverride != null && cir.getReturnValue().equals(Style.DEFAULT_FONT)) cir.setReturnValue(Bedrock4JClient.defaultFontOverride);
    }
}
