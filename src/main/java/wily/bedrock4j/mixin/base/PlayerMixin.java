package wily.bedrock4j.mixin.base;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factoryapi.FactoryAPIClient;
import wily.factoryapi.base.config.FactoryConfig;
import wily.factoryapi.util.FactoryItemUtil;
import wily.bedrock4j.Bedrock4J;
import wily.bedrock4j.Bedrock4JClient;
import wily.bedrock4j.config.LegacyCommonOptions;
import wily.bedrock4j.entity.PlayerYBobbing;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements PlayerYBobbing {
    float oYBob;
    float yBob;

    @Shadow public abstract Abilities getAbilities();

    @Override
    public float oYBob() {
        return oYBob;
    }

    @Override
    public void setOYBob(float bob) {
        oYBob = bob;
    }

    @Override
    public float yBob() {
        return yBob;
    }

    @Override
    public void setYBob(float bob) {
        yBob = bob;
    }

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "getFlyingSpeed", at = @At(value = "RETURN"), cancellable = true)
    protected void getFlyingSpeed(CallbackInfoReturnable<Float> cir) {
        if (level().isClientSide && !FactoryAPIClient.hasModOnServer) return;
        cir.setReturnValue(cir.getReturnValueF() * ( getAbilities().flying ? (isSprinting() ? level().isClientSide ? Math.max(10,Math.min(Bedrock4JClient.getEffectiveRenderDistance(),14)) * 0.6f : 6 : 2) : 1));
    }

    @Inject(method = "aiStep", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Player;bob:F", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    public void aiStep(CallbackInfo ci) {
        handleYBobbing();
    }

    @Inject(method = "resetAttackStrengthTicker", at = @At(value = "HEAD"), cancellable = true)
    protected void resetAttackStrengthTicker(CallbackInfo ci) {
        if (FactoryConfig.hasCommonConfigEnabled(LegacyCommonOptions.legacyCombat)) ci.cancel();
    }

    @Inject(method = "getCurrentItemAttackStrengthDelay", at = @At(value = "HEAD"), cancellable = true)
    protected void getCurrentItemAttackStrengthDelay(CallbackInfoReturnable<Float> cir) {
        if (FactoryConfig.hasCommonConfigEnabled(LegacyCommonOptions.legacyCombat)) cir.setReturnValue(1.0f);
    }

    @ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = /*? if <1.20.5 {*//*"Lnet/minecraft/world/entity/player/Player;getAttributeValue(Lnet/minecraft/world/entity/ai/attributes/Attribute;)D"*//*?} else {*/"Lnet/minecraft/world/entity/player/Player;getAttributeValue(Lnet/minecraft/core/Holder;)D"/*?}*/))
    protected double modifyAttackDamage(double original) {
        return original + Bedrock4J.getItemDamageModifier(getMainHandItem());
    }

    @ModifyVariable(method = "attack", at = @At(value = "STORE"), ordinal = 3)
    protected boolean modifyAttackDamage(boolean original) {
        return (!FactoryConfig.hasCommonConfigEnabled(LegacyCommonOptions.legacyCombat) || FactoryItemUtil.getEnchantmentLevel(getMainHandItem(), Enchantments.SWEEPING_EDGE, level().registryAccess()) > 0) && original;
    }

}
