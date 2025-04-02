package wily.bedrock4j.mixin.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.multiplayer.*;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.bedrock4j.Bedrock4JClient;
import wily.bedrock4j.client.BedrockOptions;
import wily.bedrock4j.client.screen.CreativeModeScreen;
import wily.bedrock4j.client.screen.LeaderboardsScreen;
import wily.bedrock4j.client.screen.BedrockLoadingScreen;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin /*? if >1.20.2 {*/extends ClientCommonPacketListenerImpl/*?}*/ {
    //? if <=1.20.2 {
    /*@Shadow @Final
    private Minecraft minecraft;
    @Inject(method = "handleRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V", shift = At.Shift.AFTER))
    public void handleRespawn(ClientboundRespawnPacket clientboundRespawnPacket, CallbackInfo ci) {
        if (clientboundRespawnPacket.shouldKeep(ClientboundRespawnPacket.KEEP_ALL_DATA)) return;
        LegacyLoadingScreen respawningScreen = LegacyLoadingScreen.getRespawningScreen(()-> false);
        minecraft.setScreen(new ReceivingLevelScreen(){
            @Override
            protected void init() {
                super.init();
                respawningScreen.init(minecraft, width, height);
            }

            @Override
            public void render(GuiGraphics guiGraphics, int i, int j, float f) {
                respawningScreen.render(guiGraphics, i, j, f);
            }
        });
    }
    *///?} else {
    @Shadow private LevelLoadStatusManager levelLoadStatusManager;
    protected ClientPacketListenerMixin(Minecraft minecraft, Connection connection, CommonListenerCookie commonListenerCookie) {
        super(minecraft, connection, commonListenerCookie);
    }

    @Inject(method = "handleRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;setId(I)V"))
    public void handleRespawn(ClientboundRespawnPacket clientboundRespawnPacket, CallbackInfo ci) {
        if (!clientboundRespawnPacket.shouldKeep(ClientboundRespawnPacket.KEEP_ALL_DATA)){
            minecraft.setScreen(BedrockLoadingScreen.getRespawningScreen(levelLoadStatusManager::levelReady));
        }
    }
    //?}

    @Redirect(method = "handleRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/MusicManager;stopPlaying()V"))
    public void handleRespawn(MusicManager instance) {
        minecraft.getSoundManager().stop();
    }

    @Inject(method = "handlePlayerInfoUpdate", at = @At("RETURN"))
    public void handlePlayerInfoUpdate(ClientboundPlayerInfoUpdatePacket clientboundPlayerInfoUpdatePacket, CallbackInfo ci) {
        Bedrock4JClient.onClientPlayerInfoChange();
    }

    @Inject(method = "handlePlayerInfoRemove", at = @At("RETURN"))
    public void handlePlayerInfoUpdate(ClientboundPlayerInfoRemovePacket clientboundPlayerInfoRemovePacket, CallbackInfo ci) {
        Bedrock4JClient.onClientPlayerInfoChange();
    }


    @Redirect(method = /*? if <1.21.2 {*/"handleContainerSetSlot"/*?} else {*//*"handleSetCursorItem"*//*?}*/, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;setCarried(Lnet/minecraft/world/item/ItemStack;)V"))
    public void handleContainerSetSlot(AbstractContainerMenu instance, ItemStack itemStack) {
        if (minecraft.screen instanceof CreativeModeScreen) return;
        instance.setCarried(itemStack);
    }

    //? if >=1.21.2 {
    /*@Inject(method = "handleContainerSetSlot", at = @At("RETURN"))
    public void handleContainerSetSlot(ClientboundContainerSetSlotPacket clientboundContainerSetSlotPacket, CallbackInfo ci) {
        if (this.minecraft.screen instanceof CreativeModeScreen) {
            minecraft.player.inventoryMenu.setRemoteSlot(clientboundContainerSetSlotPacket.getSlot(), clientboundContainerSetSlotPacket.getItem());
            minecraft.player.inventoryMenu.broadcastChanges();
        }
    }
    *///?}

    @Redirect(method = "handleContainerSetSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;setItem(IILnet/minecraft/world/item/ItemStack;)V", ordinal = 0))
    public void handleContainerSetSlot(AbstractContainerMenu instance, int i, int j, ItemStack itemStack) {
        if (minecraft.screen instanceof CreativeModeScreen) return;
        instance.setItem(i,j,itemStack);
    }

    @Redirect(method = "handleSetEntityPassengersPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;setOverlayMessage(Lnet/minecraft/network/chat/Component;Z)V"))
    public void handleSetEntityPassengersPacket(Gui instance, Component component, boolean bl) {

    }

    @Inject(method = "handleAwardStats", at = @At("RETURN"))
    public void handleAwardStats(ClientboundAwardStatsPacket clientboundAwardStatsPacket, CallbackInfo ci) {
        if (minecraft.screen instanceof LeaderboardsScreen s) s.onStatsUpdated();
    }

    @Inject(method = "handleSystemChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/util/thread/BlockableEventLoop;)V", shift = At.Shift.AFTER), cancellable = true)
    public void handleSystemChat(ClientboundSystemChatPacket clientboundSystemChatPacket, CallbackInfo ci) {
        if (!BedrockOptions.systemMessagesAsOverlay.get()) {
            minecraft.getChatListener().handleSystemMessage(clientboundSystemChatPacket.content(), false);
            ci.cancel();
        }
    }
}