package wily.bedrock4j.mixin.base;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.*;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import wily.bedrock4j.client.screen.LegacyLoading;

import static wily.bedrock4j.Bedrock4JClient.bedrockLoadingScreen;

@Mixin(/*? if <1.20.5 {*//*GenericDirtMessageScreen*//*?} else {*/GenericMessageScreen/*?}*/.class)
public class LegacyLoadingMessageScreenMixin extends Screen implements LegacyLoading {
    protected LegacyLoadingMessageScreenMixin(Component component) {
        super(component);
    }

    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        bedrockLoadingScreen.prepareRender(minecraft,width, height,getTitle(), null,0,false);
        bedrockLoadingScreen.render(guiGraphics,i,j,f);
    }

    @Override
    public int getProgress() {
        return bedrockLoadingScreen.getProgress();
    }

    @Override
    public void setProgress(int progress) {
        bedrockLoadingScreen.setProgress(progress);
    }

    @Override
    public Component getLoadingHeader() {
        return bedrockLoadingScreen.getLoadingHeader();
    }

    @Override
    public void setLoadingHeader(Component loadingHeader) {
        bedrockLoadingScreen.setLoadingHeader(loadingHeader);
    }

    @Override
    public Component getLoadingStage() {
        return bedrockLoadingScreen.getLoadingStage();
    }

    @Override
    public void setLoadingStage(Component loadingStage) {
        bedrockLoadingScreen.setLoadingStage(loadingStage);
    }

    @Override
    public boolean isGenericLoading() {
        return bedrockLoadingScreen.isGenericLoading();
    }

    @Override
    public void setGenericLoading(boolean genericLoading) {
        bedrockLoadingScreen.setGenericLoading(genericLoading);
    }
}
