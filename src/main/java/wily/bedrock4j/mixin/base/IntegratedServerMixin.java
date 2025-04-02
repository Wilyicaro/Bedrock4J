package wily.bedrock4j.mixin.base;

import com.mojang.datafixers.DataFixer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.FactoryAPIClient;
import wily.bedrock4j.Bedrock4JClient;
import wily.bedrock4j.client.BedrockOptions;
import wily.bedrock4j.client.LegacyClientWorldSettings;

import java.net.Proxy;
import java.util.function.BooleanSupplier;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin extends MinecraftServer {
    @Shadow @Final private Minecraft minecraft;

    @Shadow @Final private static Logger LOGGER;

    @Shadow private boolean paused;

    public IntegratedServerMixin(Thread thread, LevelStorageSource.LevelStorageAccess levelStorageAccess, PackRepository packRepository, WorldStem worldStem, Proxy proxy, DataFixer dataFixer, Services services, ChunkProgressListenerFactory chunkProgressListenerFactory) {
        super(thread, levelStorageAccess, packRepository, worldStem, proxy, dataFixer, services, chunkProgressListenerFactory);
    }

    public IntegratedServer self(){
        return (IntegratedServer) (Object) this;
    }
    @Inject(method = "tickServer", at = @At("HEAD"))
    public void tickServer(BooleanSupplier booleanSupplier, CallbackInfo ci) {
        if (Bedrock4JClient.manualSave){
            Bedrock4JClient.manualSave = false;
            FactoryAPIClient.getProfiler().push("manualSave");
            LOGGER.info("Saving manually...");
            this.saveEverything(false, true, true);
            FactoryAPIClient.getProfiler().pop();
        }
    }

    @Redirect(method = "tickServer", at = @At(value = "FIELD", target = "Lnet/minecraft/client/server/IntegratedServer;paused:Z", opcode = Opcodes.GETFIELD, ordinal = 1))
    public boolean tickServer(IntegratedServer instance) {
        return paused && BedrockOptions.autoSaveWhenPaused.get() && (BedrockOptions.autoSaveInterval.get() > 0 || !Bedrock4JClient.isCurrentWorldSource(storageSource)) && !minecraft.isDemo();
    }

    @Override
    public boolean isUnderSpawnProtection(ServerLevel serverLevel, BlockPos blockPos, Player player) {
        if (!isSingleplayerOwner(player.getGameProfile()) && !LegacyClientWorldSettings.of(worldData).trustPlayers()) return true;
        return super.isUnderSpawnProtection(serverLevel, blockPos, player);
    }
}
