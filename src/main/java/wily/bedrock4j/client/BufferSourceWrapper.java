package wily.bedrock4j.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import wily.bedrock4j.mixin.base.BufferSourceAccessor;
import wily.bedrock4j.mixin.base.CompositeRenderTypeAccessor;

public class BufferSourceWrapper extends MultiBufferSource.BufferSource {
    public final MultiBufferSource.BufferSource source;
    private RenderType overrideRenderType;

    public BufferSourceWrapper(MultiBufferSource.BufferSource source){
        super(((BufferSourceAccessor)source).buffer(),((BufferSourceAccessor)source).fixedBuffers());
        this.source = source;
    }
    public static BufferSourceWrapper translucent(BufferSource source){
        return new BufferSourceWrapper(source){
            @Override
            public VertexConsumer getBuffer(RenderType renderType) {
                if (renderType == RenderType.cutout() || renderType == RenderType.solid() || renderType == RenderType.cutoutMipped()) return super.getBuffer(RenderType.translucent());
                //? if >=1.21.2 {
                /*else if (renderType == Sheets.cutoutBlockSheet()) return super.getBuffer(Sheets.translucentItemSheet());
                *///?}
                else if (renderType.format() == DefaultVertexFormat.NEW_ENTITY && renderType instanceof RenderType.CompositeRenderType r && ((CompositeRenderTypeAccessor)(Object)r).getState().textureState instanceof RenderStateShard.TextureStateShard s && s.texture.isPresent()) return super.getBuffer(RenderType./*? if <1.21.2 {*/entityTranslucentCull/*?} else {*//*itemEntityTranslucentCull*//*?}*/(s.texture.get()));
                return super.getBuffer(renderType);
            }
        };
    }
    public static BufferSourceWrapper of(BufferSource source, RenderType overrideType){
        BufferSourceWrapper wrapper = new BufferSourceWrapper(source);
        wrapper.setOverrideRenderType(overrideType);
        return wrapper;
    }

    @Override
    public void endLastBatch() {
        source.endLastBatch();
    }

    @Override
    public void endBatch() {
        source.endBatch();
    }

    @Override
    public void endBatch(RenderType renderType) {
        source.endBatch(renderType);
    }

    public void setOverrideRenderType(RenderType overrideRenderTpe) {
        this.overrideRenderType = overrideRenderTpe;
    }

    @Override
    public VertexConsumer getBuffer(RenderType renderType) {
        return source.getBuffer(overrideRenderType == null ? renderType : overrideRenderType);
    }
}
