package wily.bedrock4j.mixin.base;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.base.client.FactoryGuiGraphics;
import wily.factoryapi.base.client.MinecraftAccessor;
import wily.bedrock4j.Bedrock4J;
import wily.bedrock4j.Bedrock4JClient;
import wily.bedrock4j.client.BedrockOptions;
import wily.bedrock4j.client.LegacyResourceManager;
import wily.bedrock4j.util.ScreenUtil;
import wily.factoryapi.util.FactoryScreenUtil;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntSupplier;

import static wily.bedrock4j.client.LegacyResourceManager.INTROS;

@Mixin(LoadingOverlay.class)
public abstract class LoadingOverlayMixin extends Overlay {
    @Unique
    private static boolean finishedIntro = false;
    @Unique
    private static boolean loadIntroLocation = false;

    @Shadow @Final private ReloadInstance reload;

    @Shadow @Final private Consumer<Optional<Throwable>> onFinish;

    @Shadow @Final private Minecraft minecraft;
    @Shadow private long fadeOutStart;
    @Shadow @Final private boolean fadeIn;
    @Shadow private long fadeInStart;
    @Shadow @Final private static IntSupplier BRAND_BACKGROUND;
    @Shadow @Final private static ResourceLocation MOJANG_STUDIOS_LOGO_LOCATION;
    @Unique
    private long initTime;
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        if (BedrockOptions.legacyIntroAndLoading.get()) {
            ci.cancel();
            if (!loadIntroLocation) {
                initTime = Util.getMillis();
                loadIntroLocation = true;
                LegacyResourceManager.registerIntroLocations(minecraft.getResourceManager());
            }
            float timer = (Util.getMillis() - initTime) / 3200f;
            if (!finishedIntro && Bedrock4JClient.canSkipIntro(timer) && reload.isDone()) finishedIntro = true;
            if (!finishedIntro) {
                guiGraphics.fill(RenderType.guiOverlay(), 0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight(), BRAND_BACKGROUND.getAsInt());
                int n = guiGraphics.guiWidth() / 2;
                int s = guiGraphics.guiHeight() / 2;
                double d = Math.min((double)guiGraphics.guiWidth() * (double)0.75F, guiGraphics.guiHeight()) / 6;
                int t = (int)(d * (double)0.5F);
                double e = d * (double)4.0F;
                int u = (int)(e * (double)0.5F);
                FactoryScreenUtil.enableBlend();
                FactoryGuiGraphics.of(guiGraphics).blit(MOJANG_STUDIOS_LOGO_LOCATION, n - u, s - t, u, (int)d, -0.0625F, 0.0F, 120, 60, 120, 120);
                FactoryGuiGraphics.of(guiGraphics).blit(MOJANG_STUDIOS_LOGO_LOCATION, n, s - t, u, (int)d, 0.0625F, 60.0F, 120, 60, 120, 120);
                FactoryScreenUtil.disableBlend();
            }

            if (finishedIntro) {
                float h;
                long m = Util.getMillis();
                if (this.fadeIn && this.fadeInStart == -1L) {
                    this.fadeInStart = m;
                }
                float g = this.fadeOutStart > -1L ? (float) (m - this.fadeOutStart) / 1000.0f : -1.0f;
                h = this.fadeInStart > -1L ? (float) (m - this.fadeInStart) / 500.0f : -1.0f;
                if ((MinecraftAccessor.getInstance().hasGameLoaded() && reload.isDone()) && minecraft.screen != null)
                    this.minecraft.screen.renderWithTooltip(guiGraphics, 0, 0, f);
                else {
                    FactoryGuiGraphics.of(guiGraphics).blit(ScreenUtil.LOADING_BACKGROUND, 0, 0, 0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight(), guiGraphics.guiWidth(), guiGraphics.guiHeight());
                }
                if (g < 1.0f && !reload.isDone() && MinecraftAccessor.getInstance().hasGameLoaded())
                    ScreenUtil.drawGenericLoading(guiGraphics, (guiGraphics.guiWidth() - 75) / 2, (guiGraphics.guiHeight() - 75) / 2);

                if (g >= 2.0f)
                    this.minecraft.setOverlay(null);

                if (this.fadeOutStart == -1L && this.reload.isDone() && (!this.fadeIn || h >= 2.0f)) {
                    try {
                        this.reload.checkExceptions();
                        this.onFinish.accept(Optional.empty());
                    } catch (Throwable throwable) {
                        this.onFinish.accept(Optional.of(throwable));
                    }
                    this.fadeOutStart = Util.getMillis();
                    if (this.minecraft.screen != null) {
                        this.minecraft.screen.init(this.minecraft, guiGraphics.guiWidth(), guiGraphics.guiHeight());
                    }
                }
            }
        }
    }
}
