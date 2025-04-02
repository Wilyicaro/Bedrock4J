package wily.bedrock4j.mixin.base.client.crafting;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.base.client.FactoryGuiGraphics;
import wily.factoryapi.base.client.UIAccessor;
import wily.bedrock4j.client.BedrockOptions;
import wily.bedrock4j.inventory.LegacySlotDisplay;
import wily.bedrock4j.util.BedrockSprites;

import static wily.bedrock4j.util.BedrockSprites.ARROW;

@Mixin(CraftingScreen.class)
public abstract class CraftingScreenMixin extends AbstractContainerScreen<CraftingMenu> {

    //? if <1.21.2 {
    @Shadow private boolean widthTooNarrow;

    @Shadow @Final private RecipeBookComponent recipeBookComponent;

    @Unique
    private ImageButton recipeButton;
    //?}
    public CraftingScreenMixin(CraftingMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

}
