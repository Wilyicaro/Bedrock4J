package wily.bedrock4j.client;

import com.mojang.serialization.Codec;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Difficulty;
import wily.factoryapi.base.Bearer;
import wily.factoryapi.base.config.FactoryConfig;
import wily.factoryapi.base.config.FactoryConfigControl;
import wily.factoryapi.base.config.FactoryConfigDisplay;
import wily.factoryapi.base.network.CommonNetwork;
import wily.bedrock4j.Bedrock4J;
import wily.bedrock4j.Bedrock4JClient;
import wily.bedrock4j.client.controller.*;
import wily.bedrock4j.network.PlayerInfoSync;
import wily.bedrock4j.util.LegacyComponents;

import java.util.*;
import java.util.function.*;

import static wily.bedrock4j.util.LegacyComponents.optionName;


public class BedrockOptions {
    public static final Function<OptionInstance<?>,FactoryConfig<?>> LEGACY_OPTION_OPTION_INSTANCE_CACHE = Util.memoize(BedrockOptions::create);

    public static final Map<Component, Component> vanillaCaptionOverrideMap = new HashMap<>(Map.of(Component.translatable("key.sprint"),Component.translatable("options.key.toggleSprint"),Component.translatable("key.sneak"),Component.translatable("options.key.toggleSneak")));

    
    public static final FactoryConfig.StorageHandler CLIENT_STORAGE = new FactoryConfig.StorageHandler(){
        @Override
        public void load() {
            for (KeyMapping keyMapping : Minecraft.getInstance().options.keyMappings) {
                LegacyKeyMapping mapping = LegacyKeyMapping.of(keyMapping);
                register(FactoryConfig.create("component_" + keyMapping.getName(), null, Optional.ofNullable(((LegacyKeyMapping) keyMapping).getDefaultBinding()), Bearer.of(()->Optional.ofNullable(mapping.getBinding()),o->mapping.setBinding(o.map(b-> !b.isBindable ? null : b).orElse(mapping.getDefaultBinding()))), ()->ControllerBinding.OPTIONAL_CODEC, m->{}, this));
            }
            super.load();
            Bedrock4JClient.isNewerVersion = Bedrock4J.isNewerVersion(Bedrock4J.VERSION.get(), lastLoadedVersion.get());
            Bedrock4JClient.isNewerMinecraftVersion = Bedrock4J.isNewerVersion(SharedConstants.getCurrentVersion().getName(), lastLoadedMinecraftVersion.get());
        }
    }.withFile("bedrock4j/client_options.json");

    public static final FactoryConfig.StorageAccess VANILLA_STORAGE_ACCESS = ()-> Minecraft.getInstance().options.save();


    public static <T> FactoryConfig<T> of(OptionInstance<T> optionInstance) {
        return (FactoryConfig<T>) LEGACY_OPTION_OPTION_INSTANCE_CACHE.apply(optionInstance);
    }

    public static <T> FactoryConfig<T> create(OptionInstance<T> optionInstance) {
        FactoryConfigControl<T> control;
        if (optionInstance.values().equals(OptionInstance.BOOLEAN_VALUES)){
            control = (FactoryConfigControl<T>) FactoryConfigControl.TOGGLE;
        } else if (optionInstance.values() instanceof OptionInstance.CycleableValueSet<T> set) {
            control = new FactoryConfigControl.FromInt<>(optionInstance.codec(), i-> set.valueListSupplier().getSelectedList().get(i), v-> set.valueListSupplier().getSelectedList().indexOf(v), ()->set.valueListSupplier().getSelectedList().size());
        } else if (optionInstance.values() instanceof OptionInstance.SliderableValueSet<T> set) {
            control = new FactoryConfigControl.FromDouble<>(optionInstance.codec(), set::fromSliderValue, set::toSliderValue);
        } else return null;
        return FactoryConfig.create(OptionInstanceAccessor.of(optionInstance).getKey(), new FactoryConfigDisplay.Instance<>(vanillaCaptionOverrideMap.getOrDefault(optionInstance.caption,optionInstance.caption), v-> componentFromTooltip(OptionInstanceAccessor.of(optionInstance).tooltip().apply(v)), (c,v)-> optionInstance.values() instanceof OptionInstance.CycleableValueSet<T> ? CommonComponents.optionNameValue(c,optionInstance.toString.apply(optionInstance.get())) : optionInstance.toString.apply(optionInstance.get())), OptionInstanceAccessor.of(optionInstance).defaultValue(), Bearer.of(optionInstance::get, v->{
            if (optionInstance.values() instanceof OptionInstance.CycleableValueSet<T> set) {
                set.valueSetter().set(optionInstance,v);
            } else optionInstance.set(v);
        }), control, v->{}, VANILLA_STORAGE_ACCESS);
    }

    public static Component componentFromTooltip(Tooltip tooltip){
        return tooltip == null ? null : tooltip.message;
    }

    public static FactoryConfig<Boolean> createBoolean(String key, boolean defaultValue) {
        return createBoolean(key, defaultValue, b-> {});
    }

    public static FactoryConfig<Boolean> createBoolean(String key, boolean defaultValue, Consumer<Boolean> consumer) {
        return createBoolean(key, b->null, defaultValue, consumer);
    }
    public static FactoryConfig<Boolean> createBoolean(String key, Function<Boolean,Component> tooltipFunction, boolean defaultValue) {
        return createBoolean(key, tooltipFunction, defaultValue, b->{});
    }
    public static FactoryConfig<Boolean> createBoolean(String key, Function<Boolean,Component> tooltipFunction, boolean defaultValue, Consumer<Boolean> consumer) {
        return FactoryConfig.createBoolean(key, new FactoryConfigDisplay.Instance<>(optionName(key), tooltipFunction, (c, v) -> c), defaultValue, consumer, CLIENT_STORAGE);
    }

    public static FactoryConfig<Integer> createInteger(String key, BiFunction<Component,Integer,Component> captionFunction, int min, IntSupplier max, int defaultValue) {
        return createInteger(key, captionFunction, min, max, defaultValue, v-> {});
    }

    public static FactoryConfig<Integer> createInteger(String key, BiFunction<Component,Integer,Component> captionFunction, int min, IntSupplier max, int defaultValue, Consumer<Integer> consumer) {
        return createInteger(key, v-> null, captionFunction, min, max, defaultValue, consumer);
    }

    public static FactoryConfig<Integer> createInteger(String key, Function<Integer,Component> tooltipFunction, BiFunction<Component,Integer,Component> captionFunction, int min, IntSupplier max, int defaultValue, Consumer<Integer> consumer) {
        return createInteger(key, tooltipFunction, captionFunction, min, max, defaultValue, consumer, CLIENT_STORAGE);
    }

    public static FactoryConfig<Integer> createInteger(String key, Function<Integer,Component> tooltipFunction, BiFunction<Component,Integer,Component> captionFunction, int min, IntSupplier max, int defaultValue, Consumer<Integer> consumer, FactoryConfig.StorageAccess access) {
        return FactoryConfig.createInteger(key, new FactoryConfigDisplay.Instance<>(optionName(key), tooltipFunction, captionFunction), new FactoryConfigControl.Int(min, max, Integer.MAX_VALUE), defaultValue, consumer, access);
    }

    public static <T> FactoryConfig<T> create(String key, BiFunction<Component,T,Component> captionFunction, Function<Integer,T> valueGetter, Function<T, Integer> valueSetter, Supplier<Integer> valuesSize, T defaultValue, Consumer<T> consumer, FactoryConfig.StorageAccess access) {
        return FactoryConfig.create(key, new FactoryConfigDisplay.Instance<>(optionName(key), captionFunction), new FactoryConfigControl.FromInt<>(valueGetter, valueSetter, valuesSize), defaultValue, consumer, access);
    }

    public static <T> FactoryConfig<T> create(String key, BiFunction<Component,T,Component> captionFunction, Supplier<List<T>> listSupplier, T defaultValue, Consumer<T> consumer) {
        return create(key, v-> null, captionFunction, listSupplier, defaultValue, consumer);
    }

    public static <T> FactoryConfig<T> create(String key, Function<T,Component> tooltipFunction, BiFunction<Component,T,Component> captionFunction, Supplier<List<T>> listSupplier, T defaultValue, Consumer<T> consumer) {
        return FactoryConfig.create(key, new FactoryConfigDisplay.Instance<>(optionName(key), tooltipFunction, captionFunction), new FactoryConfigControl.FromInt<>(i->listSupplier.get().get(i), v-> listSupplier.get().indexOf(v), ()-> listSupplier.get().size()), defaultValue, consumer, CLIENT_STORAGE);
    }


    public static FactoryConfig<Double> createDouble(String key, BiFunction<Component,Double,Component> captionFunction, double defaultValue) {
        return createDouble(key, captionFunction, defaultValue, b->{});
    }
    public static FactoryConfig<Double> createDouble(String key, BiFunction<Component,Double,Component> captionFunction, double defaultValue, Consumer<Double> consumer) {
        return createDouble(key, v-> null, captionFunction, defaultValue, consumer);
    }
    public static FactoryConfig<Double> createDouble(String key, Function<Double,Component> tooltipFunction, BiFunction<Component,Double,Component> captionFunction, double defaultValue) {
        return createDouble(key, tooltipFunction, captionFunction, defaultValue, b->{});
    }

    public static FactoryConfig<Double> createDouble(String key, Function<Double,Component> tooltipFunction, BiFunction<Component,Double,Component> captionFunction, double defaultValue, Consumer<Double> consumer) {
        return FactoryConfig.create(key, new FactoryConfigDisplay.Instance<>(optionName(key), tooltipFunction, captionFunction), FactoryConfigControl.createDouble(), defaultValue, consumer, CLIENT_STORAGE);
    }

    public static Component percentValueLabel(Component component, double d) {
        return Component.translatable("options.percent_value", component, (int)(d * 100.0));
    }

    public static <T> Function<T,Component> staticComponent(Component component){
        return v->component;
    }

    public static final FactoryConfig<String> lastLoadedVersion = CLIENT_STORAGE.register(FactoryConfig.create("lastLoadedVersion", null,()-> Codec.STRING,"", v-> {}, CLIENT_STORAGE));
    public static final FactoryConfig<String> lastLoadedMinecraftVersion = CLIENT_STORAGE.register(FactoryConfig.create("lastLoadedMinecraftVersion", null, ()-> Codec.STRING, "", v-> {}, CLIENT_STORAGE));
    public static final FactoryConfig<Boolean> animatedCharacter = CLIENT_STORAGE.register(createBoolean("animatedCharacter",true));
    public static final FactoryConfig<Boolean> vanillaTabs = CLIENT_STORAGE.register(createBoolean("vanillaTabs",staticComponent(Component.translatable("bedrock4j.options.vanillaTabs.description")),false));
    public static final FactoryConfig<Boolean> displayHUD = CLIENT_STORAGE.register(createBoolean("displayHUD",true));
    public static final FactoryConfig<Boolean> displayHand = CLIENT_STORAGE.register(createBoolean("displayHand",true));
    public static final FactoryConfig<Boolean> legacyCreativeTab = CLIENT_STORAGE.register(createBoolean("creativeTab", true));
    public static final FactoryConfig<Boolean> searchCreativeTab = CLIENT_STORAGE.register(createBoolean("searchCreativeTab", false));
    public static final FactoryConfig<Integer> autoSaveInterval = CLIENT_STORAGE.register(createInteger("autoSaveInterval",(c,i)-> i == 0 ? Options.genericValueLabel(c,Component.translatable("options.off")) :Component.translatable( "bedrock4j.options.mins_value",c, i * 5),0, ()-> 24,1, i->{/*? if >1.20.1 {*/if (Minecraft.getInstance().hasSingleplayerServer()) Minecraft.getInstance().getSingleplayerServer().onTickRateChanged();/*?}*/}));
    public static final FactoryConfig<Boolean> autoSaveWhenPaused = CLIENT_STORAGE.register(createBoolean("autoSaveWhenPaused",false));
    public static final FactoryConfig<Boolean> inGameTooltips = CLIENT_STORAGE.register(createBoolean("gameTooltips", true));
    public static final FactoryConfig<Boolean> tooltipBoxes = CLIENT_STORAGE.register(createBoolean("tooltipBoxes", true));
    public static final FactoryConfig<Boolean> hints = CLIENT_STORAGE.register(createBoolean("hints", true));
    public static final FactoryConfig<Boolean> flyingViewRolling = CLIENT_STORAGE.register(createBoolean("flyingViewRolling", true));
    public static final FactoryConfig<Boolean> directSaveLoad = CLIENT_STORAGE.register(createBoolean("directSaveLoad", false));
    public static final FactoryConfig<Boolean> vignette = CLIENT_STORAGE.register(createBoolean("vignette", false));
    public static final FactoryConfig<Boolean> minecartSounds = CLIENT_STORAGE.register(createBoolean("minecartSounds", true));
    public static final FactoryConfig<Boolean> caveSounds = CLIENT_STORAGE.register(createBoolean("caveSounds", true));
    public static final FactoryConfig<Boolean> showVanillaRecipeBook = CLIENT_STORAGE.register(createBoolean("showVanillaRecipeBook", false));
    public static final FactoryConfig<Boolean> displayNameTagBorder = CLIENT_STORAGE.register(createBoolean("displayNameTagBorder", true));
    public static final FactoryConfig<Boolean> invertYController = CLIENT_STORAGE.register(createBoolean("invertYController", false));
    public static final FactoryConfig<Boolean> invertControllerButtons = CLIENT_STORAGE.register(createBoolean("invertControllerButtons", false, (b)-> ControllerBinding.RIGHT_BUTTON.state().block(2)));
    public static final FactoryConfig<Integer> selectedController = CLIENT_STORAGE.register(createInteger("selectedController", (c, i)-> Component.translatable("options.generic_value",c,Component.literal(i+1 + (Bedrock4JClient.controllerManager.connectedController == null ? "" : " (%s)".formatted(Bedrock4JClient.controllerManager.connectedController.getName())))),  0, ()->15, 0, d -> { if (Bedrock4JClient.controllerManager.connectedController!= null) Bedrock4JClient.controllerManager.connectedController.disconnect(Bedrock4JClient.controllerManager);}));
    public static final FactoryConfig<Controller.Handler> selectedControllerHandler = CLIENT_STORAGE.register(create("selectedControllerHandler", (c, h)-> Component.translatable("options.generic_value",c,h.getName()), ()->((List<Controller.Handler>)ControllerManager.handlers.values()), SDLControllerHandler.getInstance(), d-> {
        ControllerBinding.LEFT_STICK.state().block(2);
        if (Bedrock4JClient.controllerManager.connectedController != null) Bedrock4JClient.controllerManager.connectedController.disconnect(Bedrock4JClient.controllerManager);
    }));
    public static final FactoryConfig<Boolean> controllerVirtualCursor = CLIENT_STORAGE.register(createBoolean("controllerVirtualCursor", true, b-> {}));
    public static final FactoryConfig<CursorMode> cursorMode = CLIENT_STORAGE.register(create("cursorMode", (c, d) -> CommonComponents.optionNameValue(c, d.displayName), i-> CursorMode.values()[i], CursorMode::ordinal, ()->CursorMode.values().length, CursorMode.AUTO, d -> Bedrock4JClient.controllerManager.updateCursorMode(), CLIENT_STORAGE));
    public static final FactoryConfig<Boolean> unfocusedInputs = CLIENT_STORAGE.register(createBoolean("unfocusedInputs", false));
    public static final FactoryConfig<Double> leftStickDeadZone = CLIENT_STORAGE.register(createDouble("leftStickDeadZone", BedrockOptions::percentValueLabel, 0.25));
    public static final FactoryConfig<Double> rightStickDeadZone = CLIENT_STORAGE.register(createDouble("rightStickDeadZone", BedrockOptions::percentValueLabel, 0.34));
    public static final FactoryConfig<Double> leftTriggerDeadZone = CLIENT_STORAGE.register(createDouble("leftTriggerDeadZone", BedrockOptions::percentValueLabel, 0.2));
    public static final FactoryConfig<Double> rightTriggerDeadZone = CLIENT_STORAGE.register(createDouble("rightTriggerDeadZone", BedrockOptions::percentValueLabel, 0.2));
    public static final FactoryConfig<Integer> hudScale = CLIENT_STORAGE.register(createInteger("hudScale", Options::genericValueLabel, 1, ()->3, 2));
    public static final FactoryConfig<Double> hudOpacity = CLIENT_STORAGE.register(createDouble("hudOpacity", BedrockOptions::percentValueLabel, 0.8));
    public static final FactoryConfig<Double> hudDistance = CLIENT_STORAGE.register(createDouble("hudDistance", BedrockOptions::percentValueLabel, 1.0));
    public static final FactoryConfig<Double> interfaceResolution = CLIENT_STORAGE.register(createDouble("interfaceResolution", (c, d)-> percentValueLabel(c, 0.5 + d), 0.5, d -> Minecraft.getInstance().execute(Minecraft.getInstance()::resizeDisplay)));
    public static final FactoryConfig<Double> interfaceSensitivity = CLIENT_STORAGE.register(createDouble("interfaceSensitivity", (c, d)-> percentValueLabel(c, d*2), 0.5, d -> {}));
    public static final FactoryConfig<Double> controllerSensitivity = CLIENT_STORAGE.register(FactoryConfig.create("controllerSensitivity", new FactoryConfigDisplay.Instance<>(Component.translatable("options.sensitivity"), (c, d)-> percentValueLabel(c, d*2)), new FactoryConfigControl.FromDouble<>(Codec.DOUBLE, v->v, v->v), 0.5, d -> {}, CLIENT_STORAGE));
    public static final FactoryConfig<Boolean> overrideTerrainFogStart = CLIENT_STORAGE.register(createBoolean("overrideTerrainFogStart", true));
    public static final FactoryConfig<Integer> terrainFogStart = CLIENT_STORAGE.register(createInteger("terrainFogStart", (c,i)-> CommonComponents.optionNameValue(c, Component.translatable("options.chunks", getTerrainFogStart())), 2, ()-> Minecraft.getInstance().options.renderDistance().get(), 4, d -> {}));
    public static final FactoryConfig<Boolean> overrideTerrainFogEnd = CLIENT_STORAGE.register(createBoolean("overrideTerrainFogEnd", true));
    public static final FactoryConfig<Integer> terrainFogEnd = CLIENT_STORAGE.register(createInteger("terrainFogEnd", (c,i)-> CommonComponents.optionNameValue(c, Component.translatable("options.chunks", i)), 2, ()-> 32, 16, d -> {}));
    public static final FactoryConfig<ControlType.Holder> selectedControlType = CLIENT_STORAGE.register(FactoryConfig.create("controlType", new FactoryConfigDisplay.Instance<>(optionName("controlType"), (c, i)-> Component.translatable("options.generic_value",c, i.isAuto() ? Component.translatable("bedrock4j.options.auto_value", ControlType.getActiveType().getDisplayName()) : i.get().getDisplayName())), new FactoryConfigControl.FromInt<>(ControlType.Holder.CODEC, i-> i == 0 || ControlType.types.size() < i ? ControlType.Holder.AUTO : ControlType.Holder.of(ControlType.types.getByIndex(i - 1)), s1-> 1 + ControlType.types.indexOf(s1.get()), ()-> ControlType.types.size() + 1), ControlType.Holder.AUTO, v-> {}, CLIENT_STORAGE));
    public static final FactoryConfig<Difficulty> createWorldDifficulty = CLIENT_STORAGE.register(FactoryConfig.create("createWorldDifficulty", new FactoryConfigDisplay.Instance<>(Component.translatable("options.difficulty"), Difficulty::getInfo, (c, d)-> CommonComponents.optionNameValue(c, d.getDisplayName())), new FactoryConfigControl.FromInt<>(Difficulty::byId, Difficulty::getId, ()->Difficulty.values().length), Difficulty.NORMAL, d -> {}, CLIENT_STORAGE));
    public static final FactoryConfig<Boolean> smoothMovement = CLIENT_STORAGE.register(createBoolean("smoothMovement",true));
    public static final FactoryConfig<Boolean> forceSmoothMovement = CLIENT_STORAGE.register(createBoolean("forceSmoothMovement", b-> LegacyComponents.MAY_BE_A_CHEAT,false));
    public static final FactoryConfig<Boolean> legacyCreativeBlockPlacing = CLIENT_STORAGE.register(createBoolean("legacyCreativeBlockPlacing",true));
    public static final FactoryConfig<Boolean> legacyDrownedAnimation = CLIENT_STORAGE.register(createBoolean("legacyDrownedAnimation",true));
    public static final FactoryConfig<Boolean> merchantTradingIndicator = CLIENT_STORAGE.register(createBoolean("merchantTradingIndicator",true));
    public static final FactoryConfig<Boolean> itemLightingInHand = CLIENT_STORAGE.register(createBoolean("itemLightingInHand",true));
    public static final FactoryConfig<Boolean> loyaltyLines = CLIENT_STORAGE.register(createBoolean("loyaltyLines",true));
    public static final FactoryConfig<Boolean> controllerToggleCrouch = CLIENT_STORAGE.register(FactoryConfig.createBoolean("controllerToggleCrouch", new FactoryConfigDisplay.Instance<>(Component.translatable("options.key.toggleSneak")),true, b->{}, CLIENT_STORAGE));;
    public static final FactoryConfig<Boolean> controllerToggleSprint = CLIENT_STORAGE.register(FactoryConfig.createBoolean("controllerToggleSprint", new FactoryConfigDisplay.Instance<>(Component.translatable("options.key.toggleSprint")),false, b-> {}, CLIENT_STORAGE));
    public static final FactoryConfig<Boolean> lockControlTypeChange = CLIENT_STORAGE.register(createBoolean("lockControlTypeChange",false));
    public static final FactoryConfig<Integer> selectedItemTooltipLines = CLIENT_STORAGE.register(createInteger("selectedItemTooltipLines", Options::genericValueLabel, 0,()->6, 1));
    public static final FactoryConfig<Boolean> itemTooltipEllipsis = CLIENT_STORAGE.register(createBoolean("itemTooltipEllipsis",false));
    public static final FactoryConfig<Integer> selectedItemTooltipSpacing = CLIENT_STORAGE.register(createInteger("selectedItemTooltipSpacing", Options::genericValueLabel, 8,()->12, 12));
    public static final FactoryConfig<VehicleCameraRotation> vehicleCameraRotation = CLIENT_STORAGE.register(create("vehicleCameraRotation", (c, d) -> CommonComponents.optionNameValue(c, d.displayName), i-> VehicleCameraRotation.values()[i], VehicleCameraRotation::ordinal, ()->VehicleCameraRotation.values().length, VehicleCameraRotation.ONLY_NON_LIVING_ENTITIES, d -> {}, CLIENT_STORAGE));
    public static final FactoryConfig<Boolean> defaultParticlePhysics = CLIENT_STORAGE.register(createBoolean("defaultParticlePhysics", true));
    public static final FactoryConfig<Boolean> linearCameraMovement = CLIENT_STORAGE.register(createBoolean("linearCameraMovement", false));
    public static final FactoryConfig<Boolean> legacyOverstackedItems = CLIENT_STORAGE.register(createBoolean("legacyOverstackedItems", true));
    public static final FactoryConfig<Boolean> displayMultipleControlsFromAction = CLIENT_STORAGE.register(createBoolean("displayMultipleControlsFromAction", false));
    public static final FactoryConfig<Boolean> enhancedPistonMovingRenderer = CLIENT_STORAGE.register(createBoolean("enhancedPistonMovingRenderer", true));
    public static final FactoryConfig<Boolean> legacyEntityFireTint = CLIENT_STORAGE.register(createBoolean("legacyEntityFireTint", true));
    public static final FactoryConfig<Boolean> advancedHeldItemTooltip = CLIENT_STORAGE.register(createBoolean("advancedHeldItemTooltip", false));
    public static final FactoryConfig<AdvancedOptionsMode> advancedOptionsMode = CLIENT_STORAGE.register(create("advancedOptionsMode", (c, d) -> CommonComponents.optionNameValue(c, d.displayName), i-> AdvancedOptionsMode.values()[i], AdvancedOptionsMode::ordinal, ()->AdvancedOptionsMode.values().length, AdvancedOptionsMode.DEFAULT, d -> {}, CLIENT_STORAGE));
    public static final FactoryConfig<Boolean> autoSaveCountdown = CLIENT_STORAGE.register(createBoolean("autoSaveCountdown", true));
    public static final FactoryConfig<Boolean> displayControlTooltips = CLIENT_STORAGE.register(createBoolean("displayControlTooltips", true));
    public static final FactoryConfig<Boolean> systemMessagesAsOverlay = CLIENT_STORAGE.register(createBoolean("systemMessagesAsOverlay", false));
    public static final FactoryConfig<Boolean> headFollowsTheCamera = CLIENT_STORAGE.register(createBoolean("headFollowsTheCamera", true));
    public static final FactoryConfig<Boolean> invertedFrontCameraPitch = CLIENT_STORAGE.register(createBoolean("invertedFrontCameraPitch", true, b-> {}));
    public static final FactoryConfig<Boolean> legacySkyShape = CLIENT_STORAGE.register(createBoolean("legacySkyShape", true, b-> Bedrock4JClient.updateSkyShape()));
    public static final FactoryConfig<Boolean> fastLeavesCustomModels = CLIENT_STORAGE.register(createBoolean("fastLeavesCustomModels", true, b-> Bedrock4JClient.updateChunks()));
    public static final FactoryConfig<Boolean> skipIntro = CLIENT_STORAGE.register(createBoolean("skipIntro", false));
    public static final FactoryConfig<Boolean> legacyIntroAndLoading = CLIENT_STORAGE.register(createBoolean("legacyIntroAndLoading", true));
    public static final FactoryConfig<Boolean> skipInitialSaveWarning = CLIENT_STORAGE.register(createBoolean("skipInitialSaveWarning", false));
    public static final FactoryConfig<Boolean> titleScreenFade = CLIENT_STORAGE.register(createBoolean("titleScreenFade", false));
    public static final FactoryConfig<Boolean> titleScreenVersionText = CLIENT_STORAGE.register(createBoolean("titleScreenVersionText", true));
    public static final FactoryConfig<Boolean> legacyEvokerFangs = CLIENT_STORAGE.register(createBoolean("legacyEvokerFangs", true));
    public static final FactoryConfig<Boolean> vanillaTutorial = CLIENT_STORAGE.register(createBoolean("vanillaTutorial", false, o->{
        if (Minecraft.getInstance().level != null && !o) {
            Minecraft.getInstance().getTutorial().stop();
        }
    }));
    public static final FactoryConfig<Boolean> mapsWithCoords = CLIENT_STORAGE.register(createBoolean("mapsWithCoords", true));
    public static final FactoryConfig<Boolean> legacyPanorama = CLIENT_STORAGE.register(createBoolean("legacyPanorama", true));
    public static final FactoryConfig<Boolean> cursorAtFirstInventorySlot = CLIENT_STORAGE.register(createBoolean("cursorAtFirstInventorySlot", false));
    public static final FactoryConfig<Boolean> controllerCursorAtFirstInventorySlot = CLIENT_STORAGE.register(FactoryConfig.createBoolean("controllerCursorAtFirstInventorySlot", new FactoryConfigDisplay.Instance<>(Component.translatable("bedrock4j.options.cursorAtFirstInventorySlot")),true, b->{}, CLIENT_STORAGE));
    public static final FactoryConfig<Boolean> systemCursor = CLIENT_STORAGE.register(createBoolean("systemCursor", true, b-> Bedrock4JClient.controllerManager.updateCursorInputMode()));

    public static int getTerrainFogStart(){
        return Math.min(terrainFogStart.get(), Minecraft.getInstance().options.renderDistance().get());
    }

    public static boolean hasSystemCursor(){
        return systemCursor.get() && !Bedrock4JClient.controllerManager.isControllerTheLastInput();
    }

    public enum VehicleCameraRotation implements StringRepresentable {
        NONE("none", LegacyComponents.NONE),ALL_ENTITIES("all_entities"),ONLY_NON_LIVING_ENTITIES("only_non_living_entities"),ONLY_LIVING_ENTITIES("only_living_entities");
        public static final EnumCodec<VehicleCameraRotation> CODEC = StringRepresentable.fromEnum(VehicleCameraRotation::values);
        private final String name;
        public final Component displayName;

        VehicleCameraRotation(String name, Component displayName){
            this.name = name;
            this.displayName = displayName;
        }
        VehicleCameraRotation(String name){
            this(name,Component.translatable("bedrock4j.options.vehicleCameraRotation."+name));
        }
        public boolean isForLivingEntities(){
            return this == ALL_ENTITIES || this == ONLY_LIVING_ENTITIES;
        }
        public boolean isForNonLivingEntities(){
            return this == ALL_ENTITIES || this == ONLY_NON_LIVING_ENTITIES;
        }
        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public enum AdvancedOptionsMode implements StringRepresentable {
        DEFAULT("default"),MERGE("merge"),HIDE("hide");
        public static final EnumCodec<AdvancedOptionsMode> CODEC = StringRepresentable.fromEnum(AdvancedOptionsMode::values);
        private final String name;
        public final Component displayName;

        AdvancedOptionsMode(String name, Component displayName){
            this.name = name;
            this.displayName = displayName;
        }
        AdvancedOptionsMode(String name) {
            this(name, Component.translatable("bedrock4j.options.advancedOptionsMode." + name));
        }
        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public enum CursorMode implements StringRepresentable {
        AUTO("auto"),ALWAYS("always"),NEVER("never");
        public static final EnumCodec<CursorMode> CODEC = StringRepresentable.fromEnum(CursorMode::values);
        private final String name;
        public final Component displayName;

        CursorMode(String name, Component displayName){
            this.name = name;
            this.displayName = displayName;
        }

        CursorMode(String name){
            this(name, Component.translatable("bedrock4j.options.cursorMode."+name));
        }

        public boolean isAuto(){
            return this == AUTO;
        }

        public boolean isAlways(){
            return this == ALWAYS;
        }

        public boolean isNever(){
            return this == NEVER;
        }
        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
