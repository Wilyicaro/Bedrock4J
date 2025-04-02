package wily.bedrock4j.client.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import wily.bedrock4j.util.LegacyComponents;

public class ServerOptionsScreen extends ConfirmationScreen{
    protected final PlayGameScreen parent;
    private final ServerData serverData;

    public ServerOptionsScreen(PlayGameScreen parent, ServerData selectedServer) {
        super(parent, 230, 107, LegacyComponents.SERVER_OPTIONS, LegacyComponents.SERVER_OPTIONS_MESSAGE, (b)->{});
        this.parent = parent;
        this.serverData = selectedServer;
    }

    @Override
    protected void addButtons() {
        renderableVList.addRenderable(Button.builder(Component.translatable("gui.cancel"), b-> minecraft.setScreen(parent)).bounds(panel.x + 15, panel.y + panel.height - 74,200,20).build());
        renderableVList.addRenderable(Button.builder(Component.translatable("addServer.title"),b-> minecraft.setScreen(new ServerEditScreen(parent, serverData,false))).bounds(panel.x + 15, panel.getRectangle().bottom() - 52,200,20).build());
        renderableVList.addRenderable(Button.builder(Component.translatable("selectServer.delete"),b-> minecraft.setScreen(new ConfirmationScreen(parent, Component.translatable("selectServer.delete"), Component.translatable("selectServer.deleteQuestion"), b1-> {
            parent.getServers().remove(serverData);
            parent.getServers().save();
            parent.serverRenderableList.updateServers();
            minecraft.setScreen(parent);
        }))).bounds(panel.x + 15, panel.getRectangle().bottom() - 30,200,20).build());
    }
}
