package wily.bedrock4j.mixin.base;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.bedrock4j.Bedrock4J;
import wily.bedrock4j.util.ScreenUtil;

@Mixin(Inventory.class)
public class ClientInventoryMixin {
    @Unique
    private Inventory self(){
        return (Inventory) (Object)this;
    }
    @Inject(method = "setItem", at = @At("HEAD"))
    public void setItem(int i, ItemStack itemStack, CallbackInfo ci) {
        ItemStack actualItem = self().getItem(i);
        if (!ItemStack.matches(itemStack, actualItem) && !actualItem.isEmpty() && !itemStack.isEmpty() && Bedrock4J.anyArmorSlotMatch(self(), item-> item.equals(actualItem))){
            ScreenUtil.updateAnimatedCharacterTime(1500);
        }
    }
}
