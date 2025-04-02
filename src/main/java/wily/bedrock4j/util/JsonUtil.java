package wily.bedrock4j.util;

import com.google.gson.*;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Registry;
//? if >=1.20.5 {
import net.minecraft.core.component.DataComponentPatch;
//?}
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.ArbitrarySupplier;
import wily.factoryapi.util.DynamicUtil;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

public class JsonUtil {

    public static <T> Predicate<T> registryMatches(Registry<T> registry, JsonObject o) {
        String name = registry.key().location().getPath();
        if (!o.has(name) && !o.has(name + "s")) return t -> false;
        List<T> tip = new ArrayList<>();
        List<T> tipExclusions = new ArrayList<>();
        List<TagKey<T>> tipTags = new ArrayList<>();

        if (o.has(name) && o.get(name) instanceof JsonPrimitive j && j.isString()) {
            String s = j.getAsString();
            if (s.startsWith("#"))
                tipTags.add(TagKey.create(registry.key(), FactoryAPI.createLocation(s.replaceFirst("#", ""))));
            else tip.add(FactoryAPIPlatform.getRegistryValue(FactoryAPI.createLocation(s), registry));
        }
        if (o.has(name + "s") && o.get(name + "s") instanceof JsonArray a) {
            a.forEach(ie -> {
                if (ie instanceof JsonPrimitive p && p.isString()) {
                    String s = p.getAsString();
                    if (s.startsWith("#"))
                        tipTags.add(TagKey.create(registry.key(), FactoryAPI.createLocation(s.replaceFirst("#", ""))));
                    else if (s.startsWith("!")) {
                        ResourceLocation l = FactoryAPI.createLocation(s.replaceFirst("!", ""));
                        registry.getOptional(l).ifPresent(tipExclusions::add);
                    } else tip.add(FactoryAPIPlatform.getRegistryValue(FactoryAPI.createLocation(s), registry));
                }
            });
        }
        return t -> !tipExclusions.contains(t) && (tip.contains(t) || tipTags.stream().anyMatch(registry./*? if <1.21.2 {*/getHolderOrThrow/*?} else {*//*getOrThrow*//*?}*/(registry.getResourceKey(t).orElseThrow())::is));
    }

    public static BiPredicate<Item, /*? if <1.20.5 {*/ /*CompoundTag*//*?} else {*/DataComponentPatch/*?}*/> registryMatchesItem(JsonObject o){
        //? if <1.20.5 {
        /*CompoundTag data = o.has("nbt") ? CompoundTag.CODEC.parse(JsonOps.INSTANCE,o.get("nbt")).result().orElse(null) : null;
        *///?} else {
        DataComponentPatch data = o.has("components") ? DataComponentPatch.CODEC.parse(DynamicUtil.getActualRegistryOps(JsonOps.INSTANCE),o.getAsJsonObject("components")).getOrThrow() : null;
        //?}
        Predicate<Item> p = registryMatches(BuiltInRegistries.ITEM,o);
        return (item, d) -> p.test(item) && (data == null || /*? if <1.20.5 {*//*NbtUtils.compareNbt(data,d,true)*//*?} else {*/Objects.equals(d,data)/*?}*/);
    }

    public static ArbitrarySupplier<ItemStack> getItemFromJson(JsonElement element, boolean allowData){
        return DynamicUtil.getItemFromDynamic(new Dynamic<>(JsonOps.INSTANCE,element),allowData);
    }

    public static <K,V> void addMapListEntry(Map<K,List<V>> map, K key, V entry){
        map.computeIfAbsent(key,k-> new ArrayList<>()).add(entry);
    }

    public static <T> T getJsonStringOrNull(JsonObject object, String element, Function<String,T> constructor){
        String s = GsonHelper.getAsString(object,element, null);
        return s == null ? null : constructor.apply(s);
    }

    public static <T> void ifJsonStringNotNull(JsonObject object, String element, Function<String,T> constructor, Consumer<T> consumer){
        T obj = getJsonStringOrNull(object,element,constructor);
        if  (obj != null) consumer.accept(obj);
    }

    public static Stream<String> getOrderedNamespaces(ResourceManager manager){
        return manager.getNamespaces().stream().sorted(Comparator.comparingInt(s-> s.equals("bedrock4j") ? 0 : 1));
    }

    public static Integer optionalJsonColor(JsonObject object, String element, Integer fallback) {
        return optionalJsonColor(object.get(element),fallback);
    }
    public static Integer optionalJsonColor(JsonElement element, Integer fallback) {
        if (element instanceof JsonPrimitive p){
            if (p.isString() && p.getAsString().startsWith("#")) return (int) Long.parseLong(p.getAsString().substring(1), 16);
            return p.getAsInt();
        }
        return fallback;
    }
}
