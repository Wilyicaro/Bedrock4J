package wily.bedrock4j.mixin.base.client.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.base.client.FactoryGuiGraphics;
import wily.bedrock4j.Bedrock4JClient;
import wily.bedrock4j.client.CommonColor;
import wily.bedrock4j.client.BedrockOptions;
import wily.bedrock4j.client.screen.CreativeModeScreen;
import wily.bedrock4j.client.screen.LegacyMenuAccess;
import wily.bedrock4j.client.screen.ReplaceableScreen;
import wily.bedrock4j.inventory.LegacySlotDisplay;
import wily.bedrock4j.util.BedrockSprites;
import wily.bedrock4j.util.ScreenUtil;

import static wily.bedrock4j.util.BedrockSprites.SHIELD_SLOT;
import static wily.bedrock4j.util.BedrockSprites.SMALL_ARROW;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractContainerScreen<InventoryMenu> implements ReplaceableScreen, LegacyMenuAccess<InventoryMenu> {
    //? if <1.21.2 {
    @Shadow @Final private RecipeBookComponent recipeBookComponent;

    @Shadow private boolean widthTooNarrow;

    private ImageButton recipeButton;
    //?}
    private static final ResourceLocation[] EQUIPMENT_SLOT_SPRITES = new ResourceLocation[]{BedrockSprites.HEAD_SLOT, BedrockSprites.CHEST_SLOT, BedrockSprites.LEGS_SLOT, BedrockSprites.FEET_SLOT};

    private boolean canReplace = true;

    public InventoryScreenMixin(InventoryMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

    @ModifyArg(method = "containerTick",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V"))
    public Screen containerTick(Screen arg) {
        return CreativeModeScreen.getActualCreativeScreenInstance(minecraft);
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
        if (Bedrock4JClient.playerHasInfiniteMaterials()){
            minecraft.setScreen(CreativeModeScreen.getActualCreativeScreenInstance(minecraft));
        } else {
            super.init();
            for (int i = 0; i < menu.slots.size(); i++) {
                Slot s = menu.slots.get(i);
                if (i == InventoryMenu.SHIELD_SLOT) LegacySlotDisplay.override(s, 80, 62);
                else LegacySlotDisplay.override(s);
            }
            this.titleLabelX = 93;
            //? <1.21.2 {
            this.widthTooNarrow = this.width < 379;
            this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.menu);
            if (BedrockOptions.showVanillaRecipeBook.get()) {
                this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
                recipeButton = this.addRenderableWidget(new ImageButton(this.leftPos + 104, this.height / 2 - 22, 20, 18, /*? if >1.20.1 {*/RecipeBookComponent.RECIPE_BUTTON_SPRITES/*?} else {*//*0, 19, RECIPE_BUTTON_LOCATION*//*?}*/, (button) -> {
                    this.recipeBookComponent.toggleVisibility();
                    this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
                    button.setPosition(this.leftPos + 104, this.height / 2 - 22);
                }));
                if (recipeBookComponent.isVisible()) recipeButton.setFocused(true);
            } else if (recipeBookComponent.isVisible()) recipeBookComponent.toggleVisibility();
            //?}
        }
    }

    //? if >=1.21.2 {
    /*@Inject(method = "getRecipeBookButtonPosition", at = @At("HEAD"), cancellable = true)
    protected void getRecipeBookButtonPosition(CallbackInfoReturnable<ScreenPosition> cir){
        cir.setReturnValue(new ScreenPosition(this.leftPos + 104, this.height / 2 - 22));
    }
    *///?}

    @Inject(method = "renderBg",at = @At("HEAD"), cancellable = true)
    public void renderBg(GuiGraphics graphics, float f, int i, int j, CallbackInfo ci) {
        ci.cancel();
        FactoryGuiGraphics.of(graphics).blitSprite(BedrockSprites.PANEL,leftPos,topPos,imageWidth,imageHeight);
        FactoryGuiGraphics.of(graphics).blitSprite(BedrockSprites.SQUARE_ENTITY_PANEL,leftPos + 25,topPos + 7,54,72);
        Pose pose = minecraft.player.getPose();
        minecraft.player.setPose(Pose.STANDING);
        ScreenUtil.renderEntityInInventoryFollowsMouse(graphics,leftPos + 25,topPos + 7,leftPos + 79,topPos + 79,30,0.0625f, i, j, minecraft.player);
        minecraft.player.setPose(pose);
        FactoryGuiGraphics.of(graphics).blitSprite(SMALL_ARROW,leftPos + 134,topPos + 29,16,13);
        //? <1.21.2 {
        if (!recipeBookComponent.isVisible() && recipeButton != null && !recipeButton.isHovered()) recipeButton.setFocused(false);
        //?}
    }
    @Inject(method = "renderLabels",at = @At("HEAD"), cancellable = true)
    public void renderLabels(GuiGraphics guiGraphics, int i, int j, CallbackInfo ci) {
        ci.cancel();
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, CommonColor.INVENTORY_GRAY_TEXT.get(), false);
    }

    public boolean canReplace() {
        return Bedrock4JClient.playerHasInfiniteMaterials() && canReplace;
    }

    public void setCanReplace(boolean canReplace) {
        this.canReplace = canReplace;
    }

    public Screen getReplacement() {
        return CreativeModeScreen.getActualCreativeScreenInstance(minecraft);
    }

    @Override
    public int getTipXDiff() {
        return -186;
    }

    @Override
    public boolean allowItemPopping() {
        return true;
    }
}
