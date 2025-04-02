package wily.bedrock4j.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.io.FileUtils;
import wily.factoryapi.base.client.FactoryGuiGraphics;
import wily.factoryapi.base.client.UIAccessor;
import wily.factoryapi.util.FactoryScreenUtil;
import wily.bedrock4j.Bedrock4J;
import wily.bedrock4j.Bedrock4JClient;
import wily.bedrock4j.client.BedrockOptions;
import wily.bedrock4j.client.LegacyWorldTemplate;
import wily.bedrock4j.util.LegacyComponents;
import wily.bedrock4j.util.ScreenUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Consumer;

public class CreationList extends RenderableVList{
    protected final Minecraft minecraft;

    public CreationList(UIAccessor accessor) {
        super(accessor);
        layoutSpacing(l->0);
        minecraft = Minecraft.getInstance();
        addIconButton(this, Bedrock4J.createModLocation("creation_list/create_world"),Component.translatable("bedrock4j.menu.create_world"), c-> CreateWorldScreen.openFresh(this.minecraft, getScreen()));
        LegacyWorldTemplate.list.forEach(t-> addIconButton(this,t.icon(),t.buttonMessage(), c-> {
            if (t.isGamePath() && !Files.exists(t.getPath())){
                minecraft.setScreen(ConfirmationScreen.createInfoScreen(getScreen(), LegacyComponents.MISSING_WORLD_TEMPLATE, Component.translatable("bedrock4j.menu.missing_world_template_message",t.buttonMessage())));
                return;
            }
            try (LevelStorageSource.LevelStorageAccess access = Bedrock4JClient.getLevelStorageSource().createAccess(Bedrock4JClient.importSaveFile(t.open(), minecraft.getLevelSource()::levelExists, Bedrock4JClient.getLevelStorageSource(),t.folderName()))) {
                LevelSummary summary = access.getSummary(/*? if >1.20.2 {*/access.getDataTag()/*?}*/);
                access.close();
                if (t.directJoin()) {
                    LoadSaveScreen.loadWorld(getScreen(), minecraft, Bedrock4JClient.getLevelStorageSource(), summary);
                } else minecraft.setScreen(new LoadSaveScreen(getScreen(),summary,access,t.isLocked()) {
                    @Override
                    public void onClose() {
                        FileUtils.deleteQuietly(access.getDimensionPath(Level.OVERWORLD).toFile());
                        super.onClose();
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    public static void addIconButton(RenderableVList list, ResourceLocation iconSprite, Component message, Consumer<AbstractButton> onPress){
        addIconButton(list,iconSprite,message,onPress,null);
    }
    public static void addIconButton(RenderableVList list, ResourceLocation iconSprite, Component message, Consumer<AbstractButton> onPress, Tooltip tooltip){
        AbstractButton button;
        list.addRenderable(button = new AbstractButton(0,0,270,30,message) {
            @Override
            protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
                super.renderWidget(guiGraphics, i, j, f);
                if (!list.accessor.getBoolean("allowButtonsWithIcons",true)) return;
                FactoryScreenUtil.enableBlend();
                FactoryGuiGraphics.of(guiGraphics).blitSprite(iconSprite, getX() + 5, getY() + 5, 20, 20);
                FactoryScreenUtil.disableBlend();
                if (Minecraft.getInstance().options.touchscreen().get().booleanValue() || isHovered) {
                    guiGraphics.fill(getX() + 5, getY() + 5, getX() + 25, getY() + 25, -1601138544);
                }
            }
            @Override
            protected void renderScrollingString(GuiGraphics guiGraphics, Font font, int i, int j) {
                int k = this.getX() + 35;
                int l = this.getX() + this.getWidth();
                ScreenUtil.renderScrollingString(guiGraphics, font, this.getMessage(), k, this.getY(), l, this.getY() + this.getHeight(), j, true);
            }
            @Override
            public void onPress() {
                onPress.accept(this);
            }

            @Override
            protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
                defaultButtonNarrationText(narrationElementOutput);
            }
        });
        button.setTooltip(tooltip);
    }

}
