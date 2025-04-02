package wily.bedrock4j.client.screen;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import wily.factoryapi.base.Bearer;
import wily.bedrock4j.client.CommonColor;
import wily.bedrock4j.client.KnownListing;
import wily.bedrock4j.util.LegacyComponents;
import wily.bedrock4j.util.ScreenUtil;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ConfirmationScreen extends OverlayPanelScreen implements RenderableVList.Access{
    protected final MultiLineLabel messageLabel;
    protected Consumer<ConfirmationScreen> okAction;
    public Button okButton;
    protected Bearer<Integer> messageYOffset = Bearer.of(0);
    protected final RenderableVList renderableVList = new RenderableVList(accessor).layoutSpacing(l->2);
    private final List<RenderableVList> renderableVLists = Collections.singletonList(renderableVList);
    protected boolean initialized = false;

    public ConfirmationScreen(Screen parent, Function<Screen,Panel> panelConstructor, Component title, MultiLineLabel messageLabel, Consumer<ConfirmationScreen> okAction) {
        super(parent, panelConstructor, title);
        this.messageLabel = messageLabel;
        this.okAction = okAction;
        this.parent = parent;
    }
    public ConfirmationScreen(Screen parent, int imageWidth, int baseHeight, int xOffset, int yOffset, Component title, MultiLineLabel messageLines, Consumer<ConfirmationScreen> okAction) {
        this(parent,s-> Panel.centered(s, imageWidth, baseHeight + messageLines.getLineCount() * 12, xOffset, yOffset),title,messageLines, okAction);
    }
    public ConfirmationScreen(Screen parent, int imageWidth, int baseHeight, int xOffset, int yOffset, Component title, Component message, Consumer<ConfirmationScreen> okAction) {
        this(parent, imageWidth, baseHeight, xOffset , yOffset, title, MultiLineLabel.create(Minecraft.getInstance().font,message,imageWidth - 30), okAction);
    }
    public ConfirmationScreen(Screen parent, int imageWidth, int baseHeight, Component title, Component message, Consumer<ConfirmationScreen> okAction) {
        this(parent, imageWidth, baseHeight, 0, 0, title, message, okAction);
    }
    public ConfirmationScreen(Screen parent, Component title, Component message, Consumer<ConfirmationScreen> okAction) {
        this(parent,230, 97,title,message,okAction);
    }
    public ConfirmationScreen(Screen parent, int imageWidth, int baseHeight, Component title, Component message) {
        this(parent, imageWidth, baseHeight, title, message, BedrockScreen::onClose);
    }
    public ConfirmationScreen(Screen parent, Component title, Component message) {
        this(parent,230,97,title,message);
    }

    public static ConfirmationScreen createInfoScreen(Screen parent, Component title,Component message) {
        return new ConfirmationScreen(parent,title,message){
            protected void addButtons() {
                renderableVList.addRenderable(okButton = Button.builder(Component.translatable("gui.ok"),b-> okAction.accept(this)).build());
            }
        };
    }

    public static ConfirmationScreen createLinkScreen(Screen parent, String link) {
        return createLinkScreen(parent, LegacyComponents.OPEN_LINK_TITLE, LegacyComponents.OPEN_LINK_MESSAGE, link);
    }

    public static ConfirmationScreen createLinkScreen(Screen parent, Component title, Component message, String link) {
        return new ConfirmationScreen(parent,230,97, title,message, s-> {
            Util.getPlatform().openUri(link);
            s.onClose();
        });
    }

    public static ConfirmationScreen createResetKnownListingScreen(Screen parent, Component title, Component message, KnownListing<?> knownListing) {
        return new ConfirmationScreen(parent,230,97, title,message, s-> {
            knownListing.list.clear();
            knownListing.save();
            s.onClose();
        });
    }

    public static ConfirmationScreen createSaveInfoScreen(Screen parent){
        return new ConfirmationScreen(parent,275,130, 0, 25, Component.empty(), LegacyComponents.AUTOSAVE_MESSAGE, BedrockScreen::onClose){
            protected void addButtons() {
                transparentBackground = false;
                renderableVList.addRenderable(okButton = Button.builder(Component.translatable("gui.ok"), b-> okAction.accept(this)).build());
            }
            @Override
            public void renderableVListInit(){
                messageYOffset.set(68);
                renderableVList.init(panel.x + (panel.width - 220) / 2, panel.y + panel.height - 40,220,0);
            }

            @Override
            public boolean shouldCloseOnEsc() {
                return false;
            }

            public void render(GuiGraphics guiGraphics, int i, int j, float f) {
                super.render(guiGraphics, i, j, f);
                ScreenUtil.drawAutoSavingIcon(guiGraphics,panel.x + 127, panel.y + 36);
            }
        };
    }

    @Override
    protected void init() {
        if (!initialized){
            addButtons();
            initialized = true;
        }
        super.init();
        renderableVListInit();
        accessor.putIntegerBearer("messageYOffset",messageYOffset);
    }

    @Override
    public List<RenderableVList> getRenderableVLists() {
        return renderableVLists;
    }

    public void renderableVListInit(){
        messageYOffset.set(title.getString().isEmpty() ? 15 : 35);
        renderableVList.init(panel.x + (panel.width - 200) / 2, panel.y + panel.height - renderableVList.renderables.size() * 22 - 8,200,0);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (renderableVList.keyPressed(i)) return true;
        return super.keyPressed(i, j, k);
    }

    protected void addButtons(){
        renderableVList.addRenderable(Button.builder(Component.translatable("gui.cancel"), b-> this.onClose()).build());
        renderableVList.addRenderable(okButton = Button.builder(Component.translatable("gui.ok"),b-> okAction.accept(this)).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        ScreenUtil.renderScrollingString(guiGraphics,font,title,panel.x + 15, panel.y + 15,panel.x + panel.width - 15, panel.y + 26, CommonColor.INVENTORY_GRAY_TEXT.get(),false);
        messageLabel.renderLeftAlignedNoShadow(guiGraphics,panel.x + 15, panel.y + messageYOffset.get(), 12, CommonColor.INVENTORY_GRAY_TEXT.get());
    }
}
