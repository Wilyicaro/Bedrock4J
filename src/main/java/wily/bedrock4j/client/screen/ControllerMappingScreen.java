package wily.bedrock4j.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.ArrayUtils;
import wily.factoryapi.base.ArbitrarySupplier;
import wily.factoryapi.base.client.SimpleLayoutRenderable;
import wily.bedrock4j.client.CommonColor;
import wily.bedrock4j.client.ControlType;
import wily.bedrock4j.client.BedrockOptions;
import wily.bedrock4j.client.controller.BindingState;
import wily.bedrock4j.client.controller.ControllerBinding;
import wily.bedrock4j.client.controller.LegacyKeyMapping;
import wily.bedrock4j.util.LegacyComponents;

import java.util.Arrays;
import java.util.Objects;

public class ControllerMappingScreen extends BedrockKeyMappingScreen {
    public ControllerMappingScreen(Screen parent) {
        super(parent, Component.translatable("bedrock4j.options.selectedController"));
    }

    @Override
    public void addButtons() {
        KeyMapping[] keyMappings = ArrayUtils.clone(Minecraft.getInstance().options.keyMappings);
        Arrays.sort(keyMappings);
        String lastCategory = null;
        renderableVList.addRenderable(Button.builder(Component.translatable("bedrock4j.menu.reset_defaults"),button -> minecraft.setScreen(new ConfirmationScreen(this, Component.translatable("bedrock4j.menu.reset_controls"),Component.translatable("bedrock4j.menu.reset_controls_message"), b-> {
            for (KeyMapping keyMapping : keyMappings)
                LegacyKeyMapping.of(keyMapping).setBinding(LegacyKeyMapping.of(keyMapping).getDefaultBinding());
            BedrockOptions.CLIENT_STORAGE.save();
            minecraft.setScreen(this);
        }))).size(240,20).build());
        renderableVList.addOptions(BedrockOptions.controllerToggleCrouch, BedrockOptions.controllerToggleSprint, BedrockOptions.controllerCursorAtFirstInventorySlot, BedrockOptions.selectedController, BedrockOptions.selectedControllerHandler, BedrockOptions.invertControllerButtons, BedrockOptions.controllerSensitivity, BedrockOptions.leftStickDeadZone, BedrockOptions.rightStickDeadZone, BedrockOptions.leftTriggerDeadZone, BedrockOptions.rightTriggerDeadZone);
        for (KeyMapping keyMapping : keyMappings) {
            String category = keyMapping.getCategory();
            if (!Objects.equals(lastCategory, category)) {
                renderableVList.addRenderables(SimpleLayoutRenderable.createDrawString(Component.translatable(category), 1, 4, 240, 13, CommonColor.INVENTORY_GRAY_TEXT.get(), false));
                if (category.equals("key.categories.movement"))
                    renderableVList.addOptions(BedrockOptions.invertYController, BedrockOptions.smoothMovement, BedrockOptions.forceSmoothMovement, BedrockOptions.linearCameraMovement, BedrockOptions.controllerVirtualCursor);
            }
            lastCategory = keyMapping.getCategory();
            renderableVList.addRenderable(new MappingButton(0,0,240,20, LegacyKeyMapping.of(keyMapping)) {
                @Override
                public ControlTooltip.ComponentIcon getIcon() {
                    return mapping.getBinding().getIcon();
                }

                @Override
                public boolean isNone() {
                    return mapping.getBinding() == null;
                }

                @Override
                public void onPress() {
                    ControllerBinding.DOWN_BUTTON.state().block();
                    if (Screen.hasShiftDown() || ControllerBinding.LEFT_STICK_BUTTON.state().pressed){
                        mapping.setBinding(mapping.getDefaultBinding());
                        BedrockOptions.CLIENT_STORAGE.save();
                        setAndUpdateMappingTooltip(ArbitrarySupplier.empty());
                    } else if (!ControlType.getActiveType().isKbm()) {
                        setSelectedMapping(mapping);
                        setAndUpdateMappingTooltip(ControllerMappingScreen.this::getCancelTooltip);
                    }
                }
            });
        }
    }

    @Override
    protected boolean areConflicting(LegacyKeyMapping keyMapping, LegacyKeyMapping comparison){
        return keyMapping.getBinding() == comparison.getBinding();
    }

    protected void setNone(LegacyKeyMapping keyMapping){
        keyMapping.setBinding(null);
        BedrockOptions.CLIENT_STORAGE.save();
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == InputConstants.KEY_ESCAPE && selectedMapping != null) {
            setSelectedMapping(null);
            setAndUpdateMappingTooltip(ArbitrarySupplier.empty());
            return true;
        }
        if (selectedMapping != null) return false;
        return super.keyPressed(i, j, k);
    }

    @Override
    public boolean allowsKey() {
        return false;
    }

    @Override
    public Component getCancelTooltip() {
        return Component.translatable("bedrock4j.options.controllerMappingTooltip", ControlTooltip.CANCEL_BINDING.get().getComponent());
    }

    @Override
    public Component getConflictingTooltip(){
        return LegacyComponents.CONFLICTING_BUTTONS;
    }

    public void applyBinding(ControllerBinding<?> binding) {
        selectedMapping.setBinding(binding);
        BedrockOptions.CLIENT_STORAGE.save();
        setAndUpdateMappingTooltip(ArbitrarySupplier.empty());
        resolveConflictingMappings();
        setSelectedMapping(null);
    }

    @Override
    public void bindingStateTick(BindingState state) {
        if (selectedMapping != null) {
            if (state.is(ControllerBinding.BACK)){
                if (state.canClick() && state.timePressed >= state.getDefaultDelay())
                    applyBinding(ControllerBinding.BACK);
                else if (state.released) applyBinding(null);
            } else if (state.canClick() && state.binding.isBindable) {
                applyBinding(state.binding);
                state.block();
            }
        }
    }
}
