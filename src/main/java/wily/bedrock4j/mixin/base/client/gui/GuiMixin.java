package wily.bedrock4j.mixin.base.client.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.AttackIndicatorStatus;
//? if >=1.21 {
import net.minecraft.client.DeltaTracker;
//?}
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
//? if >1.20.2 {
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.scores.PlayerScoreEntry;
//?} else {
/*import net.minecraft.world.scores.Score;
*///?}
//? if <1.21.5 {
import com.mojang.blaze3d.platform.GlStateManager;
//?}
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.FactoryAPIClient;
import wily.factoryapi.base.client.FactoryGuiGraphics;
import wily.factoryapi.base.client.UIAccessor;
import wily.factoryapi.base.config.FactoryConfig;
import wily.bedrock4j.client.BedrockOptions;
import wily.bedrock4j.config.LegacyCommonOptions;
import wily.bedrock4j.util.BedrockSprites;
import wily.bedrock4j.client.screen.ControlTooltip;
import wily.bedrock4j.util.ScreenUtil;
import wily.factoryapi.util.FactoryGuiElement;

import java.util.*;


@Mixin(Gui.class)
public abstract class GuiMixin implements ControlTooltip.Event {
    @Shadow @Final
    private Minecraft minecraft;
    //? if >1.20.2 {
    @Final
    @Shadow
    private static Comparator<? super PlayerScoreEntry> SCORE_DISPLAY_ORDER;
    //?}

    @Shadow public abstract Font getFont();


    @Shadow private long healthBlinkTime;

    @Redirect(method = "renderSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getPopTime()I"))
    public int renderSlot(ItemStack instance) {
        return 0;
    }

    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;", ordinal = 1))
    public Object renderCrosshair(OptionInstance<AttackIndicatorStatus> instance) {
        return FactoryConfig.hasCommonConfigEnabled(LegacyCommonOptions.legacyCombat) ? AttackIndicatorStatus.OFF : instance.get();
    }

    @Redirect(method = /*? if >=1.20.5 {*/"renderItemHotbar"/*?} else {*//*"renderHotbar"*//*?}*/, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;"))
    public Object renderItemHotbar(OptionInstance<AttackIndicatorStatus> instance) {
        return FactoryConfig.hasCommonConfigEnabled(LegacyCommonOptions.legacyCombat) ? AttackIndicatorStatus.OFF : instance.get();
    }


    //? if >1.20.1 {
    @Inject(method = /*? if >=1.20.5 {*/"renderItemHotbar"/*?} else {*//*"renderHotbar"*//*?}*/, at = @At(value = "INVOKE", target = /*? if <1.21.2 {*/"Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"/*?} else {*//*"Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIII)V"*//*?}*/, ordinal = 1))
    private void renderHotbarSelection(/*? if <1.20.5 {*//*float f, *//*?}*/GuiGraphics guiGraphics/*? if >=1.20.5 {*/, DeltaTracker deltaTracker/*?}*/, CallbackInfo ci) {
        FactoryGuiGraphics.of(guiGraphics).blitSprite(BedrockSprites.HOTBAR_SELECTION,24,24,0,23,guiGraphics.guiWidth() / 2 - 91 - 1 + minecraft.player.getInventory()./*? if <1.21.5 {*/selected/*?} else {*//*getSelectedSlot()*//*?}*/ * 20, guiGraphics.guiHeight(), 0,24, 1);
    }
    //?} else {
    /*@Redirect(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V", ordinal = 1))
    private void renderHotbarSelection(GuiGraphics instance, ResourceLocation resourceLocation, int i, int j, int k, int l, int m, int n) {
        instance.blit(resourceLocation, i, j, k, l, m, 24);
    }
    *///?}
    @Inject(method = "displayScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void displayScoreboardSidebar(GuiGraphics guiGraphics, Objective objective, CallbackInfo ci) {
        ci.cancel();
        if (minecraft.screen != null) return;
        Scoreboard scoreboard = objective.getScoreboard();
        //? if >1.20.2 {
        NumberFormat numberFormat = objective.numberFormatOrDefault(StyledFormat.SIDEBAR_DEFAULT);
        List<PlayerScoreEntry> scores = scoreboard.listPlayerScores(objective).stream().filter((playerScoreEntry) -> !playerScoreEntry.isHidden()).sorted(SCORE_DISPLAY_ORDER).limit(15L).toList();
        //?} else {
        /*List<Score> scores = scoreboard.getPlayerScores(objective).stream().filter((scorex) -> scorex.getOwner() != null && !scorex.getOwner().startsWith("#")).limit(15L).toList();;
        *///?}
        Component component = objective.getDisplayName();
        int i = this.getFont().width(component);
        int k = this.getFont().width(": ");
        int j = Math.max(i,scores.stream().mapToInt(lv-> {
            int w = getFont().width(/*? if >1.20.2 {*/lv.formatValue(numberFormat)/*?} else {*//*"" + ChatFormatting.RED + lv.getScore()*//*?}*/);
            return this.getFont().width(PlayerTeam.formatNameForTeam(scoreboard.getPlayersTeam(lv./*? if >1.20.2 {*/owner/*?} else {*//*getOwner*//*?}*/()), /*? if >1.20.2 {*/lv.ownerName()/*?} else {*//*Component.literal(lv.getOwner())*//*?}*/)) + (w > 0 ? k + w : 0);
        }).max().orElse(0));

        Objects.requireNonNull(this.getFont());
        int l = scores.size() * 9;
        int m = guiGraphics.guiHeight() / 2 + l / 3;
        int x = guiGraphics.guiWidth() - 8;
        int o = x - j;
        int p = x + 2;
        Objects.requireNonNull(this.getFont());
        int s = m - scores.size() * 9;
        Objects.requireNonNull(this.getFont());
        ScreenUtil.renderPointerPanel(guiGraphics, o - 6,s - 16,j + 12, scores.size() * 9 + 22);
        Font var18 = this.getFont();
        int var10003 = o + j / 2 - i / 2;
        Objects.requireNonNull(this.getFont());
        guiGraphics.drawString(var18, component, var10003, s - 9, -1, false);

        for(int t = 0; t < scores.size(); ++t) {
            /*? if >1.20.2 {*/PlayerScoreEntry/*?} else {*//*Score*//*?}*/ lv = scores.get(t);
            x = scores.size() - t;
            Objects.requireNonNull(this.getFont());
            int u = m - x * 9;
            guiGraphics.drawString(this.getFont(), PlayerTeam.formatNameForTeam(scoreboard.getPlayersTeam(lv./*? if >1.20.2 {*/owner/*?} else {*//*getOwner*//*?}*/()), /*? if >1.20.2 {*/lv.ownerName()/*?} else {*//*Component.literal(lv.getOwner())*//*?}*/), o, u, -1, false);
            Component score = /*? if >1.20.2 {*/lv.formatValue(numberFormat)/*?} else {*//*Component.literal("" + ChatFormatting.RED + lv.getScore())*//*?}*/;
            guiGraphics.drawString(this.getFont(), score, p - getFont().width(score), u, -1, false);
        }
    }

   //? if >=1.20.5 {
   @Shadow protected abstract boolean isExperienceBarVisible();
    @Inject(method = "renderExperienceLevel", at = @At("HEAD"), cancellable = true)
    public void renderExperienceLevel(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        ci.cancel();
        if (!FactoryGuiElement.EXPERIENCE_BAR.isVisible((UIAccessor) this)) {
            return;
        }
        int i = this.minecraft.player.experienceLevel;
        if (this.isExperienceBarVisible() && i > 0) {
            ScreenUtil.prepareHUDRender(guiGraphics);
            this.minecraft.getProfiler().push("expLevel");
            String string = "" + i;
            int j = (guiGraphics.guiWidth() - this.getFont().width(string)) / 2;
            guiGraphics.drawString(this.getFont(), string, j, guiGraphics.guiHeight() - 31 - 8, 8453920, true);
            this.minecraft.getProfiler().pop();
            ScreenUtil.finalizeHUDRender(guiGraphics);
        }
    }
    //?}
    @Inject(method = "renderExperienceBar", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableBlend()V"))
    public void renderExperienceBar(GuiGraphics guiGraphics, int i, CallbackInfo ci) {
        FactoryGuiGraphics.of(guiGraphics).blitSprite(BedrockSprites.EXPERIENCE_BAR_NUB, i + 5, guiGraphics.guiHeight() - 32 + 3, 172, 5);
    }

    @Redirect(method = "renderExperienceBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V"))
    public void renderExperienceBar(GuiGraphics instance, ResourceLocation arg, int i, int j, int k, int l, int m, int n, int o, int p) {
        FactoryGuiGraphics.of(instance).blitSprite(arg, m, instance.guiHeight() - 32 + 3, o, 5);
    }

    @Inject(method = "renderSavingIndicator", at = @At("HEAD"), cancellable = true)
    public void renderAutoSaveIndicator(GuiGraphics guiGraphics,/*? if >=1.21 {*/ DeltaTracker deltaTracker,/*?}*/ CallbackInfo ci) {
        ci.cancel();
    }

    @Redirect(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"))
    private boolean tick(ItemStack instance) {
        return !ScreenUtil.getTooltip(instance).equals(ScreenUtil.getTooltip(minecraft.player.getInventory()./*? if <1.21.5 {*/getSelected()/*?} else {*//*getSelectedItem()*//*?}*/));
    }

    @Redirect(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;"))
    private Object tick(OptionInstance<Double> instance) {
        return Math.min(ScreenUtil.getSelectedItemTooltipLines(),ScreenUtil.getTooltip(minecraft.player.getInventory()./*? if <1.21.5 {*/getSelected()/*?} else {*//*getSelectedItem()*//*?}*/).size()) * instance.get();
    }

    @Inject(method = /*? if forge || neoforge {*/ /*"renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V" *//*?} else {*/"renderSelectedItemName"/*?}*/, at = @At("HEAD"), cancellable = true/*? if forge || neoforge {*//*, remap = false*//*?}*/)
    public void renderSelectedItemName(GuiGraphics guiGraphics, /*? if forge || neoforge {*/ /*int shift, *//*?}*/ CallbackInfo ci) {
        ci.cancel();
        ScreenUtil.renderHUDTooltip(guiGraphics, /*? if forge || neoforge {*/ /*shift *//*?} else {*/0/*?}*/);
    }

    //? if >=1.20.5 || fabric {
    @Redirect(method=/*? if neoforge {*//*"renderHealthLevel"*//*?} else {*/"renderPlayerHealth"/*?}*/, at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/Gui;healthBlinkTime:J", opcode = Opcodes.PUTFIELD, ordinal = 1))
    private void renderPlayerHealth(Gui instance, long value) {
        healthBlinkTime = value - 6;
    }
    //?}

}
