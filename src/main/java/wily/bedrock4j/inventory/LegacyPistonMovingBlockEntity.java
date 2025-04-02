package wily.bedrock4j.inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public interface LegacyPistonMovingBlockEntity {
    CompoundTag getMovedBlockEntityTag();
    void setMovedBlockEntityTag(CompoundTag tag);
    BlockEntity getRenderingBlockEntity();
    void setRenderingBlockEntity(BlockEntity entity);
    BlockEntityType<?> getMovingBlockEntityType();
    void setMovingBlockEntityType(BlockEntityType<?> type);
    void createRenderingBlockEntity(Level level);

    default void load(){
        if (this instanceof BlockEntity be){
            load(be.getLevel().getBlockEntity(be.getBlockPos()));
        }
    }
    default void load(BlockEntity blockEntity){
        if (getMovedBlockEntityTag() == null) return;
        if (blockEntity != null) blockEntity./*? if <1.20.5 {*//*load*//*?} else {*/loadCustomOnly/*?}*/(getMovedBlockEntityTag()/*? if >=1.20.5 {*/, blockEntity.getLevel().registryAccess()/*?}*/);
    }
}
