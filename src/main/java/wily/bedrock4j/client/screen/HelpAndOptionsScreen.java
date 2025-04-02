package wily.bedrock4j.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.PlayerModelPart;
import wily.factoryapi.base.ArbitrarySupplier;
import wily.bedrock4j.client.BedrockOptions;
import wily.bedrock4j.util.BedrockSprites;

import java.util.ArrayList;
import java.util.List;

public class HelpAndOptionsScreen extends RenderableVListScreen {
    //public static final List<Function<Screen, AbstractButton>> HOW_TO_PLAY_BUTTONS = new ArrayList<>(List.of(s->RenderableVListScreen.openScreenButton(Component.literal("Minecraft Wiki"),()->ConfirmationScreen.createLinkScreen(s,"https://minecraft.wiki/")).build(), s->RenderableVListScreen.openScreenButton(Component.literal("Legacy4J Wiki"),()->ConfirmationScreen.createLinkScreen(s,"https://github.com/Wilyicaro/Legacy-Minecraft/wiki")).build(),s->RenderableVListScreen.openScreenButton(Component.translatable("bedrock4j.options.hints"),()->new ItemViewerScreen(s,s1->Panel.centered(s1,325,180), CommonComponents.EMPTY)).build()));

    public static List<AbstractWidget> createPlayerSkinWidgets(){
        List<AbstractWidget> list = new ArrayList<>();
        for (PlayerModelPart p : PlayerModelPart.values()) {
            list.add(new Toggle(0,0,Minecraft.getInstance().options.isModelPartEnabled(p), b->p.getName(), b->null, t->Minecraft.getInstance().options./*? if <1.21.2 {*/toggleModelPart/*?} else {*//*setModelPart*//*?}*/(p,t.selected)));
        }
        list.add(LegacyConfigWidgets.createWidget(BedrockOptions.of(Minecraft.getInstance().options.mainHand())));
        return list;
    }

    public static ScreenSection<?> CHANGE_SKIN = new OptionsScreen.Section(Component.translatable("bedrock4j.menu.change_skin"), s->Panel.centered(s, 250,150), new ArrayList<>(List.of(o-> o.renderableVList.renderables.addAll(createPlayerSkinWidgets()))));
    public static final OptionsScreen.Section HOW_TO_PLAY = new OptionsScreen.Section(Component.translatable("bedrock4j.menu.how_to_play"), s->Panel.centered(s, BedrockSprites.PANEL, 240, Math.min(7, (int)HowToPlayScreen.Section.getWithButton().count())*25+24, 0, 20), new ArrayList<>(List.of(o-> HowToPlayScreen.Section.getWithButton().forEach(s-> o.getRenderableVList().addRenderable(s.createButtonBuilder(o).build())))), ArbitrarySupplier.empty(), ((screen, section) -> new OptionsScreen(screen, section){
        @Override
        public void renderableVListInit() {
            getRenderableVList().cyclic(false).layoutSpacing(l->5).init(panel.x + (panel.width - 225) / 2,panel.getY() + 8,225,panel.getHeight()-16);
        }
    }));

    public HelpAndOptionsScreen(Screen parent) {
        super(parent,Component.translatable("options.title"), r-> {});
        renderableVList.addRenderable(CHANGE_SKIN.createButtonBuilder(this).build());
        renderableVList.addRenderable(HOW_TO_PLAY.createButtonBuilder(this).build());
        renderableVList.addRenderable(openScreenButton(Component.translatable("controls.title"),()-> new RenderableVListScreen(this,Component.translatable("controls.title"),r->r.addRenderables(Button.builder(Component.translatable("options.mouse_settings.title"), button -> this.minecraft.setScreen(new OptionsScreen(r.getScreen(), s-> Panel.centered(s, 250, 130),Component.translatable("options.mouse_settings.title"), BedrockOptions.of(minecraft.options.invertYMouse()), BedrockOptions.of(Minecraft.getInstance().options.sensitivity()), BedrockOptions.of(minecraft.options.mouseWheelSensitivity()), BedrockOptions.of(minecraft.options.discreteMouseScroll()), BedrockOptions.of(minecraft.options.touchscreen()), BedrockOptions.cursorAtFirstInventorySlot, BedrockOptions.systemCursor))).build(),Button.builder(Component.translatable("controls.keybinds.title"), button -> this.minecraft.setScreen(new BedrockKeyMappingScreen(r.getScreen()))).build(),Button.builder(Component.translatable("bedrock4j.options.selectedController"), button -> this.minecraft.setScreen(new ControllerMappingScreen(r.getScreen()))).build()))).build());
        renderableVList.addRenderable(openScreenButton(Component.translatable("bedrock4j.menu.settings"),()->new SettingsScreen(this)).build());
        renderableVList.addRenderable(openScreenButton(Component.translatable("credits_and_attribution.button.credits"),()->new RenderableVListScreen(this,Component.translatable("credits_and_attribution.screen.title"),r-> r.addRenderables(openScreenButton(Component.translatable("credits_and_attribution.button.credits"),()->new WinScreen(false, () -> this.minecraft.setScreen(r.getScreen()))).build(),Button.builder(Component.translatable("credits_and_attribution.button.attribution"), b-> Minecraft.getInstance().setScreen(ConfirmationScreen.createLinkScreen(r.getScreen(), "https://aka.ms/MinecraftJavaAttribution"))).build(),Button.builder(Component.translatable("credits_and_attribution.button.licenses"), b-> Minecraft.getInstance().setScreen(ConfirmationScreen.createLinkScreen(r.getScreen(), "https://aka.ms/MinecraftJavaLicenses"))).build()))).build());
    }

}
