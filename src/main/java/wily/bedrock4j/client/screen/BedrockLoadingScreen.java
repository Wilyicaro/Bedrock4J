package wily.bedrock4j.client.screen;

import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import wily.bedrock4j.util.BedrockSprites;
import wily.factoryapi.base.client.FactoryGuiGraphics;
import wily.factoryapi.base.client.UIAccessor;
import wily.factoryapi.util.FactoryScreenUtil;
import wily.bedrock4j.Bedrock4JClient;
import wily.bedrock4j.client.CommonColor;
import wily.bedrock4j.client.LegacyTip;
import wily.bedrock4j.client.LegacyTipManager;
import wily.bedrock4j.util.LegacyComponents;
import wily.bedrock4j.util.ScreenUtil;

import java.util.function.BooleanSupplier;

import static wily.bedrock4j.util.BedrockSprites.LOADING_BACKGROUND;
import static wily.bedrock4j.util.BedrockSprites.LOADING_BAR;

public class BedrockLoadingScreen extends Screen implements LegacyLoading {
    public static LegacyTip actualLoadingTip;
    private int progress;
    private Component loadingHeader;
    private Component loadingStage;
    private boolean genericLoading;
    private UIAccessor accessor = UIAccessor.of(this);

    protected RandomSource random = RandomSource.create();

    public BedrockLoadingScreen() {
        super(GameNarrator.NO_TITLE);
    }
    public BedrockLoadingScreen(Component loadingHeader, Component loadingStage) {
        this();
        this.setLoadingHeader(loadingHeader);
        this.setLoadingStage(loadingStage);
    }

    public void prepareRender(Minecraft minecraft,int width, int height,Component loadingHeader, Component loadingStage, int progress, boolean genericLoading){
        resize(minecraft,width,height);
        this.minecraft = minecraft;
        this.accessor = UIAccessor.of(minecraft.screen);
        this.setLoadingHeader(accessor.getElementValue("loadingHeader.component",loadingHeader,Component.class));
        this.setLoadingStage(accessor.getElementValue("loadingStage.component",loadingStage,Component.class));
        this.setProgress(accessor.getInteger("progress",progress));
        this.setGenericLoading(accessor.getBoolean("genericLoading",genericLoading));
    }

    public LegacyTip getLoadingTip(){
        if (usingLoadingTips.isEmpty()){
            if (LegacyTipManager.loadingTips.isEmpty()) return null;
            else usingLoadingTips.addAll(LegacyTipManager.loadingTips);
        }
        if (actualLoadingTip == null) {
            int i = random.nextInt(usingLoadingTips.size());
            actualLoadingTip = usingLoadingTips.get(i).get();
            usingLoadingTips.remove(i);
        }else if (actualLoadingTip.visibility == Toast.Visibility.HIDE) {
            actualLoadingTip = null;
            return getLoadingTip();
        }
        return actualLoadingTip;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    //? if >1.20.1 {
    @Override
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        ScreenUtil.renderDefaultBackground(accessor, guiGraphics, true, true);
    }
    //?}
    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        FactoryScreenUtil.disableDepthTest();
        //? if <=1.20.1
        /*ScreenUtil.renderDefaultBackground(accessor, guiGraphics, true, true, false);*/
        super.render(guiGraphics, i, j, f);
        int x = width / 2 - 145;
        int y = height / 2 - 50;
        FactoryScreenUtil.enableBlend();
        FactoryGuiGraphics.of(guiGraphics).blitSprite(BedrockSprites.PANEL_TRANSLUCENT_DIALOG, x, y, 290, 100);
        FactoryScreenUtil.disableBlend();
        if (getProgress() != -1) {
            FactoryGuiGraphics.of(guiGraphics).blitSprite(BedrockSprites.EXPERIENCE_BAR_BACKGROUND, width / 2 - 125, y + 76, 251, 5);
            FactoryGuiGraphics.of(guiGraphics).blitSprite(BedrockSprites.EXPERIENCE_BAR_CURRENT, width / 2 - 125, y + 76, Math.round(251 * Math.max(0, Math.min(getProgress() / 100F, 1))), 5);
            FactoryScreenUtil.enableBlend();
            FactoryGuiGraphics.of(guiGraphics).blitSprite(BedrockSprites.EXPERIENCE_BAR_NUB, width / 2 - 125 + 5, y + 76, 241, 5);
            FactoryScreenUtil.disableBlend();
        }
        if (getLoadingHeader() != null) {
            guiGraphics.drawString(minecraft.font, getLoadingHeader(), (width - minecraft.font.width(getLoadingHeader())) / 2, y + 10, CommonColor.INVENTORY_GRAY_TEXT.get(), false);
        }
        LegacyTip tip = getLoadingTip();
        if (tip != null) tip.tipLabel.withShadow(false).withColor(CommonColor.TIP_TEXT.get()).withPos(x + 10, y + 28).render(guiGraphics, i, j, f);
        FactoryScreenUtil.enableDepthTest();
    }

    public static BedrockLoadingScreen getDimensionChangeScreen(ClientLevel lastLevel, ClientLevel newLevel){
        boolean lastOd = isOtherDimension(lastLevel);
        boolean od = isOtherDimension(newLevel);
        BedrockLoadingScreen screen = new BedrockLoadingScreen(od || lastOd ? Component.translatable("bedrock4j.menu." + (lastOd ? "leaving" : "entering"), LegacyComponents.getDimensionName((lastOd ? lastLevel : newLevel).dimension())) : Component.empty(), Component.empty());
        if (od || lastOd) screen.setGenericLoading(true);
        return screen;
    }

    public static boolean isOtherDimension(Level level){
        return level != null && level.dimension() != Level.OVERWORLD;
    }

    public static BedrockLoadingScreen getRespawningScreen(BooleanSupplier levelReady){
        long createdTime = Util.getMillis();
        BedrockLoadingScreen screen = new BedrockLoadingScreen(LegacyComponents.RESPAWNING, Component.empty()){
            @Override
            public void tick() {
                if (levelReady.getAsBoolean() || Util.getMillis() - createdTime >= 30000) minecraft.setScreen(null);
            }

            @Override
            public boolean isPauseScreen() {
                return false;
            }
        };
        screen.setGenericLoading(true);
        return screen;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public Component getLoadingHeader() {
        return loadingHeader;
    }

    public void setLoadingHeader(Component loadingHeader) {
        this.loadingHeader = loadingHeader;
    }

    public Component getLoadingStage() {
        return loadingStage;
    }

    public void setLoadingStage(Component loadingStage) {
        this.loadingStage = loadingStage;
    }

    public boolean isGenericLoading() {
        return genericLoading;
    }

    public void setGenericLoading(boolean genericLoading) {
        this.genericLoading = genericLoading;
    }
}
