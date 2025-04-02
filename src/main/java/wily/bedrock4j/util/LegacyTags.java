package wily.bedrock4j.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import wily.bedrock4j.Bedrock4J;

public class LegacyTags {
    public static final TagKey<Block> PUSHABLE_BLOCK = TagKey.create(Registries.BLOCK, Bedrock4J.createModLocation("pushable"));
}
