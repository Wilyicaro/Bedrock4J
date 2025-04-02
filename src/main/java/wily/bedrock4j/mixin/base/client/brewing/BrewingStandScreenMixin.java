package wily.bedrock4j.mixin.base.client.brewing;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.BrewingStandScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.phys.Vec3;
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

import static wily.bedrock4j.util.BedrockSprites.BREWING_FUEL_SLOT;

@Mixin(BrewingStandScreen.class)
public abstract class BrewingStandScreenMixin extends AbstractContainerScreen<BrewingStandMenu> {
    @Shadow @Final private static int[] BUBBLELENGTHS;

    private static final Vec3 BREWING_SLOT_OFFSET = new Vec3(0,0.5,0);

    private static final LegacySlotDisplay FIRST_BREWING_SLOT_DISPLAY = createBrewingSlotDisplay(BREWING_SLOT_OFFSET);
    private static final LegacySlotDisplay SECOND_BREWING_SLOT_DISPLAY = createBrewingSlotDisplay(new Vec3(0.5,0,0));
    private static final LegacySlotDisplay THIRD_BREWING_SLOT_DISPLAY = createBrewingSlotDisplay(BREWING_SLOT_OFFSET);
    private static final LegacySlotDisplay FOURTH_BREWING_SLOT_DISPLAY = createBrewingSlotDisplay(new Vec3(0.5,0.5,0));

    private static LegacySlotDisplay createBrewingSlotDisplay(Vec3 offset){
        return new LegacySlotDisplay(){
            public Vec3 getOffset() {
                return offset;
            }
            public int getWidth() {
                return 27;
            }
            public ArbitrarySupplier<ResourceLocation> getIconHolderOverride() {
                return EMPTY_OVERRIDE;
            }
        };
    }

    public BrewingStandScreenMixin(BrewingStandMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }
}
