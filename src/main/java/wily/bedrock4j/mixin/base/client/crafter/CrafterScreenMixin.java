//? if >1.20.2 {
package wily.bedrock4j.mixin.base.client.crafter;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CrafterScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CrafterMenu;
import net.minecraft.world.inventory.CrafterSlot;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.base.ArbitrarySupplier;
import wily.factoryapi.base.client.FactoryGuiGraphics;
import wily.factoryapi.base.client.UIAccessor;
import wily.bedrock4j.inventory.LegacySlotDisplay;
import wily.bedrock4j.util.BedrockSprites;

@Mixin(CrafterScreen.class)
public abstract class CrafterScreenMixin extends AbstractContainerScreen<CrafterMenu> {



    @Shadow @Final private static Component DISABLED_SLOT_TOOLTIP;

    @Shadow @Final private static ResourceLocation POWERED_REDSTONE_LOCATION_SPRITE;

    @Shadow @Final private static ResourceLocation UNPOWERED_REDSTONE_LOCATION_SPRITE;

    public CrafterScreenMixin(CrafterMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

}
//?}
