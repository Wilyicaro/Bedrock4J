package wily.bedrock4j.client.screen;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.resources.ResourceLocation;
import wily.factoryapi.base.client.FactoryGuiGraphics;
import wily.factoryapi.util.FactoryScreenUtil;
import wily.bedrock4j.util.BedrockSprites;

public class LegacyScrollRenderer {
    public static final ResourceLocation[] SCROLLS = new ResourceLocation[]{BedrockSprites.SCROLL_UP, BedrockSprites.SCROLL_DOWN, BedrockSprites.SCROLL_LEFT, BedrockSprites.SCROLL_RIGHT};
    public final long[] lastScrolled = new long[4];
    public long lastScroll = 0;
    public ScreenDirection lastDirection;

    public void updateScroll(ScreenDirection direction){
        lastDirection = direction;
        lastScroll = (lastScrolled[direction.ordinal()] = Util.getMillis());
    }
    public void renderScroll(GuiGraphics graphics, ScreenDirection direction, int x, int y){
        boolean h = direction.getAxis() == ScreenAxis.HORIZONTAL;
        FactoryScreenUtil.enableBlend();
        long l = lastScrolled[direction.ordinal()];
        if (l > 0) {
            float f = (Util.getMillis() - l) / 320f;
            float fade = Math.min(1.0f,f < 0.5f ? 1 - f * 2f : (f - 0.5f) * 2f);
            FactoryGuiGraphics.of(graphics).setColor(1.0f,1.0f,1.0f, fade);
        }
        FactoryGuiGraphics.of(graphics).blitSprite(SCROLLS[direction.ordinal()],x,y, h ? 6 : 13, h ? 11 : 7);
        if (l > 0)
            FactoryGuiGraphics.of(graphics).clearColor();
        FactoryScreenUtil.disableBlend();
    }

}
