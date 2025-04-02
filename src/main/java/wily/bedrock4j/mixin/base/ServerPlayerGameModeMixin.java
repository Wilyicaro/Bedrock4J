package wily.bedrock4j.mixin.base;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.bedrock4j.entity.LegacyPlayerInfo;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {
    @Shadow @Final protected ServerPlayer player;

    @Inject(method = "setGameModeForPlayer", at = @At("RETURN"))
    protected void setGameModeForPlayer(GameType gameType, GameType gameType2, CallbackInfo ci) {
        LegacyPlayerInfo.updateMayFlySurvival(player, LegacyPlayerInfo.of(player).mayFlySurvival(), false);
    }
}
