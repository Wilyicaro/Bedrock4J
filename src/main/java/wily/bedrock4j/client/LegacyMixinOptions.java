package wily.bedrock4j.client;

import wily.factoryapi.base.config.FactoryMixinToggle;
import wily.bedrock4j.config.LegacyMixinToggles;

public class LegacyMixinOptions {
    public static final FactoryMixinToggle.Storage CLIENT_MIXIN_STORAGE = new FactoryMixinToggle.Storage("bedrock4j/client_mixin.json");

    public static final FactoryMixinToggle legacyInventoryScreen = createAndRegisterMixin("bedrock4j.mixin.base.client.inventory", "legacyInventoryScreen");
    public static final FactoryMixinToggle legacyClassicCraftingScreen = createAndRegisterMixin("bedrock4j.mixin.base.client.crafting", "legacyClassicCraftingScreen");
    public static final FactoryMixinToggle legacyClassicStonecutterScreen = createAndRegisterMixin("bedrock4j.mixin.base.client.stonecutter", "legacyClassicStonecutterScreen");
    public static final FactoryMixinToggle legacyClassicLoomScreen = createAndRegisterMixin("bedrock4j.mixin.base.client.loom", "legacyClassicLoomScreen");
    public static final FactoryMixinToggle legacyClassicMerchantScreen = createAndRegisterMixin("bedrock4j.mixin.base.client.merchant", "legacyClassicMerchantScreen");
    public static final FactoryMixinToggle legacyContainerLikeScreen = createAndRegisterMixin("bedrock4j.mixin.base.client.container", "legacyContainerLikeScreen");
    //? if >=1.20.2 {
    public static final FactoryMixinToggle legacyCrafterScreen = createAndRegisterMixin("bedrock4j.mixin.base.client.crafter", "legacyCrafterScreen");
    //?}
    public static final FactoryMixinToggle legacyFurnaceScreen = createAndRegisterMixin("bedrock4j.mixin.base.client.furnace", "legacyFurnaceScreen");
    public static final FactoryMixinToggle legacyAnvilScreen = createAndRegisterMixin("bedrock4j.mixin.base.client.anvil", "legacyAnvilScreen");
    public static final FactoryMixinToggle legacySmithingScreen = createAndRegisterMixin("bedrock4j.mixin.base.client.smithing", "legacySmithingScreen");
    public static final FactoryMixinToggle legacyGrindstoneScreen = createAndRegisterMixin("bedrock4j.mixin.base.client.grindstone", "legacyGrindstoneScreen");
    public static final FactoryMixinToggle legacyCartographyScreen = createAndRegisterMixin("bedrock4j.mixin.base.client.cartography", "legacyCartographyScreen");
    public static final FactoryMixinToggle legacyEnchantmentScreen = createAndRegisterMixin("bedrock4j.mixin.base.client.enchantment", "legacyEnchantmentScreen");
    public static final FactoryMixinToggle legacyBeaconScreen = createAndRegisterMixin("bedrock4j.mixin.base.client.beacon", "legacyBeaconScreen");
    public static final FactoryMixinToggle legacyBrewingStandScreen = createAndRegisterMixin("bedrock4j.mixin.base.client.brewing", "legacyBrewingStandScreen");
    public static final FactoryMixinToggle legacyBookScreen = createAndRegisterMixin("bedrock4j.mixin.base.client.book", "legacyBookScreen");
    public static final FactoryMixinToggle legacySignScreen = createAndRegisterMixin("bedrock4j.mixin.base.client.sign", "legacySignScreen");
    public static final FactoryMixinToggle legacyCreateWorldScreen = createAndRegisterMixin("bedrock4j.mixin.base.client.create_world", "legacyCreateWorldScreen");
    public static final FactoryMixinToggle legacyTitleScreen = createAndRegisterMixin("bedrock4j.mixin.base.client.title", "legacyTitleScreen");
    public static final FactoryMixinToggle legacyPauseScreen = createAndRegisterMixin("bedrock4j.mixin.base.client.pause", "legacyPauseScreen");
    public static final FactoryMixinToggle legacyGui = createAndRegisterMixin("bedrock4j.mixin.base.client.gui", "legacyGui");
    public static final FactoryMixinToggle legacyChat = createAndRegisterMixin("bedrock4j.mixin.base.client.chat", "legacyChat");
    public static final FactoryMixinToggle legacyBossHealth = createAndRegisterMixin("bedrock4j.mixin.base.client.bosshealth", "legacyBossHealth");
    public static final FactoryMixinToggle legacyWitches = createAndRegisterMixin("bedrock4j.mixin.base.client.witch", "legacyWitches");
    public static final FactoryMixinToggle legacyDrowned = createAndRegisterMixin("bedrock4j.mixin.base.client.drowned", "legacyDrowned");



    public static FactoryMixinToggle createAndRegisterMixin(String key, String translationKey){
        return CLIENT_MIXIN_STORAGE.register(LegacyMixinToggles.createMixinOption(key, translationKey, true));
    }
}
