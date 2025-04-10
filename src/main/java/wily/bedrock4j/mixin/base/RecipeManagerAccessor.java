//? if <1.21.2 {
package wily.bedrock4j.mixin.base;

import net.minecraft.world.item.crafting.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Collection;

@Mixin(RecipeManager.class)
public interface RecipeManagerAccessor {
    @Invoker("byType")
    <R extends Recipe<?>> /*? if <1.20.5 {*/ /*Map<ResourceLocation, /^? if >1.20.1 {^/RecipeHolder<R>/^?} else {^//^R^//^?}^/>*//*?} else {*/Collection<RecipeHolder<R>>/*?}*/ getRecipeByType(RecipeType<R> recipeType);
}
//?}