package wily.bedrock4j.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import wily.bedrock4j.util.BedrockSprites;
import wily.factoryapi.base.client.SimpleLayoutRenderable;
import wily.bedrock4j.client.CommonColor;
import wily.bedrock4j.client.BedrockOptions;
import wily.bedrock4j.util.LegacyComponents;
import wily.bedrock4j.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BedrockOptionsScreen extends OptionsScreen implements TabList.Access {
    protected final TabList tabList = new TabList();
    protected final List<List<Renderable>> renderablesByTab = new ArrayList<>();
    protected final EditBox editBox = new EditBox(Minecraft.getInstance().font, 0, 0, 200, 20, Component.translatable("bedrock4j.menu.filter.search"));


    public BedrockOptionsScreen(Screen screen) {
        super(screen,s-> Panel.centered(s, BedrockSprites.PANEL_TRANSLUCENT_MIDDLE,250,250, 50, 0), CommonComponents.EMPTY);
        tabList.add(0,0,100, 25, 4, null, LegacyComponents.ALL,null, b->resetElements());
        renderablesByTab.add(new ArrayList<>());
        OptionsScreen.Section.list.forEach(this::addOptionSection);
        addActualRenderables();
    }

    protected void addOptionSection(OptionsScreen.Section section){
        tabList.add(0,0,100, 25, 4, null, section.title(),null, b->resetElements());
        section.elements().forEach(c->c.accept(this));
        section.advancedSection().ifPresent(s1-> {
            getRenderableVList().addRenderable(SimpleLayoutRenderable.createDrawString(s1.title(),0,1,200,9, CommonColor.INVENTORY_GRAY_TEXT.get(), false));
            if (s1 == Section.ADVANCED_USER_INTERFACE) getRenderableVList().addOptions(BedrockOptions.advancedOptionsMode);
            s1.elements().forEach(c -> c.accept(this));
        });
        List<Renderable> renderables = List.copyOf(getRenderableVList().renderables);
        getRenderableVList().renderables.clear();
        renderablesByTab.get(0).addAll(renderables);
        renderablesByTab.add(renderables);
    }

    protected void resetElements(){
        getRenderableVList().renderables.clear();
        addActualRenderables();
        getRenderableVList().scrolledList.set(0);
        repositionElements();
    }

    protected void addActualRenderables(){
        String value = editBox.getValue().toLowerCase(Locale.ROOT);
        if (value.isBlank()) {
            getRenderableVList().renderables.addAll(renderablesByTab.get(getTabList().selectedTab));
        } else {
            for (Renderable renderable : renderablesByTab.get(getTabList().selectedTab)) {
                if (renderable instanceof AbstractWidget w && w.getMessage().getString().toLowerCase(Locale.ROOT).contains(value)){
                    getRenderableVList().renderables.add(w);
                }
            }
        }
    }

    @Override
    public void renderDefaultBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        ScreenUtil.renderDefaultBackground(accessor, guiGraphics, false);
    }

    @Override
    protected void init() {
        addRenderableWidget(tabList);
        super.init();
        addRenderableWidget(editBox);
        editBox.setPosition(panel.getX() + (panel.width - editBox.getWidth()) / 2, panel.getY() + 10);
        editBox.setResponder(s->resetElements());
        tabList.init((b, i)->{
            b.setX(panel.x - b.getWidth() + 6);
            b.setY(panel.y + i + 4);
        },true);
    }

    @Override
    public void renderableVListInit() {
        getRenderableVList().init(panel.x + 10,panel.y + 40,panel.width - 20,panel.height-50);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (tabList.controlTab(i)) return true;
        return super.keyPressed(i, j, k);
    }

    @Override
    public TabList getTabList() {
        return tabList;
    }
}
