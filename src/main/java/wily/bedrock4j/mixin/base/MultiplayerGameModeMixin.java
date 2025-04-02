package wily.bedrock4j.mixin.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MultiPlayerGameMode.class)
public class MultiplayerGameModeMixin {
    @Shadow @Final private Minecraft minecraft;
    //? if >=1.21.2 {
    /*@ModifyVariable(method = "handleCreativeModeItemDrop", at = @At("STORE"))
    public boolean handleCreativeModeItemDrop(boolean original) {
        return original && !(minecraft.screen instanceof CreativeModeScreen);
    }
    *///?}
}
