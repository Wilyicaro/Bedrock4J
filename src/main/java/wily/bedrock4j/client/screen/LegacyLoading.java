package wily.bedrock4j.client.screen;

import net.minecraft.network.chat.Component;
import wily.bedrock4j.client.LegacyTip;
import wily.bedrock4j.client.LegacyTipManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public interface LegacyLoading {
    List<Supplier<LegacyTip>> usingLoadingTips = new ArrayList<>(LegacyTipManager.loadingTips);


    int getProgress();

    void setProgress(int progress);

    Component getLoadingHeader();

    void setLoadingHeader(Component loadingHeader);

    Component getLoadingStage();

    void setLoadingStage(Component loadingStage);

    boolean isGenericLoading();

    void setGenericLoading(boolean genericLoading);
}
