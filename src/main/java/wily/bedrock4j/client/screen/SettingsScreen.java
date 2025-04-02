package wily.bedrock4j.client.screen;

import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import wily.bedrock4j.Bedrock4JClient;


import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SettingsScreen extends RenderableVListScreen {
    public static final List<Function<Screen,AbstractButton>> SETTINGS_BUTTONS = OptionsScreen.Section.list.stream().map(section-> (Function<Screen,AbstractButton>) s->section.createButtonBuilder(s).build()).collect(Collectors.toList());

    protected SettingsScreen(Screen parent) {
        super(parent,Component.translatable("bedrock4j.menu.settings"), r->{});
        SETTINGS_BUTTONS.forEach(f->renderableVList.addRenderable(f.apply(this)));
        renderableVList.addRenderable(openScreenButton(Component.translatable("bedrock4j.menu.reset_defaults"),()->new ConfirmationScreen(this,Component.translatable("bedrock4j.menu.reset_settings"),Component.translatable("bedrock4j.menu.reset_message"), b1->{
            Bedrock4JClient.resetOptions(minecraft);
            minecraft.setScreen(this);
        })).build());
    }

}
