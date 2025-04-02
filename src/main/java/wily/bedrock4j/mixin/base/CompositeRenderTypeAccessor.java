package wily.bedrock4j.mixin.base;

import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RenderType.CompositeRenderType.class)
public interface CompositeRenderTypeAccessor {
    //? if <1.21.5 {
    @Invoker("state")
    //?} else {
    /*@Accessor("state")
    *///?}
    RenderType.CompositeState getState();
}
