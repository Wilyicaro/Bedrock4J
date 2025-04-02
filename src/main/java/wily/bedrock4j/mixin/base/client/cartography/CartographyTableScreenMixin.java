package wily.bedrock4j.mixin.base.client.cartography;

//? if >=1.20.5 {
import net.minecraft.core.component.DataComponents;
 //?}
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CartographyTableScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.base.client.FactoryGuiGraphics;
import wily.factoryapi.base.client.UIAccessor;
import wily.factoryapi.util.FactoryItemUtil;
import wily.bedrock4j.client.CommonColor;
import wily.bedrock4j.inventory.LegacySlotDisplay;
import wily.bedrock4j.util.BedrockSprites;
import wily.bedrock4j.inventory.RenameItemMenu;

@Mixin(CartographyTableScreen.class)
public abstract class CartographyTableScreenMixin extends AbstractContainerScreen<CartographyTableMenu> {
    private static final Component MAP_NAME = Component.translatable("bedrock4j.container.mapName");
    private static final Component RENAME_MAP = Component.translatable("bedrock4j.container.renameMap");
    private static final Component ZOOM = Component.translatable("bedrock4j.container.zoomMap");
    private static final Component COPY = Component.translatable("bedrock4j.container.copyMap");
    private static final Component LOCK = Component.translatable("bedrock4j.container.lockMap");

    private EditBox name;
    private final ContainerListener listener = new ContainerListener() {
        @Override
        public void slotChanged(AbstractContainerMenu abstractContainerMenu, int i, ItemStack itemStack) {
            if (i == 0) {
                name.setValue(itemStack.isEmpty() ? "" : itemStack.getHoverName().getString());
                name.setEditable(!itemStack.isEmpty());
            }
        }
        @Override
        public void dataChanged(AbstractContainerMenu abstractContainerMenu, int i, int j) {

        }
    };

    public CartographyTableScreenMixin(CartographyTableMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }
}
