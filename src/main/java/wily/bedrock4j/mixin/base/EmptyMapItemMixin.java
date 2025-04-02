package wily.bedrock4j.mixin.base;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EmptyMapItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
//? if >=1.20.5 {
import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.component.DataComponents;
//?}
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import wily.bedrock4j.init.BedrockGameRules;

@Mixin(EmptyMapItem.class)
public class EmptyMapItemMixin {
    //? if <1.20.5 {
    /*@Redirect(method = "use", at = @At(value = "INVOKE",target = "Lnet/minecraft/world/item/MapItem;create(Lnet/minecraft/world/level/Level;IIBZZ)Lnet/minecraft/world/item/ItemStack;"))
    public ItemStack use(Level level, int arg, int i, byte j, boolean b, boolean bl, Level level1, Player player, InteractionHand interactionHand) {
        ItemStack map = player.getItemInHand(interactionHand);
        byte newScale = map.getOrCreateTag().getByte(MapItem.MAP_SCALE_TAG);
        return MapItem.create(level, arg, i, newScale > 0 ? newScale : (byte) level.getGameRules().getInt(LegacyGameRules.DEFAULT_MAP_SIZE), b, bl);
    }
    *///?} else {
    @Redirect(method = "use", at = @At(value = "INVOKE",target = "Lnet/minecraft/world/item/ItemStack;consume(ILnet/minecraft/world/entity/LivingEntity;)V"))
    public void useConsume(ItemStack instance, int i, LivingEntity arg) {

    }
    @WrapOperation(method = "use", at = @At(value = "INVOKE",target = /*? if <1.21.5 {*/"Lnet/minecraft/world/item/MapItem;create(Lnet/minecraft/world/level/Level;IIBZZ)Lnet/minecraft/world/item/ItemStack;"/*?} else {*//*"Lnet/minecraft/world/item/MapItem;create(Lnet/minecraft/server/level/ServerLevel;IIBZZ)Lnet/minecraft/world/item/ItemStack;"*//*?}*/))
    public ItemStack use(/*? if <1.21.5 {*/Level/*?} else {*//*ServerLevel*//*?}*/ level, int arg, int i, byte j, boolean b, boolean bl, Operation<ItemStack> original, Level level1, Player player, InteractionHand interactionHand) {
        ItemStack map = player.getItemInHand(interactionHand);
        CompoundTag custom = map.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        map.consume(1,player);
        return MapItem.create(level, arg, i, custom.contains("map_scale") ? custom.getByte("map_scale")/*? if >=1.21.5 {*//*.orElse((byte) 0)*//*?}*/ : (byte) ((ServerLevel)level).getGameRules().getInt(BedrockGameRules.DEFAULT_MAP_SIZE), b, bl);
    }
    //?}
}
