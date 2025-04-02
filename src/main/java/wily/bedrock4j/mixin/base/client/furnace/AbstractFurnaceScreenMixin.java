package wily.bedrock4j.mixin.base.client.furnace;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
//? if <1.21.2 {
import net.minecraft.client.gui.screens.recipebook.AbstractFurnaceRecipeBookComponent;
//?}
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.base.client.FactoryGuiGraphics;
import wily.factoryapi.base.client.UIAccessor;
import wily.bedrock4j.client.CommonColor;
import wily.bedrock4j.client.BedrockOptions;
import wily.bedrock4j.client.screen.ControlTooltip;
import wily.bedrock4j.inventory.LegacySlotDisplay;
import wily.bedrock4j.util.LegacyComponents;
import wily.bedrock4j.util.BedrockSprites;
import wily.factoryapi.util.FactoryScreenUtil;

import static wily.bedrock4j.util.BedrockSprites.ARROW;

@Mixin(AbstractFurnaceScreen.class)
public abstract class AbstractFurnaceScreenMixin<T extends AbstractFurnaceMenu> extends AbstractContainerScreen<T> {
    //? if <1.21.2 {
    @Shadow private boolean widthTooNarrow;

    @Shadow @Final public AbstractFurnaceRecipeBookComponent recipeBookComponent;

    @Unique
    private ImageButton recipeButton;
    //?}
    public AbstractFurnaceScreenMixin(T abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

    @Override
    public void added() {
        super.added();
        ControlTooltip.Renderer.of(this).replace(3,i-> i, c-> hoveredSlot == null || hoveredSlot.getItem().isEmpty() || hoveredSlot.container != minecraft.player.getInventory() ? c : menu.canSmelt(hoveredSlot.getItem()) ? LegacyComponents.MOVE_INGREDIENT : /*? if <1.21.2 {*/AbstractFurnaceBlockEntity.isFuel/*?} else {*//*minecraft.level.fuelValues().isFuel*//*?}*/(hoveredSlot.getItem()) ? LegacyComponents.MOVE_FUEL : c);
    }
    //? if >1.20.1 {
    @Override
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        renderBg(guiGraphics, f, i, j);
    }
    //?} else {
    /*@Shadow @Final private static ResourceLocation RECIPE_BUTTON_LOCATION;
    @Override
    public void renderBackground(GuiGraphics guiGraphics) {
    }
    *///?}
    @Inject(method = "init",at = @At("HEAD"), cancellable = true)
    public void init(CallbackInfo ci) {
        ci.cancel();
        inventoryLabelX = 7;
        inventoryLabelY = 77;
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        titleLabelY = 5;
        super.init();
        for (int i = 0; i < menu.slots.size(); i++) {
            Slot s = menu.slots.get(i);
            if (i == 0) {
                LegacySlotDisplay.override(s, 51, 15);
            } else if (i == 1) {
                LegacySlotDisplay.override(s, 51,53);
            } else if (i == 2) {
                LegacySlotDisplay.override(s, 109, 30,new LegacySlotDisplay(){
                    public int getWidth() {
                        return 26;
                    }
                });
            } else if (i < menu.slots.size() - 9) {
                LegacySlotDisplay.override(s, s.x,87 + (s.getContainerSlot() - 9) / 9 * 18);
            } else {
                LegacySlotDisplay.override(s, s.x,144);
            }
        }
        //? if <1.21.2 {
        this.widthTooNarrow = this.width < 379;
        this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.menu);
        if (BedrockOptions.showVanillaRecipeBook.get()) {
            this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
            recipeButton = this.addRenderableWidget(new ImageButton(this.leftPos + 20, this.height / 2 - 49, 20, 18, /*? if >1.20.1 {*/RecipeBookComponent.RECIPE_BUTTON_SPRITES/*?} else {*//*0, 19, RECIPE_BUTTON_LOCATION*//*?}*/, (button) -> {
                this.recipeBookComponent.toggleVisibility();
                this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
                button.setPosition(this.leftPos + 20, this.height / 2 - 49);
            }));
            if (recipeBookComponent.isVisible()) recipeButton.setFocused(true);
        }
        else if (recipeBookComponent.isVisible()) recipeBookComponent.toggleVisibility();
        //?}
    }

    //? if >=1.21.2 {
    /*@Inject(method = "getRecipeBookButtonPosition", at = @At("HEAD"), cancellable = true)
    protected void getRecipeBookButtonPosition(CallbackInfoReturnable<ScreenPosition> cir){
        cir.setReturnValue(new ScreenPosition(this.leftPos + 49, topPos + 49));
    }
    *///?}

    @Inject(method = "renderBg",at = @At("HEAD"), cancellable = true)
    public void renderBg(GuiGraphics guiGraphics, float f, int i, int j, CallbackInfo ci) {
        ci.cancel();
        FactoryGuiGraphics.of(guiGraphics).blitSprite(UIAccessor.of(this).getElementValue("imageSprite", BedrockSprites.PANEL, ResourceLocation.class),leftPos,topPos,imageWidth,imageHeight);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(leftPos + 52,topPos + 36,0);
        FactoryGuiGraphics.of(guiGraphics).blitSprite(BedrockSprites.LIT,0,0, 13, 13);
        if (menu.isLit()) {
            int n = Mth.ceil(/*? if >1.20.1 {*/menu.getLitProgress()/*?} else {*//*Mth.clamp(menu.getLitProgress()/ 13f,0,1)*//*?}*/ * 12.0f) + 1;
            FactoryGuiGraphics.of(guiGraphics).blitSprite(BedrockSprites.LIT_PROGRESS, 13, 13, 0, 13 - n, 0, 13 - n, 13, n);
        }
        guiGraphics.pose().popPose();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(leftPos + 77,topPos + 30,0);
        FactoryGuiGraphics.of(guiGraphics).blitSprite(ARROW,0,0,22,15);
        int burn = (int) Math.ceil(/*? if >1.20.1 {*/menu.getBurnProgress()/*?} else {*//*Mth.clamp(menu.getBurnProgress() / 24f ,0,1)*//*?}*/ * 22);
        if (burn > 0) {
            FactoryScreenUtil.enableBlend();
            FactoryGuiGraphics.of(guiGraphics).blitSprite(BedrockSprites.FULL_ARROW, 22, 15, 0, 0, 0, -1, burn, 15);
            FactoryScreenUtil.disableBlend();
        }
        guiGraphics.pose().popPose();
        //? <1.21.2 {
        if (!recipeBookComponent.isVisible() && recipeButton != null && !recipeButton.isHovered()) recipeButton.setFocused(false);
        //?}
    }
}
