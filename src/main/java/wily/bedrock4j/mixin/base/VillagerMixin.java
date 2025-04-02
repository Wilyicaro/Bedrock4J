package wily.bedrock4j.mixin.base;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.*;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.bedrock4j.inventory.BedrockMerchantOffer;

import java.util.ArrayList;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager {

    public VillagerMixin(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow public abstract VillagerData getVillagerData();

    @Redirect(method = "increaseMerchantCareer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/Villager;updateTrades()V"))
    private void increaseMerchantCareer(Villager instance){
        if (getLevel() < 5){
            updateTrades(getLevel() + 1);
        }
    }
    @Unique
    private int getLevel(){
        return getVillagerData()./*? if <1.21.5 {*/getLevel/*?} else {*//*level*//*?}*/();
    }

    public Villager self(){
        return (Villager) (Object) this;
    }

    @Override
    public MerchantOffers getOffers() {
        if (this.offers == null) {
            this.offers = new MerchantOffers();
            this.updateTrades();
            updateTrades(getLevel() + 1);
        }
        return this.offers;
    }
    @Inject(method = "updateTrades", at = @At("HEAD"), cancellable = true)
    public void updateTrades(CallbackInfo ci) {
        ci.cancel();
        updateTrades(getLevel());
    }

    protected void updateTrades(int level){
        Int2ObjectMap<VillagerTrades.ItemListing[]> int2ObjectMap;
        VillagerData villagerData = this.getVillagerData();
        Int2ObjectMap<VillagerTrades.ItemListing[]> int2ObjectMap2 = /*? if >=1.20.2 {*/this.level().enabledFeatures().contains(FeatureFlags.TRADE_REBALANCE) ? ((int2ObjectMap = VillagerTrades.EXPERIMENTAL_TRADES.get(villagerData./*? if <1.21.5 {*/getProfession/*?} else {*//*profession*//*?}*/())) != null ? int2ObjectMap : VillagerTrades.TRADES.get(villagerData./*? if <1.21.5 {*/getProfession/*?} else {*//*profession*//*?}*/())) : /*?}*/ VillagerTrades.TRADES.get(villagerData./*? if <1.21.5 {*/getProfession/*?} else {*//*profession*//*?}*/());
        VillagerTrades.ItemListing[] itemListings;
        if (int2ObjectMap2 == null || int2ObjectMap2.isEmpty() || (itemListings = int2ObjectMap2.get(level)) == null) return;

        ArrayList<VillagerTrades.ItemListing> arrayList = Lists.newArrayList(itemListings);
        int j = 0;
        while (j < 2 && !arrayList.isEmpty()) {
            MerchantOffer merchantOffer = arrayList.remove(self().getRandom().nextInt(arrayList.size())).getOffer(self(), self().getRandom());
            if (merchantOffer == null) continue;
            ((BedrockMerchantOffer) merchantOffer).setRequiredLevel(level);
            self().getOffers().add(merchantOffer);
            ++j;
        }

    }
}
