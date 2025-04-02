package wily.bedrock4j.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.server.LanServer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.compress.utils.FileNameUtils;
import wily.factoryapi.base.client.FactoryGuiGraphics;
import wily.factoryapi.base.client.UIAccessor;
import wily.bedrock4j.Bedrock4JClient;
import wily.bedrock4j.client.CommonColor;
import wily.bedrock4j.client.ControlType;
import wily.bedrock4j.client.screen.compat.FriendsServerRenderableList;
import wily.bedrock4j.client.controller.ControllerBinding;
import wily.bedrock4j.util.BedrockSprites;
import wily.bedrock4j.util.ScreenUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class PlayGameScreen extends PanelVListScreen implements ControlTooltip.Event, TabList.Access{
    private static final Component SAFETY_TITLE = Component.translatable("multiplayerWarning.header").withStyle(ChatFormatting.BOLD);
    private static final Component SAFETY_CONTENT = Component.translatable("multiplayerWarning.message");
    private static final Component SAFETY_CHECK = Component.translatable("multiplayerWarning.check");
    public static final Component DIRECT_CONNECTION = Component.translatable("selectServer.direct");
    public boolean isLoading = false;
    protected final TabList tabList = new TabList().add(30,0,Component.translatable("bedrock4j.menu.load"), b-> repositionElements()).add(30,1,Component.translatable("bedrock4j.menu.create"), b-> repositionElements()).add(30,2,Component.translatable("bedrock4j.menu.join"), b-> {
        if (this.minecraft.options.skipMultiplayerWarning)
            repositionElements();
        else minecraft.setScreen(new ConfirmationScreen(this,SAFETY_TITLE,Component.translatable("bedrock4j.menu.multiplayer_warning").append("\n").append(SAFETY_CONTENT)){
            @Override
            protected void addButtons() {
                renderableVList.addRenderable(Button.builder(SAFETY_CHECK, b-> {
                    this.minecraft.options.skipMultiplayerWarning = true;
                    this.minecraft.options.save();
                    onClose();
                }).bounds(panel.x + (panel.width - 200) / 2, panel.y + panel.height - 52,200,20).build());
                renderableVList.addRenderable(okButton = Button.builder(Component.translatable("gui.ok"),b-> okAction.accept(this)).bounds(panel.x + (panel.width - 200) / 2, panel.y + panel.height - 30,200,20).build());
            }
        });
    });
    private final ServerStatusPinger pinger = new ServerStatusPinger();
    public final SaveRenderableList saveRenderableList = new SaveRenderableList(accessor);
    public final CreationList creationList = new CreationList(accessor);
    protected final ServerRenderableList serverRenderableList = PublishScreen.hasWorldHost() ? new FriendsServerRenderableList(accessor) : new ServerRenderableList(accessor);
    @Override
    public void addControlTooltips(ControlTooltip.Renderer renderer) {
        super.addControlTooltips(renderer);
        renderer.add(()-> ControlType.getActiveType().isKbm() ? ControlTooltip.getKeyIcon(InputConstants.KEY_O) : ControllerBinding.UP_BUTTON.getIcon(),()->ControlTooltip.getKeyMessage(InputConstants.KEY_O,this));
        renderer.add(()-> tabList.selectedTab != 2 ? null : ControlType.getActiveType().isKbm() ? ControlTooltip.getKeyIcon(InputConstants.KEY_X) : ControllerBinding.LEFT_BUTTON.getIcon(),()->DIRECT_CONNECTION);
    }
    public PlayGameScreen(Screen parent, int initialTab) {
        super(s-> Panel.createPanel(s, p-> p.appearance(BedrockSprites.PANEL_TRANSLUCENT_MIDDLE, 300, s.height - 80), p-> p.pos(p.centeredLeftPos(s), p.centeredTopPos(s) + 13)),Component.translatable("bedrock4j.menu.play_game"));
        this.parent = parent;
        tabList.selectedTab = initialTab;
        renderableVLists.clear();
        renderableVLists.add(saveRenderableList);
        renderableVLists.add(creationList);
        renderableVLists.add(serverRenderableList);
    }

    public boolean hasTabList(){
        return accessor.getBoolean("hasTabList",true);
    }

    public PlayGameScreen(Screen parent) {
        this(parent,0);
    }

    public static Screen createAndCheckNewerVersions(Screen parent){
        return new PlayGameScreen(parent);
    }

    protected boolean canNotifyOnlineFriends(){
        return serverRenderableList.hasOnlineFriends() && Util.getMillis() % 1000 < 500;
    }

    @Override
    public TabList getTabList() {
        return tabList;
    }

    @Override
    public void added() {
        super.added();
        serverRenderableList.added();
    }

    @Override
    protected void init() {
        panel.height = Math.min(256,height-52);
        if (hasTabList()) addWidget(tabList);
        panel.init();
        renderableVListInit();
        if (hasTabList()) tabList.init(panel.x,panel.y - 26,panel.width);
    }

    @Override
    public void renderableVListInit() {
        getRenderableVList().init(panel.x + 15,panel.y + 15,270, panel.height - 30 - (tabList.selectedTab == 0 ? 21 : 0));
        if (!hasTabList()) serverRenderableList.init("serverRenderableVList",panel.x + 15,panel.y + 15,270, panel.height - 30 - (tabList.selectedTab == 0 ? 21 : 0));
    }

    @Override
    public void renderDefaultBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        ScreenUtil.renderDefaultBackground(accessor, guiGraphics, false);
        panel.render(guiGraphics,i,j,f);
        if (hasTabList()) tabList.render(guiGraphics,i,j,f);
        if (isLoading)
            ScreenUtil.drawGenericLoading(guiGraphics, panel.x + 112 ,
                    panel.y + 66);
    }

    @Override
    public RenderableVList getRenderableVList() {
        return getRenderableVLists().get(hasTabList() ? tabList.selectedTab : 0);
    }

    @Override
    public void removed() {
        if (this.saveRenderableList != null) {
            SaveRenderableList.resetIconCache();
        }
        serverRenderableList.removed();
        this.pinger.removeAll();
    }

    @Override
    public void tick() {
        super.tick();
        List<LevelSummary> summaries = saveRenderableList.pollLevelsIgnoreErrors();
        if (summaries != saveRenderableList.currentlyDisplayedLevels) {
            saveRenderableList.fillLevels("",summaries);
            repositionElements();
        }
        List<LanServer> list = serverRenderableList.lanServerList.takeDirtyServers();
        if (list != null) {
            if (serverRenderableList.lanServers == null || !new HashSet<>(serverRenderableList.lanServers).containsAll(list)) {
                serverRenderableList.lanServers = list;
                serverRenderableList.updateServers();
                rebuildWidgets();
            }
        }
        this.pinger.tick();
    }




    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (hasTabList() && (tabList.controlTab(i) || tabList.directionalControlTab(i)))  return true;
        if (super.keyPressed(i, j, k)) {
            return true;
        }
        if (i == InputConstants.KEY_F5) {
            if (tabList.selectedTab == 0) {
                saveRenderableList.reloadSaveList();
            } else if (tabList.selectedTab == 2) {
                serverRenderableList.servers.load();
                serverRenderableList.updateServers();
            }
            this.rebuildWidgets();
            return true;
        }
        if (i == InputConstants.KEY_X && tabList.selectedTab == 2){
            EditBox serverBox = new EditBox(Minecraft.getInstance().font, 0,0,200,20,DIRECT_CONNECTION);
            minecraft.setScreen(new ConfirmationScreen(this, 230, 120, serverBox.getMessage(),Component.translatable("addServer.enterIp"), b1->  ConnectScreen.startConnecting(this, minecraft, ServerAddress.parseString(serverBox.getValue()), new ServerData("","",/*? if >1.20.2 {*/ ServerData.Type.OTHER/*?} else {*//*false*//*?}*/), false/*? if >=1.20.5 {*/,null/*?}*/)){
                boolean released = false;
                @Override
                protected void addButtons() {
                    super.addButtons();
                    okButton.active = false;
                }

                @Override
                public boolean charTyped(char c, int i) {
                    if (!released) return false;
                    return super.charTyped(c, i);
                }

                @Override
                public boolean keyReleased(int i2, int j, int k) {
                    if (i2 == i) released = true;
                    return super.keyReleased(i2, j, k);
                }

                @Override
                protected void init() {
                    super.init();
                    serverBox.setPosition(panel.getX() + 15, panel.getY() + 45);
                    serverBox.setResponder(s-> okButton.active = ServerAddress.isValidAddress(s));
                    addRenderableWidget(serverBox);
                }
            });
            return true;
        }
        return false;
    }

    public ServerStatusPinger getPinger() {
        return this.pinger;
    }

    public ServerList getServers() {
        return serverRenderableList.servers;
    }
    public void onFilesDrop(List<Path> list) {
        if (tabList.selectedTab == 0) {
            for (Path path : list) {
                if (!path.getFileName().toString().endsWith(".mcsave") && !path.getFileName().toString().endsWith(".zip")) return;
            }
            String string = list.stream().map(Path::getFileName).map(Path::toString).collect(Collectors.joining(", "));
            minecraft.setScreen(new ConfirmationScreen(this, Component.translatable("bedrock4j.menu.import_save"), Component.translatable("bedrock4j.menu.import_save_message", string), (b) -> {
                list.forEach(p -> {
                    try {
                        Bedrock4JClient.importSaveFile(new FileInputStream(p.toFile()),minecraft.getLevelSource(), FileNameUtils.getBaseName(p.getFileName().toString()));
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                });
                minecraft.setScreen(this);
                saveRenderableList.reloadSaveList();
            }));
        }
    }
}
