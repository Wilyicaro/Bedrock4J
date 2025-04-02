package wily.bedrock4j.mixin.base;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public interface EntityAccessor {
    //? if <1.21.1 {
    /*@Accessor
    BlockPos getPortalEntrancePos();

    @Accessor
    void setPortalEntrancePos(BlockPos pos);
    *///?}
}
