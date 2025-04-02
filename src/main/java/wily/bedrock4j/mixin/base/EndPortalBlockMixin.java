package wily.bedrock4j.mixin.base;

import net.minecraft.world.level.block.EndPortalBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EndPortalBlock.class)
public class EndPortalBlockMixin {
    //? if <1.21.1 {
    /*@Inject(method = "entityInside", at = @At("HEAD"))
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity, CallbackInfo ci) {
        if (entity instanceof EntityAccessor accessor) accessor.setPortalEntrancePos(blockPos);
    }
    *///?}
}
