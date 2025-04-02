package wily.bedrock4j.mixin.base.client.merchant;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factoryapi.base.client.FactoryGuiGraphics;
import wily.factoryapi.base.client.UIAccessor;
import wily.factoryapi.util.FactoryScreenUtil;
import wily.bedrock4j.client.CommonColor;
import wily.bedrock4j.client.screen.LegacyScrollRenderer;
import wily.bedrock4j.inventory.BedrockMerchantOffer;
import wily.bedrock4j.inventory.LegacySlotDisplay;
import wily.bedrock4j.util.BedrockSprites;
import wily.bedrock4j.util.ScreenUtil;

import static wily.bedrock4j.util.BedrockSprites.ARROW;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends AbstractContainerScreen<MerchantMenu> {

    private LegacyScrollRenderer scrollRenderer = new LegacyScrollRenderer();

    @Shadow private int scrollOff;

    @Shadow @Final private static Component DEPRECATED_TOOLTIP;

    @Shadow protected abstract void renderAndDecorateCostA(GuiGraphics arg, ItemStack arg2, ItemStack arg3, int i, int j);

    @Shadow protected abstract void renderButtonArrows(GuiGraphics arg, MerchantOffer arg2, int i, int j);

    @Shadow private int shopItem;

    @Shadow private boolean isDragging;

    @Shadow protected abstract void postButtonClick();

    @Shadow @Final private static Component TRADES_LABEL;

    public MerchantScreenMixin(MerchantMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }
}
