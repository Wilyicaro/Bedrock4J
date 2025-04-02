package wily.bedrock4j.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.Component;
import wily.factoryapi.base.client.FactoryGuiGraphics;
import wily.bedrock4j.client.BedrockOptions;
import wily.bedrock4j.util.BedrockSprites;

import java.util.Locale;

public class BedrockLanguageScreen extends PanelVListScreen {
    public static final Component WARNING_LABEL = Component.translatable("options.languageAccuracyWarning");
    protected String selectedLang;
    public BedrockLanguageScreen(Screen parent, LanguageManager manager) {
        super(parent, s->Panel.createPanel(s, p-> p.appearance(BedrockSprites.PANEL_TRANSLUCENT_MIDDLE, 250, s.height - 90), p-> p.pos(p.centeredLeftPos(s), p.centeredTopPos(s) + 13)), Component.translatable("controls.keybinds.title"));
        String autoCode = getSystemLanguageCode();
        renderableVList.addRenderable(new AbstractButton(0,0,260,20,Component.translatable("bedrock4j.menu.system_language")) {
            @Override
            protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
                super.renderWidget(guiGraphics, i, j, f);
                if (manager.getSelected().equals(autoCode)) setFocused(true);
            }

            @Override
            public void onPress() {
                selectedLang = autoCode;
            }
            @Override
            protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
                defaultButtonNarrationText(narrationElementOutput);
            }
        });
        manager.getLanguages().forEach(((s, languageInfo) -> renderableVList.addRenderable(new AbstractButton(0,0,260,20,languageInfo.toComponent()) {
            @Override
            protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
                super.renderWidget(guiGraphics, i, j, f);
                if (manager.getSelected().equals(s)) setFocused(true);
            }

            @Override
            public void onPress() {
                selectedLang = s;
            }
            @Override
            protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
                defaultButtonNarrationText(narrationElementOutput);
            }
        })));
    }
    public static String getSystemLanguageCode(){
        String auto = Locale.getDefault().toString().toLowerCase(Locale.ENGLISH);
        return Minecraft.getInstance().getLanguageManager().getLanguages().containsKey(auto) ? auto : "en_us";
    }

    @Override
    public void onClose() {
        if (selectedLang != null && !minecraft.getLanguageManager().getSelected().equals(selectedLang)) {
            minecraft.getLanguageManager().setSelected(selectedLang);
            minecraft.options.languageCode = selectedLang;
            this.minecraft.reloadResourcePacks();
            minecraft.options.save();
        }
        super.onClose();
    }

    @Override
    protected void init() {
        panel.init();
        addRenderableOnly(panel);
        getRenderableVList().init(panel.x + 10,panel.y + 30,panel.width - 20,panel.height - 46);
        addRenderableWidget(LegacyConfigWidgets.createWidget(BedrockOptions.of(minecraft.options.forceUnicodeFont()),panel.x + 10, panel.y + 10, panel.width - 20, v->{}));
    }
}
