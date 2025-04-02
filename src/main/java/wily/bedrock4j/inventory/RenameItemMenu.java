package wily.bedrock4j.inventory;

//? if >=1.20.5 {
import net.minecraft.util.StringUtil;
 //?}


public interface RenameItemMenu {

    void setResultItemName(String name);
    String getResultItemName();
    static String validateName(String string) {
        if (string == null || string.isBlank()) return null;
        String string2 = /*? if <1.20.5 {*//*SharedConstants*//*?} else {*/StringUtil/*?}*/.filterText(string);
        if (string2.length() <= 50) {
            return string2;
        }
        return null;
    }
}
