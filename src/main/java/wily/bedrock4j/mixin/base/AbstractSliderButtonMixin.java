package wily.bedrock4j.mixin.base;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.base.client.FactoryGuiGraphics;
import wily.factoryapi.util.FactoryScreenUtil;
import wily.bedrock4j.client.screen.ControlTooltip;
import wily.bedrock4j.util.LegacyComponents;
import wily.bedrock4j.util.BedrockSprites;
import wily.bedrock4j.util.ScreenUtil;

@Mixin(AbstractSliderButton.class)
public abstract class AbstractSliderButtonMixin extends AbstractWidget implements ControlTooltip.ActionHolder {


    @Shadow protected double value;

    @Shadow public boolean canChangeValue;

    public AbstractSliderButtonMixin(int i, int j, int k, int l, Component component) {
        super(i, j, k, l, component);
    }
    @Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true)
    public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        ci.cancel();
        alpha = active ? 1.0f : 0.8f;
        Minecraft minecraft = Minecraft.getInstance();
        FactoryGuiGraphics.of(guiGraphics).setColor(1.0f, 1.0f, 1.0f, alpha);
        FactoryScreenUtil.enableBlend();
        FactoryScreenUtil.enableDepthTest();
        FactoryGuiGraphics.of(guiGraphics).blitSprite(BedrockSprites.SLIDER, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        if (isHoveredOrFocused()) FactoryGuiGraphics.of(guiGraphics).blitSprite(BedrockSprites.HIGHLIGHTED_SLIDER, this.getX() - 1, this.getY() - 1, this.getWidth() + 2, this.getHeight() + 2);
        FactoryGuiGraphics.of(guiGraphics).blitSprite(isHovered() ? BedrockSprites.SLIDER_HANDLE_HIGHLIGHTED : BedrockSprites.SLIDER_HANDLE, this.getX() + (int)(this.value * (double)(this.width - 8)), this.getY(), 8, this.getHeight());
        FactoryGuiGraphics.of(guiGraphics).setColor(1.0f, 1.0f, 1.0f, 1.0f);
        int k = ScreenUtil.getDefaultTextColor(!isHoveredOrFocused());
        this.renderScrollingString(guiGraphics, minecraft.font, 2, k | Mth.ceil(this.alpha * 255.0f) << 24);
    }

    @Override
    public @Nullable Component getAction(Context context) {
        return isFocused() && context instanceof KeyContext c && c.key() == InputConstants.KEY_RETURN ? canChangeValue ? LegacyComponents.LOCK : LegacyComponents.UNLOCK : null;
    }
}
