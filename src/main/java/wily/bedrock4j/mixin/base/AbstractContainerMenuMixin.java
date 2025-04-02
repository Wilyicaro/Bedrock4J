package wily.bedrock4j.mixin.base;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.bedrock4j.Bedrock4J;

import static wily.bedrock4j.Bedrock4J.canRepair;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {
    @Shadow @Final public NonNullList<Slot> slots;

    @Shadow public abstract ItemStack getCarried();

    @Shadow public abstract void setCarried(ItemStack itemStack);

    @Inject(method = "doClick", at = @At("HEAD"), cancellable = true)
    private void doClick(int i, int j, ClickType clickType, Player player, CallbackInfo ci) {
        Slot slot;
        if ((clickType == ClickType.PICKUP || clickType == ClickType.QUICK_MOVE) && j == 1 && i >= 0 && i < slots.size() && (slot = slots.get(i)).hasItem() && !getCarried().isEmpty()){
            if (canRepair(slot.getItem(),getCarried())) {
                ItemStack item = slot.getItem().getItem().getDefaultInstance();
                item.setDamageValue(slot.getItem().getDamageValue() - (item.getMaxDamage() - getCarried().getDamageValue()));
                slot.set(item);
                if (!/*? if <1.20.5 {*//*player.getAbilities().instabuild*//*?} else {*/player.hasInfiniteMaterials()/*?}*/)
                    setCarried(ItemStack.EMPTY);
                ci.cancel();
            } else if (Bedrock4J.isDyeableItem(slot.getItem().getItemHolder()) && getCarried().getItem() instanceof DyeItem d) {
                Bedrock4J.dyeItem(slot.getItem(), Bedrock4J.getDyeColor(d.getDyeColor()));
                slot.setChanged();
                if (!/*? if <1.20.5 {*//*player.getAbilities().instabuild*//*?} else {*/player.hasInfiniteMaterials()/*?}*/)
                    getCarried().shrink(1);
                ci.cancel();
            }
        }
    }
}
