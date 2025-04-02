package wily.bedrock4j.mixin.base;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.FactoryAPIClient;
import wily.bedrock4j.client.BedrockOptions;
import wily.bedrock4j.client.screen.LegacyMenuAccess;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {
    @Shadow @Final private PoseStack pose;

    @Shadow public abstract PoseStack pose();
    @Shadow @Final private Minecraft minecraft;


    //? if <1.21.4 {
    @Redirect(method = "enableScissor", at = @At(value = "NEW", target = "(IIII)Lnet/minecraft/client/gui/navigation/ScreenRectangle;"))
    private ScreenRectangle enableScissor(int i, int j, int k, int l, int x, int y, int xd, int yd){
        Matrix4f matrix4f = this.pose.last().pose();
        Vector3f vector3f = matrix4f.transformPosition(x, y, 0.0F, new Vector3f());
        Vector3f vector3f2 = matrix4f.transformPosition(xd, yd, 0.0F, new Vector3f());
        return new ScreenRectangle(Mth.floor(vector3f.x), Mth.floor(vector3f.y), Mth.floor(vector3f2.x - vector3f.x), Mth.floor(vector3f2.y - vector3f.y));
    }
    //?}

    @Inject(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"))
    private void renderItem(LivingEntity livingEntity, Level level, ItemStack itemStack, int i, int j, int k, int l, CallbackInfo ci){
        float g = (float)itemStack.getPopTime() - FactoryAPIClient.getGamePartialTick(true);
        if (g > 0.0F && (minecraft.screen == null || minecraft.screen instanceof LegacyMenuAccess<?> m && m.allowItemPopping())) {
            float h = 1.0F + g / 5.0F;
            pose().translate((float)(i + 8), (float)(j + 12), 0.0F);
            pose().scale(1.0F / h, (h + 1.0F) / 2.0F, 1.0F);
            pose().translate((float)(-(i + 8)), (float)(-(j + 12)), 0.0F);
            if (minecraft.player != null  && !minecraft.player.getInventory()./*? if <1.21.5 {*/items/*?} else {*//*getNonEquipmentItems()*//*?}*/.contains(itemStack)) itemStack.setPopTime(itemStack.getPopTime() - 1);
        }
    }
    @ModifyArg(method = "renderTooltipInternal", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V", ordinal = 0), index = 2)
    private float renderTooltipInternal(float z){
        return 800;
    }

    @Redirect(method = /*? if <1.21.2 {*/"renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V"/*?} else {*//*"renderItemCount"*//*?}*/, at = @At(value = "INVOKE", target = "Ljava/lang/String;valueOf(I)Ljava/lang/String;"))
    private String renderItemDecorationsTail(int i, Font font, ItemStack itemStack){
        return i > itemStack.getMaxStackSize() && BedrockOptions.legacyOverstackedItems.get() ? I18n.get("bedrock4j.container.overstack",itemStack.getMaxStackSize()) : String.valueOf(i);
    }
}
