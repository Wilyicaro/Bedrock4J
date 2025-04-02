package wily.bedrock4j.mixin.base;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
//? if >=1.21.2 {
/*import net.minecraft.client.renderer.SkyRenderer;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.bedrock4j.Bedrock4JClient;
import wily.bedrock4j.client.BedrockOptions;
import wily.bedrock4j.client.LevelRendererAccessor;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin implements LevelRendererAccessor {
    //? if <1.21.2 {
    @Shadow protected abstract void createLightSky();

    @Shadow protected abstract void createDarkSky();

    @Override
    public void updateSkyBuffers() {
        createLightSky();
        createDarkSky();
    }

    //? if <1.20.5 {
    /*@Inject(method = "buildSkyDisc", at = @At("HEAD"), cancellable = true)
    private static void buildSkyDisc(BufferBuilder bufferBuilder, float f, CallbackInfoReturnable<BufferBuilder.RenderedBuffer> cir) {
        if (LegacyOptions.legacySkyShape.get()) {
            RenderSystem.setShader(GameRenderer::getPositionShader);
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
            Legacy4JClient.buildLegacySkyDisc(bufferBuilder, f);
            cir.setReturnValue(bufferBuilder.end());
        }
    }
    *///?} else {
    @Inject(method = "buildSkyDisc", at = @At("HEAD"), cancellable = true)
    private static void buildSkyDisc(Tesselator tesselator, float f, CallbackInfoReturnable<MeshData> cir) {
        if (BedrockOptions.legacySkyShape.get()) {
            RenderSystem.setShader(GameRenderer::getPositionShader);
            BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
            Bedrock4JClient.buildLegacySkyDisc(bufferBuilder, f);
            cir.setReturnValue(bufferBuilder.buildOrThrow());
        }
    }
    //?}

    //?} else {

    /*@Shadow @Final @Mutable
    private SkyRenderer skyRenderer;
    @Override
    public void updateSkyBuffers() {
        skyRenderer.close();
        this.skyRenderer = new SkyRenderer();
    }
    *///?}

    @ModifyArg(method = "renderHitOutline", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderShape(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/phys/shapes/VoxelShape;DDDFFFF)V"), index = 9)
    private float changeHitOutlineTranslucency(float f){
        return 1.0f;
    }
}
