package wily.bedrock4j.client.controller;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import wily.bedrock4j.Bedrock4JClient;
import wily.bedrock4j.client.screen.ControlTooltip;

public interface LegacyKeyMapping {
    static LegacyKeyMapping of(KeyMapping key){
        return (LegacyKeyMapping) key;
    }

    default KeyMapping self(){
        return (KeyMapping) this;
    }

    <T extends BindingState> ControllerBinding<T> getDefaultBinding();

    <T extends BindingState> ControllerBinding<T> getBinding();

    Component getDisplayName();

    static Component getDefaultDisplayName(KeyMapping keyMapping) {
        String name = keyMapping.getName();
        Minecraft minecraft = Minecraft.getInstance();
        boolean hasPlayer = minecraft.player != null && minecraft.gameMode != null;
        switch (name){
            case "bedrock4j.key.crafting" -> name = hasPlayer && Bedrock4JClient.playerHasInfiniteMaterials() ? "selectWorld.gameMode.creative" : name;
            case "bedrock4j.key.inventory" -> name = "key.inventory";
        }
        return ControlTooltip.getAction(name);
    }

    InputConstants.Key getKey();

    <T extends BindingState> void setBinding(ControllerBinding<T> binding);

    <T extends BindingState> void setDefaultBinding(ControllerBinding<T> binding);
}
