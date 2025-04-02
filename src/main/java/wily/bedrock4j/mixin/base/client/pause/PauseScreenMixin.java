package wily.bedrock4j.mixin.base.client.pause;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlayerSkinWidget;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.bedrock4j.util.BedrockSprites;
import wily.factoryapi.base.client.FactoryGuiGraphics;
import wily.factoryapi.base.client.UIAccessor;
import wily.bedrock4j.Bedrock4JClient;
import wily.bedrock4j.client.BedrockOptions;
import wily.bedrock4j.client.screen.*;
import wily.bedrock4j.util.LegacyComponents;
import wily.bedrock4j.util.ScreenUtil;

import java.util.Collections;
import java.util.List;

@Mixin(PauseScreen.class)
public class PauseScreenMixin extends Screen implements ControlTooltip.Event,RenderableVList.Access{
    protected PauseScreenMixin(Component component) {
        super(component);
    }

    @Unique
    protected RenderableVList renderableVList;
    @Unique
    private List<RenderableVList> renderableVLists;
    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (renderableVList.keyPressed(i)) return true;
        return super.keyPressed(i, j, k);
    }
    @Unique
    private void setAutoSave(int autoSave, Button button){
        BedrockOptions.autoSaveInterval.set(autoSave);
        BedrockOptions.autoSaveInterval.save();
        button.setMessage(BedrockOptions.autoSaveInterval.get() > 0 ? LegacyComponents.DISABLE_AUTO_SAVE : LegacyComponents.SAVE_GAME);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initScreen(CallbackInfo ci){
        renderableVList = new RenderableVList(this).layoutSpacing(l->2);
        renderableVLists = Collections.singletonList(renderableVList);
        renderableVList.addRenderables(Button.builder(Component.translatable("menu.returnToGame"), button -> {
                    this.minecraft.setScreen(null);
                    this.minecraft.mouseHandler.grabMouse();
                }).size(148,30).build(),Button.builder(Component.translatable("menu.options"), button -> this.minecraft.setScreen(new HelpAndOptionsScreen(this))).size(148,30).build(),Button.builder(Component.translatable("gui.advancements"), button -> this.minecraft.setScreen(new AdvancementsScreen(minecraft.getConnection().getAdvancements(),  this))).size(148,30).build(),Button.builder(Component.translatable("bedrock4j.menu.exit_and_save"), button -> minecraft.setScreen(new ExitConfirmationScreen(this))).size(148,30).build()
        );
        minecraft = Minecraft.getInstance();
    }

    //? if >1.20.1 {
    @Inject(method = "renderBackground",at = @At("HEAD"), cancellable = true)
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        ci.cancel();
        ScreenUtil.renderDefaultBackground(UIAccessor.of(this), guiGraphics, false);
    }
    //?}

    @Inject(method = "render",at = @At("HEAD"), cancellable = true)
    public void render(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        ci.cancel();
        //? if <=1.20.1
        /*ScreenUtil.renderDefaultBackground(UIAccessor.of(this), guiGraphics);*/
        super.render(guiGraphics, i, j, f);
        int sideBarWidth = width / 3 + 10;
        FactoryGuiGraphics.of(guiGraphics).blitSprite(BedrockSprites.PAUSE_SIDE_BAR, width - sideBarWidth, 0, sideBarWidth, height);
    }

    @Inject(method = "init",at = @At("HEAD"), cancellable = true)
    public void init(CallbackInfo ci) {
        ci.cancel();
        renderableVListInit();
        PlayerSkinWidget playerSkinWidget = addRenderableWidget(new PlayerSkinWidget(60, 80, minecraft.getEntityModels(), minecraft.getSkinManager().lookupInsecure(minecraft.getGameProfile())));
        playerSkinWidget.setPosition(width / 2 - 10, height / 2 - 40);
        addRenderableWidget(HelpAndOptionsScreen.CHANGE_SKIN.createButtonBuilder(this).bounds(playerSkinWidget.getX() - 8, playerSkinWidget.getY() + playerSkinWidget.getHeight() + 5, 80, 30).build());
        addRenderableOnly(((guiGraphics, i, j, f) -> ScreenUtil.drawUsernameWithBackground(guiGraphics, playerSkinWidget.getX() + playerSkinWidget.getWidth() / 2, playerSkinWidget.getY() - 10)));
    }

    @Override
    public void renderableVListInit() {
        renderableVList.init(width / 4 - 74,this.height / 3 + 10,148,0);
    }

    @Override
    public RenderableVList getRenderableVList() {
        return renderableVList;
    }

    @Override
    public List<RenderableVList> getRenderableVLists() {
        return renderableVLists;
    }
}
