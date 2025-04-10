package wily.bedrock4j.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import wily.bedrock4j.Bedrock4JClient;
import wily.bedrock4j.client.CommonColor;
import wily.bedrock4j.init.LegacyRegistries;
import wily.bedrock4j.util.BedrockSprites;
import wily.bedrock4j.util.ScreenUtil;
import wily.factoryapi.base.client.AdvancedTextWidget;
import wily.factoryapi.base.client.FactoryGuiGraphics;
import wily.factoryapi.util.FactoryScreenUtil;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class BedrockSliderButton<T> extends AbstractSliderButton {
    private final Function<BedrockSliderButton<T>,Component> messageGetter;

    private final Function<BedrockSliderButton<T>, T> valueGetter;
    private final Function<T, Double> valueSetter;
    private final Consumer<BedrockSliderButton<T>> onChange;
    private final Function<BedrockSliderButton<T>,Tooltip> tooltipSupplier;
    private int slidingMul = 1;
    private int lastSliderInput = -1;
    protected T objectValue;
    protected final AdvancedTextWidget text = new AdvancedTextWidget(FactoryScreenUtil.getGuiAccessor());


    public BedrockSliderButton(int i, int j, int k, int l, Function<BedrockSliderButton<T>,Component> messageGetter, Function<BedrockSliderButton<T>,Tooltip> tooltipSupplier, T initialValue, Function<BedrockSliderButton<T>,T> valueGetter, Function<T, Double> valueSetter, Consumer<BedrockSliderButton<T>> onChange) {
        super(i, j, k, l, Component.empty(), valueSetter.apply(initialValue));
        this.messageGetter = messageGetter;
        this.valueGetter = valueGetter;
        this.valueSetter = valueSetter;
        this.onChange = onChange;
        this.tooltipSupplier = tooltipSupplier;
        objectValue = initialValue;
        updateMessage();
    }

    public static <T> BedrockSliderButton<T> createFromInt(int i, int j, int k, int l, Function<BedrockSliderButton<T>,Component> messageGetter, Function<BedrockSliderButton<T>,Tooltip> tooltipSupplier, T initialValue, Function<Integer,T> valueGetter, Function<T, Integer> valueSetter, Supplier<Integer> valuesSize, Consumer<BedrockSliderButton<T>> onChange) {
        return new BedrockSliderButton<>(i, j, k, l, messageGetter, tooltipSupplier, initialValue, b-> valueGetter.apply((int) Math.round(b.value * (valuesSize.get() - 1))), t->Math.max(0d,valueSetter.apply(t))/ (valuesSize.get() - 1),onChange);
    }

    public BedrockSliderButton(int i, int j, int k, int l, Function<BedrockSliderButton<T>,Component> messageGetter, Function<BedrockSliderButton<T>,Tooltip> tooltipSupplier, T initialValue, Supplier<List<T>> values, Consumer<BedrockSliderButton<T>> onChange) {
        this(i, j, k, l, messageGetter, tooltipSupplier, initialValue, b-> values.get().get((int) Math.round(b.value * (values.get().size() - 1))),t->Math.max(0d,values.get().indexOf(t))/ (values.get().size() - 1),onChange);
    }

    public static BedrockSliderButton<Integer> createFromIntRange(int i, int j, int k, int l, Function<BedrockSliderButton<Integer>,Component> messageGetter, Function<BedrockSliderButton<Integer>,Tooltip> tooltipSupplier, Integer initialValue, int min, IntSupplier max, Consumer<BedrockSliderButton<Integer>> onChange) {
        return new BedrockSliderButton<>(i, j, k, l, messageGetter, tooltipSupplier, initialValue, b-> min + (int) Math.round(b.value * (max.getAsInt() - min)), t->Math.max(0d,Math.min((double)(t-min) / (max.getAsInt()-min),1d)),onChange);
    }

    public static BedrockSliderButton<Integer> createFromIntRange(int i, int j, int k, int l, Function<BedrockSliderButton<Integer>,Component> messageGetter, Function<BedrockSliderButton<Integer>,Tooltip> tooltipSupplier, Integer initialValue, int min, int max, Consumer<BedrockSliderButton<Integer>> onChange) {
        return createFromIntRange(i,j,k,l,messageGetter,tooltipSupplier,initialValue,min,()->max,onChange);
    }

    public Component getDefaultMessage(Component caption, Component visibleValue){
        return CommonComponents.optionNameValue(caption,visibleValue);
    }

    public T getObjectValue(){
        return objectValue == null ? valueGetter.apply(this) : objectValue;
    }

    public double getValue(){
        return value;
    }

    @Override
    protected void updateMessage() {
        setMessage(messageGetter.apply(this));
        setTooltip(tooltipSupplier.apply(this));
        text.withLines(getMessage(), getWidth()).withShadow(false).withColor(CommonColor.WHITE.get());
        setHeight(16 + text.getHeight());
    }

    @Override
    public void setWidth(int i) {
        super.setWidth(i);
        updateMessage();
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        text.withPos(getX(), getY()).render(guiGraphics, i, j, f);
        FactoryGuiGraphics.of(guiGraphics).blitSprite(isHoveredOrFocused() ? BedrockSprites.SLIDER_BACKGROUND_HIGHLIGHTED : BedrockSprites.SLIDER_BACKGROUND, getX() + 5, getY() + getHeight() - 13, getWidth() - 10, 10);
        FactoryGuiGraphics.of(guiGraphics).blitSprite(isHoveredOrFocused() ? BedrockSprites.SLIDER_PROGRESS_HIGHLIGHTED : BedrockSprites.SLIDER_PROGRESS, this.getX() + 5, getY() + getHeight() - 13, (int)(this.value * (double)(getWidth() - 10)), 10);
        FactoryGuiGraphics.of(guiGraphics).blitSprite(isHoveredOrFocused() ? BedrockSprites.BUTTON_HIGHLIGHTED: BedrockSprites.BUTTON, this.getX() + 5 + (int)(this.value * (double)(getWidth() - 20)), this.getY() + getHeight() - 16, 10, 16);
    }

    public void setFocused(boolean bl) {
        super.setFocused(bl);
        if (bl) canChangeValue = Bedrock4JClient.controllerManager.canChangeSlidersValue;
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (!active) return false;
        if (CommonInputs.selected(i)) {
            Bedrock4JClient.controllerManager.canChangeSlidersValue = this.canChangeValue = !this.canChangeValue;
            return true;
        }
        if (this.canChangeValue) {
            boolean bl = i == 263;
            if ((bl && value > 0) || (i == 262 && value < 1.0)) {
                if (slidingMul > 0 && i != lastSliderInput) slidingMul = 1;
                lastSliderInput = i;
                double part = 1d / (width - 8) * slidingMul;
                T v = getObjectValue();
                while (v.equals(getObjectValue())) {
                    setValue(this.value + (bl ? -part : part));
                    if (part >= 1) break;
                    part= Math.min(part * 2,1);
                }
                slidingMul++;
                return true;
            }
        }
        return false;
    }

    public boolean keyReleased(int i, int j, int k) {
        if (this.canChangeValue && (i == 263 || i== 262)) slidingMul = 1;
        return false;
    }

    public void setObjectValue(T objectValue){
        this.objectValue = objectValue;
        value = valueSetter.apply(objectValue);
    }

    @Override
    protected void applyValue() {
        T oldValue = objectValue;
        setObjectValue(valueGetter.apply(this));
        if (!oldValue.equals(objectValue)){
            ScreenUtil.playSimpleUISound(LegacyRegistries.SCROLL.get(),1.0f);
            onChange.accept(this);
        }
    }

}
