package wily.bedrock4j.config;

import wily.factoryapi.base.config.FactoryConfigDisplay;
import wily.factoryapi.base.config.FactoryMixinToggle;
import wily.bedrock4j.util.LegacyComponents;

import static wily.bedrock4j.util.LegacyComponents.optionName;

public class LegacyMixinToggles {
    public static final FactoryMixinToggle.Storage COMMON_STORAGE = new FactoryMixinToggle.Storage("bedrock4j/common_mixin.json");
    public static final FactoryMixinToggle legacyCauldrons = createAndRegisterMixinOption("bedrock4j.mixin.base.cauldron", "legacyCauldrons");
    public static final FactoryMixinToggle legacyPistons = createAndRegisterMixinOption("bedrock4j.mixin.base.piston", "legacyPistons");


    private static FactoryMixinToggle createAndRegisterMixinOption(String key, String translationKey) {
        return COMMON_STORAGE.register(createMixinOption(key, translationKey, true));
    }

    public static FactoryMixinToggle createMixinOption(String key, String translationKey, boolean defaultValue) {
        return new FactoryMixinToggle(key, defaultValue, ()-> new FactoryConfigDisplay.Instance<>(optionName(translationKey), b-> LegacyComponents.NEEDS_RESTART, (c,v)->c));
    }
}
