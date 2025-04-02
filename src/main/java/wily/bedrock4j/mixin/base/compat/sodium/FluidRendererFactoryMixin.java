//? if >=1.21 && (fabric || neoforge) {
package wily.bedrock4j.mixin.base.compat.sodium;

import net.caffeinemc.mods.sodium.client.model.quad.blender.BlendedColorProvider;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
//? if fabric {
import net.caffeinemc.mods.sodium.fabric.render.FluidRendererImpl;
//?} else if forge || neoforge {
/*import net.caffeinemc.mods.sodium.neoforge.render.FluidRendererImpl;
*///?}
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.bedrock4j.client.LegacyBiomeOverride;

@Mixin(value = /*? if fabric {*/FluidRendererImpl.FabricFactory /*?} else {*/ /*FluidRendererImpl.ForgeFactory*//*?}*/.class, remap = false)
public class FluidRendererFactoryMixin {
    @Inject(method = "getWaterColorProvider", at = @At("HEAD"), cancellable = true)
    public void getWaterColorProvider(CallbackInfoReturnable<BlendedColorProvider<FluidState>> cir) {
        cir.setReturnValue(new BlendedColorProvider<>() {
            @Override
            protected int getColor(LevelSlice levelSlice, FluidState fluidState, BlockPos blockPos) {
                return LegacyBiomeOverride.getOrDefault(levelSlice.getBiomeFabric(blockPos).unwrapKey()).getWaterARGBOrDefault(BiomeColors.getAverageWaterColor(levelSlice,blockPos));
            }
        });
    }
    @Inject(method = "getWaterBlockColorProvider", at = @At("HEAD"), cancellable = true)
    public void getWaterBlockColorProvider(CallbackInfoReturnable<BlendedColorProvider<FluidState>> cir) {
        cir.setReturnValue(new BlendedColorProvider<>() {
            @Override
            protected int getColor(LevelSlice levelSlice, FluidState fluidState, BlockPos blockPos) {
                return LegacyBiomeOverride.getOrDefault(levelSlice.getBiomeFabric(blockPos).unwrapKey()).getWaterARGBOrDefault(BiomeColors.getAverageWaterColor(levelSlice,blockPos));
            }
        });
    }
}
//?}
