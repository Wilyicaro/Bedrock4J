package wily.bedrock4j.config;

import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.config.FactoryConfig;
import wily.factoryapi.base.config.FactoryConfigDisplay;
import wily.bedrock4j.Bedrock4JClient;

import static wily.bedrock4j.util.LegacyComponents.optionName;

public class LegacyCommonOptions {
    public static final FactoryConfig.StorageHandler COMMON_STORAGE = new FactoryConfig.StorageHandler(true).withFile("bedrock4j/common.json");
    public static final FactoryConfig<Boolean> legacyCombat = COMMON_STORAGE.register(FactoryConfig.createBoolean("legacyCombat", new FactoryConfigDisplay.Instance<>(optionName("legacyCombat")), true, b-> {}, COMMON_STORAGE));
    public static final FactoryConfig<Boolean> legacySwordBlocking = COMMON_STORAGE.register(FactoryConfig.createBoolean("legacySwordBlocking", new FactoryConfigDisplay.Instance<>(optionName("legacySwordBlocking")), false, b-> {}, COMMON_STORAGE));
    public static final FactoryConfig<Boolean> squaredViewDistance = COMMON_STORAGE.register(FactoryConfig.createBoolean("squaredViewDistance", new FactoryConfigDisplay.Instance<>(optionName("squaredViewDistance")), false, b-> {
        if (FactoryAPI.isClient()) Bedrock4JClient.updateChunks();
    }, COMMON_STORAGE));

}
