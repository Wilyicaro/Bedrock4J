package wily.bedrock4j.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import wily.factoryapi.base.client.FactoryGuiGraphics;
import wily.factoryapi.util.FactoryScreenUtil;
import wily.bedrock4j.Bedrock4J;
import wily.bedrock4j.client.CommonColor;
import wily.bedrock4j.util.ScreenUtil;

import java.util.function.Consumer;
import java.util.function.Function;

public class Toggle extends AbstractButton {
    public static final ResourceLocation[] SPRITES = new ResourceLocation[]{Bedrock4J.createModLocation( "widget/toggle_off"), Bedrock4J.createModLocation( "widget/toggle_on"), Bedrock4J.createModLocation( "widget/toggle_off_hover"), Bedrock4J.createModLocation( "widget/toggle_on_hover")};
    public static final ResourceLocation TICK = Bedrock4J.createModLocation( "widget/tick");
    protected final Function<Boolean,Component> message;
    protected Function<Boolean,Tooltip> tooltip;
    private final Consumer<Toggle> onPress;

    public boolean selected;

    public Toggle(int i, int j, int width, int height, boolean initialState, Function<Boolean, Component> message, Function<Boolean, Tooltip> tooltip, Consumer<Toggle> onPress) {
        super(i, j, width, height, message.apply(false));
        this.selected = initialState;
        this.message = message;
        this.tooltip = tooltip;
        this.onPress = onPress;
        setTooltip(tooltip.apply(selected));
    }
    public Toggle(int i, int j, int width, boolean initialState, Function<Boolean, Component> message, Function<Boolean, Tooltip> tooltip, Consumer<Toggle> onPress){
        this(i,j,width,16,initialState,message,tooltip,onPress);
    }
    public Toggle(int i, int j, boolean initialState, Function<Boolean, Component> message, Function<Boolean, Tooltip> tooltip, Consumer<Toggle> onPress){
        this(i,j,200,initialState,message,tooltip,onPress);
    }
    @Override
    public void onPress() {
        selected = !selected;
        onPress.accept(this);
        setTooltip(tooltip.apply(selected));
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        setAlpha(active ? 1.0F : 0.5F);
        Minecraft minecraft = Minecraft.getInstance();
        FactoryGuiGraphics.of(guiGraphics).setColor(1.0f, 1.0f, 1.0f, this.alpha);
        FactoryScreenUtil.enableBlend();
        FactoryScreenUtil.enableDepthTest();
        FactoryGuiGraphics.of(guiGraphics).blitSprite(SPRITES[(isHoveredOrFocused() ? 2 : 0) + (selected ? 1 : 0)], this.getX(), this.getY(), 30, 16);
        FactoryGuiGraphics.of(guiGraphics).setColor(1.0f, 1.0f, 1.0f, 1.0F);
        this.renderString(guiGraphics, minecraft.font, CommonColor.WHITE.get());
    }


    @Override
    public Component getMessage() {
        return message.apply(selected);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
        if (this.active) {
            Component component = createNarrationMessage();
            if (this.isFocused()) {
                narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.cycle_button.usage.focused", component));
            } else {
                narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.cycle_button.usage.hovered", component));
            }
        }
    }

    @Override
    public void renderString(GuiGraphics guiGraphics, Font font, int i) {
        ScreenUtil.renderScrollingString(guiGraphics, font, this.getMessage(), this.getX() + 34, this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), i, false);
    }
}
