package wily.bedrock4j.mixin.base;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.bedrock4j.client.screen.LegacyLoading;

import static wily.bedrock4j.Bedrock4JClient.bedrockLoadingScreen;

@Mixin({LevelLoadingScreen.class, ProgressScreen.class, ReceivingLevelScreen.class, ConnectScreen.class})
public class LegacyLoadingScreenMixin extends Screen implements LegacyLoading {
    protected LegacyLoadingScreenMixin(Component component) {
        super(component);
    }

    Screen self(){
        return this;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(null);
    }


    @Inject(method = "render",at = @At("HEAD"), cancellable = true)
    public void render(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        ci.cancel();
        Component lastLoadingHeader = null;
        Component lastLoadingStage = null;
        boolean genericLoading = false;
        int progress = 0;
        if (self() instanceof ReceivingLevelScreen) progress = -1;
        if (self() instanceof LevelLoadingScreen loading) {
            lastLoadingHeader = Component.translatable("bedrock4j.connect.initializing");
            lastLoadingStage = Component.translatable("bedrock4j.loading_spawn_area");
            progress = loading.progressListener.getProgress();
        }
        if (self() instanceof ProgressScreen p) {
            lastLoadingHeader = p.header;
            lastLoadingStage = p.stage;
            if (minecraft.level != null && minecraft.level.dimension() != Level.OVERWORLD){
                genericLoading = true;
            }
        }
        if (self() instanceof ConnectScreen p) {
            lastLoadingHeader = p.status;
        }
        bedrockLoadingScreen.prepareRender(minecraft,width, height,lastLoadingHeader,lastLoadingStage,progress,genericLoading);
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
