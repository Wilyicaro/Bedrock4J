package wily.bedrock4j.client.screen;

import com.mojang.realmsclient.RealmsMainScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import wily.bedrock4j.Bedrock4JClient;
import wily.bedrock4j.client.BedrockOptions;

public class ExitConfirmationScreen extends ConfirmationScreen {
    public ExitConfirmationScreen(Screen parent) {
        super(parent, 230, Bedrock4JClient.hasSaveSystem(Minecraft.getInstance()) ? 120 : 97, Component.translatable("menu.quit"), Minecraft.getInstance().hasSingleplayerServer() && BedrockOptions.autoSaveInterval.get() == 0 ? Component.translatable("bedrock4j.menu.exit_message") : Minecraft.getInstance().screen instanceof TitleScreen ? Component.translatable("bedrock4j.menu.gameExitMessage") : Component.translatable("bedrock4j.menu.server_exit_message"), b-> {});
    }

    @Override
    protected void addButtons() {
        renderableVList.addRenderable(Button.builder(Component.translatable("gui.cancel"), b-> this.onClose()).build());
        if (Bedrock4JClient.hasSaveSystem(minecraft)) {
            renderableVList.addRenderable(Button.builder(Component.translatable("bedrock4j.menu.exit_and_save"), b -> exit(minecraft, true)).build());
            renderableVList.addRenderable(Button.builder(Component.translatable("bedrock4j.menu.exit_without_save"), b-> minecraft.setScreen(new ConfirmationScreen(this,Component.translatable("bedrock4j.menu.exit_without_save_title"),Component.translatable("bedrock4j.menu.exit_without_save_message"), b1-> exit(minecraft, false)))).build());
        }else renderableVList.addRenderable(Button.builder(Component.translatable( "menu.quit"), b-> exit(minecraft, minecraft.hasSingleplayerServer() && (minecraft.getSingleplayerServer().isHardcore() || !Bedrock4JClient.isCurrentWorldSource(minecraft.getSingleplayerServer().storageSource)))).build());
    }

    public static void exit(Minecraft minecraft, boolean save) {
        if (minecraft.getConnection() == null){
            minecraft.stop();
            return;
        }

        if (save) Bedrock4JClient.saveExit = Bedrock4JClient.retakeWorldIcon = true;
        //? if <=1.20.2
        /*boolean wasInRealms = minecraft.isConnectedToRealms();*/
        if (minecraft.level != null) {
            minecraft.level.disconnect();
        }
        minecraft.getSoundManager().stop();

        minecraft./*? if >1.20.2 {*/disconnect/*?} else {*//*clearLevel*//*?}*/(new BedrockLoadingScreen(Component.translatable(save ? "menu.savingLevel": "disconnect.quitting"),Component.empty()));
        ServerData serverData = minecraft.getCurrentServer();
        TitleScreen mainMenuScreen = new TitleScreen();
        if (serverData != null && /*? if >1.20.2 {*/serverData.isRealm()/*?} else {*//*wasInRealms*//*?}*/) {
            minecraft.setScreen(new RealmsMainScreen(mainMenuScreen));
        } else {
            minecraft.setScreen(mainMenuScreen);
        };
    }
}
