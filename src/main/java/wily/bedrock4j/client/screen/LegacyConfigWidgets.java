package wily.bedrock4j.client.screen;


import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import wily.factoryapi.base.client.FactoryConfigWidgets;
import wily.factoryapi.base.config.FactoryConfig;
import wily.factoryapi.base.config.FactoryConfigControl;
import wily.factoryapi.base.config.FactoryConfigDisplay;

import java.util.function.*;

public class LegacyConfigWidgets {
    public static <T> AbstractWidget createWidget(FactoryConfig<T> config, int x, int y, int width, Consumer<T> afterSet) {
        FactoryConfigDisplay<T> d = config.getDisplay();
        if (d == null) return null;

        Function<T,Tooltip> tooltipFunction = v-> FactoryConfigWidgets.getCachedTooltip(d.tooltip().apply(v));

        AbstractWidget override = FactoryConfigWidgets.getOverride(config, tooltipFunction, x, y, width, afterSet);
        if (override != null) return override;

        if (config.control().equals(FactoryConfigControl.TOGGLE)){
            return new Toggle(x,y,width,config.secureCast(Boolean.class).get(), b-> d.name(), (Function<Boolean, Tooltip>) tooltipFunction, t-> FactoryConfig.saveOptionAndConsume((FactoryConfig<Boolean>)config, t.selected,(Consumer<Boolean>) afterSet));
        } else if (config.control() instanceof FactoryConfigControl.FromInt<T> c){
            return BedrockSliderButton.createFromInt(x,y,width,16, s-> d.captionFunction().apply(d.name(), s.getObjectValue()), s-> tooltipFunction.apply(s.getObjectValue()), config.get(), c.valueGetter(), c.valueSetter(), c.valuesSize(), s-> FactoryConfig.saveOptionAndConsume(config,s.getObjectValue(),afterSet));
        } else if (config.control() instanceof FactoryConfigControl.FromDouble<T> c){
            return new BedrockSliderButton<>(x, y, width,16, s-> d.captionFunction().apply(d.name(),s.getObjectValue()), b->tooltipFunction.apply(b.getObjectValue()), config.get(), s-> c.valueGetter().apply(s.getValue()), c.valueSetter(), s-> FactoryConfig.saveOptionAndConsume(config,s.getObjectValue(),afterSet));
        } else if (config.control() instanceof FactoryConfigControl.Int c) {
            return BedrockSliderButton.createFromIntRange(x, y, width, 16, s-> d.captionFunction().apply(d.name(), (T) s.getObjectValue()), b-> tooltipFunction.apply((T) b.getObjectValue()), (Integer) config.get(), c.min(), c.max(), s-> FactoryConfig.saveOptionAndConsume(config, (T) s.getObjectValue(), afterSet));
        }
        return null;
    }

    public static <T> AbstractWidget createWidget(FactoryConfig<T> config, Consumer<T> afterSet) {
        return createWidget(config, 0, 0, 0, afterSet);
    }
    public static <T> AbstractWidget createWidget(FactoryConfig<T> config) {
        return createWidget(config, v-> {});
    }

}