package wily.bedrock4j.client.screen;

import com.google.common.collect.Streams;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.VideoMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import wily.factoryapi.FactoryAPIClient;
import wily.factoryapi.base.ArbitrarySupplier;
import wily.factoryapi.base.client.FactoryOptions;
import wily.factoryapi.base.config.FactoryConfig;
import wily.bedrock4j.Bedrock4JClient;
import wily.bedrock4j.client.*;
import wily.bedrock4j.client.controller.ControllerBinding;
import wily.bedrock4j.config.LegacyCommonOptions;
import wily.bedrock4j.util.LegacyComponents;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static wily.bedrock4j.client.screen.ControlTooltip.*;
import static wily.bedrock4j.client.screen.ControlTooltip.getKeyIcon;

public class OptionsScreen extends PanelVListScreen {
    public Screen advancedOptionsScreen;

    public OptionsScreen(Screen parent, Function<Screen, Panel> panelConstructor, Component component) {
        super(parent, panelConstructor, component);
    }

    public OptionsScreen(Screen parent, Section section) {
        this(parent, BedrockOptions.advancedOptionsMode.get() == BedrockOptions.AdvancedOptionsMode.MERGE && section.advancedSection.isPresent() ? section.advancedSection.get().panelConstructor() : section.panelConstructor(), section.title());
        section.elements().forEach(c->c.accept(this));
        section.advancedSection.ifPresent(s-> {
            switch (BedrockOptions.advancedOptionsMode.get()){
                case DEFAULT -> withAdvancedOptions(s.build(this));
                case MERGE -> s.elements().forEach(c->c.accept(this));
            }
        });
    }

    public OptionsScreen(Screen parent, Function<Screen, Panel> panelConstructor, Component component, Renderable... renderables) {
        this(parent, panelConstructor, component);
        renderableVList.addRenderables(renderables);
    }

    public OptionsScreen(Screen parent, Function<Screen, Panel> panelConstructor, Component component, FactoryConfig<?>... options) {
        this(parent, panelConstructor, component);
        renderableVList.addOptions(options);
    }

    public OptionsScreen(Screen parent, Function<Screen, Panel> panelConstructor, Component component, Stream<FactoryConfig<?>> options) {
        this(parent, panelConstructor, component);
        renderableVList.addOptions(options);
    }

    public OptionsScreen withAdvancedOptions(Function<OptionsScreen,Screen> advancedOptionsFunction){
        return withAdvancedOptions(advancedOptionsFunction.apply(this));
    }

    public OptionsScreen withAdvancedOptions(Screen screen){
        advancedOptionsScreen = screen;
        return this;
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (super.keyPressed(i, j, k)) return true;
        if (i == InputConstants.KEY_O && advancedOptionsScreen != null){
            minecraft.setScreen(advancedOptionsScreen);
            return true;
        }
        return false;
    }

    public static void setupSelectorControlTooltips(ControlTooltip.Renderer renderer, Screen screen){
        renderer.add(()-> ControlType.getActiveType().isKbm() ? COMPOUND_ICON_FUNCTION.apply(new ControlTooltip.Icon[]{getKeyIcon(InputConstants.KEY_LSHIFT), PLUS_ICON,getKeyIcon(InputConstants.MOUSE_BUTTON_LEFT)}) : null, ()-> ControlTooltip.getKeyMessage(InputConstants.MOUSE_BUTTON_LEFT,screen));
        renderer.add(ControlTooltip.EXTRA::get, ()-> ControlTooltip.getKeyMessage(InputConstants.KEY_X,screen));
        renderer.add(()-> ControlType.getActiveType().isKbm() ? ControlTooltip.getKeyIcon(InputConstants.KEY_O) : ControllerBinding.UP_BUTTON.getIcon(), ()-> ControlTooltip.getKeyMessage(InputConstants.KEY_O,screen));
    }

    @Override
    public void addControlTooltips(ControlTooltip.Renderer renderer) {
        super.addControlTooltips(renderer);
        setupSelectorControlTooltips(renderer, this);
        renderer.replace(6, i-> i, c-> c == null ? advancedOptionsScreen == null ? null : LegacyComponents.SHOW_ADVANCED_OPTIONS : c);
    }

    public record Section(Component title, Function<Screen,Panel> panelConstructor, List<Consumer<OptionsScreen>> elements, ArbitrarySupplier<Section> advancedSection, BiFunction<Screen,Section,OptionsScreen> sectionBuilder) implements ScreenSection<OptionsScreen>{
        private static final Minecraft mc = Minecraft.getInstance();
        public static final List<Section> list = new ArrayList<>();

        public static OptionInstance<?> createResolutionOptionInstance(){
            Monitor monitor = mc.getWindow().findBestMonitor();
            int j = monitor == null ? -1: mc.getWindow().getPreferredFullscreenVideoMode().map(monitor::getVideoModeIndex).orElse(-1);
            return new OptionInstance<>("options.fullscreen.resolution", OptionInstance.noTooltip(), (component, integer) -> {
                if (monitor == null)
                    return Component.translatable("options.fullscreen.unavailable");
                else if (integer == -1) {
                    return Options.genericValueLabel(component, Component.translatable("options.fullscreen.current"));
                }
                VideoMode videoMode = monitor.getMode(integer);
                return Options.genericValueLabel(component, /*? if >1.20.1 {*/Component.translatable("options.fullscreen.entry", videoMode.getWidth(), videoMode.getHeight(), videoMode.getRefreshRate(), videoMode.getRedBits() + videoMode.getGreenBits() + videoMode.getBlueBits())/*?} else {*//*Component.literal(videoMode.toString())*//*?}*/);
            }, new OptionInstance.IntRange(-1, monitor != null ? monitor.getModeCount() - 1 : -1), j, integer -> {
                if (monitor == null)
                    return;
                mc.getWindow().setPreferredFullscreenVideoMode(integer == -1 ? Optional.empty() : Optional.of(monitor.getMode(integer)));
            });
        }

        public static final Section GAME_OPTIONS = add(new Section(Component.translatable("bedrock4j.menu.game_options"),s->Panel.centered(s, 250,162), new ArrayList<>(List.of(o-> o.renderableVList.addOptions(BedrockOptions.of(mc.options.autoJump()), BedrockOptions.of(mc.options.bobView()), BedrockOptions.flyingViewRolling, BedrockOptions.hints, BedrockOptions.autoSaveInterval), o->o.renderableVList.addRenderables(RenderableVListScreen.openScreenButton(Component.translatable("options.language"), () -> new BedrockLanguageScreen(o, mc.getLanguageManager())).build(),RenderableVListScreen.openScreenButton(Component.translatable("bedrock4j.menu.mods"), () -> new ModsScreen(o)).build()), o-> {if (mc.level == null && !mc.hasSingleplayerServer()) o.renderableVList.addOptions(BedrockOptions.createWorldDifficulty);})), ()-> Section.ADVANCED_GAME_OPTIONS));
        public static final Section ADVANCED_GAME_OPTIONS = new Section(Component.translatable("bedrock4j.menu.settings.advanced_options",GAME_OPTIONS.title()),s->Panel.centered(s,250,172), new ArrayList<>(List.of(o-> o.renderableVList.addOptions(BedrockOptions.selectedControlType, BedrockOptions.lockControlTypeChange, BedrockOptions.unfocusedInputs, BedrockOptions.autoSaveWhenPaused, BedrockOptions.directSaveLoad, BedrockOptions.legacyCreativeBlockPlacing, BedrockOptions.cursorMode, BedrockOptions.invertedFrontCameraPitch, BedrockOptions.skipIntro, BedrockOptions.skipInitialSaveWarning, BedrockOptions.vanillaTutorial, BedrockOptions.of(mc.options.realmsNotifications()), BedrockOptions.of(mc.options.allowServerListing())), o-> {if (mc.level == null) LegacyCommonOptions.COMMON_STORAGE.configMap.values().forEach(c-> o.getRenderableVList().addRenderable(LegacyConfigWidgets.createWidget(c, b-> c.sync())));}, o-> o.renderableVList.addRenderables(RenderableVListScreen.openScreenButton(LegacyComponents.RESET_KNOWN_BLOCKS_TITLE,()-> ConfirmationScreen.createResetKnownListingScreen(o, LegacyComponents.RESET_KNOWN_BLOCKS_TITLE,LegacyComponents.RESET_KNOWN_BLOCKS_MESSAGE, Bedrock4JClient.knownBlocks)).build(),RenderableVListScreen.openScreenButton(LegacyComponents.RESET_KNOWN_ENTITIES_TITLE,()-> ConfirmationScreen.createResetKnownListingScreen(o, LegacyComponents.RESET_KNOWN_ENTITIES_TITLE,LegacyComponents.RESET_KNOWN_ENTITIES_MESSAGE, Bedrock4JClient.knownEntities)).build()))));
        public static final Section AUDIO = add(new Section(Component.translatable("bedrock4j.menu.audio"),s->Panel.centered(s,250,88,0,-30), new ArrayList<>(List.of(o->o.renderableVList.addOptions(Streams.concat(Arrays.stream(SoundSource.values()).filter(s->s.ordinal() <= 1).sorted(Comparator.comparingInt(s->s == SoundSource.MUSIC ? 0 : 1)).map(s-> BedrockOptions.of(mc.options.getSoundSourceOptionInstance(s))), Stream.of(BedrockOptions.caveSounds, BedrockOptions.minecartSounds))))), ()-> Section.ADVANCED_AUDIO));
        public static final Section ADVANCED_AUDIO = new Section(Component.translatable("bedrock4j.menu.settings.advanced_options",AUDIO.title()),s->Panel.centered(s,250,198,0,30), new ArrayList<>(List.of(o->o.renderableVList.addOptions(Stream.concat(Stream.of(mc.options.soundDevice()), Arrays.stream(SoundSource.values()).filter(ss->ss.ordinal() > 1).map(mc.options::getSoundSourceOptionInstance)).map(BedrockOptions::of)))));
        public static final Section GRAPHICS = add(new Section(Component.translatable("bedrock4j.menu.graphics"),s->Panel.centered(s, 250,222, 0, 24), new ArrayList<>(List.of(o->o.renderableVList.addOptions(BedrockOptions.of(mc.options.cloudStatus()), BedrockOptions.of(mc.options.graphicsMode())), o->o.renderableVList.addOptions(BedrockOptions.of(mc.options.gamma()), BedrockOptions.of(mc.options.ambientOcclusion())))), ()-> Section.ADVANCED_GRAPHICS,(p, s)-> {
            GlobalPacks.Selector globalPackSelector = GlobalPacks.Selector.resources(0,0,230,45,false);
            PackAlbum.Selector selector = PackAlbum.Selector.resources(0,0,230,45,false);
            OptionsScreen screen = new OptionsScreen(p, s){
                int selectorTooltipVisibility = 0;
                boolean startedSelectorVisibility = false;
                @Override
                public void onClose() {
                    super.onClose();
                    globalPackSelector.applyChanges();
                    selector.applyChanges(true);
                }

                @Override
                protected void panelInit() {
                    super.panelInit();
                    panel.x-=Math.round(Math.min(10,getSelectorTooltipVisibility()) / 10f * 80);
                }

                private float getSelectorTooltipVisibility(){
                    return selectorTooltipVisibility == 0 ? selectorTooltipVisibility : selectorTooltipVisibility + FactoryAPIClient.getPartialTick();
                }

                @Override
                public void renderDefaultBackground(GuiGraphics guiGraphics, int i, int j, float f) {
                    if (selectorTooltipVisibility < 10 && startedSelectorVisibility) repositionElements();
                    super.renderDefaultBackground(guiGraphics, i, j, f);
                    if (selectorTooltipVisibility > 0){
                        if (getFocused() != globalPackSelector) selector.renderTooltipBox(guiGraphics, panel, Math.round((1 - (Math.min(10, getSelectorTooltipVisibility())) / 10f) * -161));
                        else globalPackSelector.renderTooltipBox(guiGraphics, panel, Math.round((1 - (Math.min(10, getSelectorTooltipVisibility())) / 10f) * -161));
                        guiGraphics.pose().translate(0, 0, 0.03f);
                    }
                }

                @Override
                public void tick() {
                    if (!startedSelectorVisibility && (getFocused() == selector || getFocused() == globalPackSelector)) startedSelectorVisibility = true;
                    if (startedSelectorVisibility && selectorTooltipVisibility < 10){
                        selectorTooltipVisibility++;
                    }
                    super.tick();
                }
            };
            screen.renderableVList.addRenderables(globalPackSelector, selector);
            return screen;
        }));
        public static final Section ADVANCED_GRAPHICS = new Section(Component.translatable("bedrock4j.menu.settings.advanced_options",GRAPHICS.title()),s->Panel.centered(s, 250,215,0,20), new ArrayList<>(List.of(o->o.renderableVList.addOptions(BedrockOptions.of(createResolutionOptionInstance()), BedrockOptions.of(mc.options.biomeBlendRadius()), BedrockOptions.of(mc.options.renderDistance()), BedrockOptions.of(mc.options.prioritizeChunkUpdates()), BedrockOptions.of(mc.options.simulationDistance()), BedrockOptions.overrideTerrainFogStart, BedrockOptions.terrainFogStart, BedrockOptions.overrideTerrainFogEnd, BedrockOptions.terrainFogEnd, BedrockOptions.legacySkyShape, BedrockOptions.fastLeavesCustomModels, BedrockOptions.mapsWithCoords, BedrockOptions.of(mc.options.framerateLimit()), BedrockOptions.of(mc.options.enableVsync())/*? if >=1.21.2 {*//*,LegacyOptions.of(mc.options.inactivityFpsLimit())*//*?}*/, BedrockOptions.displayNameTagBorder, BedrockOptions.itemLightingInHand, BedrockOptions.loyaltyLines, BedrockOptions.merchantTradingIndicator, BedrockOptions.legacyDrownedAnimation, BedrockOptions.legacyEntityFireTint, BedrockOptions.vehicleCameraRotation/*? if >=1.21.2 {*//*,LegacyOptions.create(mc.options.rotateWithMinecart())*//*?}*/, BedrockOptions.defaultParticlePhysics, BedrockOptions.headFollowsTheCamera, BedrockOptions.enhancedPistonMovingRenderer, FactoryOptions.RANDOM_BLOCK_ROTATIONS, BedrockOptions.of(mc.options.fullscreen()), BedrockOptions.of(mc.options.particles()), BedrockOptions.of(mc.options.mipmapLevels()), FactoryOptions.NEAREST_MIPMAP_SCALING, BedrockOptions.of(mc.options.entityShadows()), BedrockOptions.of(mc.options.screenEffectScale()), BedrockOptions.of(mc.options.entityDistanceScaling()), BedrockOptions.of(mc.options.fov()), BedrockOptions.of(mc.options.fovEffectScale()), BedrockOptions.of(mc.options.darknessEffectScale()), BedrockOptions.of(mc.options.glintSpeed()), BedrockOptions.legacyEvokerFangs, BedrockOptions.of(mc.options.glintStrength())), o-> Bedrock4JClient.MIXIN_CONFIGS_STORAGE.configMap.values().forEach(c-> o.getRenderableVList().addRenderable(LegacyConfigWidgets.createWidget(c))))));
        public static final Section USER_INTERFACE = add(new Section(Component.translatable("bedrock4j.menu.user_interface"),s->Panel.centered(s,250,200,0,18), new ArrayList<>(List.of(o->o.renderableVList.addOptions(BedrockOptions.displayHUD, BedrockOptions.displayHand, BedrockOptions.of(mc.options.showAutosaveIndicator()), BedrockOptions.showVanillaRecipeBook, BedrockOptions.tooltipBoxes, BedrockOptions.hudOpacity, BedrockOptions.of(mc.options.attackIndicator()), BedrockOptions.hudDistance), o->o.renderableVList.addOptions(BedrockOptions.create(mc.options.guiScale()), BedrockOptions.inGameTooltips, BedrockOptions.animatedCharacter, BedrockOptions.interfaceSensitivity, BedrockOptions.vanillaTabs, BedrockOptions.searchCreativeTab, BedrockOptions.of(mc.options.operatorItemsTab())), o-> o.getRenderableVList().addOptions(BedrockOptions.vignette, BedrockOptions.of(mc.options.narrator()), BedrockOptions.of(mc.options.showSubtitles()), BedrockOptions.of(mc.options.highContrast())))), ()-> Section.ADVANCED_USER_INTERFACE));
        public static final Section ADVANCED_USER_INTERFACE = new Section(Component.translatable("bedrock4j.menu.settings.advanced_options",USER_INTERFACE.title()), USER_INTERFACE.panelConstructor(), BedrockOptions.legacyCreativeTab, BedrockOptions.selectedItemTooltipLines, BedrockOptions.itemTooltipEllipsis, BedrockOptions.selectedItemTooltipSpacing, BedrockOptions.legacyOverstackedItems, BedrockOptions.displayMultipleControlsFromAction, BedrockOptions.advancedHeldItemTooltip, BedrockOptions.autoSaveCountdown, BedrockOptions.displayControlTooltips, BedrockOptions.systemMessagesAsOverlay, BedrockOptions.legacyIntroAndLoading, BedrockOptions.titleScreenFade, BedrockOptions.titleScreenVersionText, BedrockOptions.legacyPanorama, BedrockOptions.of(mc.options.notificationDisplayTime()), BedrockOptions.of(mc.options.damageTiltStrength()), BedrockOptions.of(mc.options.glintSpeed()), BedrockOptions.of(mc.options.glintStrength()), BedrockOptions.of(mc.options.hideLightningFlash()), BedrockOptions.of(mc.options.darkMojangStudiosBackground()), BedrockOptions.of(mc.options.panoramaSpeed())/*? if >1.20.1 {*/, BedrockOptions.of(mc.options.narratorHotkey())/*?}*/, BedrockOptions.of(mc.options.chatVisibility()), BedrockOptions.of(mc.options.chatColors()), BedrockOptions.of(mc.options.chatLinks()), BedrockOptions.of(mc.options.chatLinksPrompt()), BedrockOptions.of(mc.options.backgroundForChatOnly()), BedrockOptions.of(mc.options.chatOpacity()), BedrockOptions.of(mc.options.textBackgroundOpacity()), BedrockOptions.of(mc.options.chatScale()), BedrockOptions.of(mc.options.chatLineSpacing()), BedrockOptions.of(mc.options.chatDelay()), BedrockOptions.of(mc.options.chatWidth()), BedrockOptions.of(mc.options.chatHeightFocused()), BedrockOptions.of(mc.options.chatHeightUnfocused()), BedrockOptions.of(mc.options.narrator()), BedrockOptions.of(mc.options.autoSuggestions()), BedrockOptions.of(mc.options.hideMatchedNames()), BedrockOptions.of(mc.options.reducedDebugInfo()), BedrockOptions.of(mc.options.onlyShowSecureChat()));

        public static Section add(Section section){
            list.add(section);
            return section;
        }

        public Section(Component title, Function<Screen,Panel> panelConstructor,  List<Consumer<OptionsScreen>> elements, ArbitrarySupplier<Section> advancedSection){
            this(title, panelConstructor, elements, advancedSection, OptionsScreen::new);
        }
        public Section(Component title, Function<Screen,Panel> panelConstructor, List<Consumer<OptionsScreen>> elements){
            this(title, panelConstructor, elements, ArbitrarySupplier.empty());
        }
        public Section(Component title, Function<Screen,Panel> panelConstructor, ArbitrarySupplier<Section> advancedSection, FactoryConfig<?>... options){
            this(title, panelConstructor, new ArrayList<>(List.of(o->o.renderableVList.addOptions(options))), advancedSection);
        }
        public Section(Component title, Function<Screen,Panel> panelConstructor, FactoryConfig<?>... optionInstances){
            this(title, panelConstructor, ArbitrarySupplier.empty(), optionInstances);
        }
        public OptionsScreen build(Screen parent){
            return sectionBuilder.apply(parent,this);
        }
    }
}
