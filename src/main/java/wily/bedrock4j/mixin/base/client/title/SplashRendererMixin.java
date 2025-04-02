package wily.bedrock4j.mixin.base.client.title;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SplashRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.bedrock4j.util.ScreenUtil;

@Mixin(SplashRenderer.class)
public class SplashRendererMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V", shift = At.Shift.AFTER))
    public void renderAfterScale(GuiGraphics guiGraphics, int i, Font font, int j, CallbackInfo ci) {
        guiGraphics.pose().scale(1.5f, 1.5f, 1.5f);
        if (Minecraft.getInstance().getResourceManager().getResource(ScreenUtil.MINECRAFT).isPresent()) guiGraphics.pose().translate(0,8,0);
    }
}
