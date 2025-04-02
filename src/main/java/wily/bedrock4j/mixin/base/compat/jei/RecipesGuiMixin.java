//? if <=1.21.1 {
package wily.bedrock4j.mixin.base.compat.jei;

import mezz.jei.gui.recipes.RecipesGui;
import org.spongepowered.asm.mixin.Mixin;
import wily.bedrock4j.client.controller.Controller;
import wily.bedrock4j.client.screen.ControlTooltip;

@Mixin(RecipesGui.class)
public class RecipesGuiMixin implements Controller.Event, ControlTooltip.Event {
    @Override
    public boolean disableCursorOnInit() {
        return false;
    }
}
//?}
