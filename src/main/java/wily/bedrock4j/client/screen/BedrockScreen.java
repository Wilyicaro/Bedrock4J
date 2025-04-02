package wily.bedrock4j.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.network.chat.Component;
import wily.factoryapi.base.client.UIAccessor;
import wily.bedrock4j.client.controller.Controller;
import wily.bedrock4j.init.LegacyRegistries;
import wily.bedrock4j.util.ScreenUtil;

public class BedrockScreen extends Screen implements Controller.Event, ControlTooltip.Event {
    public Screen parent;
    protected final UIAccessor accessor = UIAccessor.of(this);
    //? if >=1.20.5 {
    public static final PanoramaRenderer PANORAMA_RENDERER = PANORAMA;
    //?}

    protected BedrockScreen(Component component) {
        super(component);
    }

    protected BedrockScreen(Screen parent, Component component) {
        this(component);
        this.parent = parent;
    }

    public void renderDefaultBackground(GuiGraphics guiGraphics, int i, int j, float f){
        ScreenUtil.renderDefaultBackground(accessor, guiGraphics, true);
    }
    @Override
    //? if >1.20.1 {
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        renderDefaultBackground(guiGraphics,i,j,f);
    }
    //?} else {
    /*public void renderBackground(GuiGraphics guiGraphics) {
        renderDefaultBackground(guiGraphics,0,0,0);
    }
    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        renderDefaultBackground(guiGraphics, i, j, f);
        super.render(guiGraphics, i, j, f);
    }
    *///?}

    @Override
    public void onClose() {
        ScreenUtil.playSimpleUISound(LegacyRegistries.BACK.get(),1.0f);
        this.minecraft.setScreen(parent);
    }
}
