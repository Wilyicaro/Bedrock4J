package wily.bedrock4j.inventory;

public interface LegacySlot {
    LegacySlotDisplay getDisplay();
    void setDisplay(LegacySlotDisplay slot);
    void setX(int x);
    void setY(int y);
}
