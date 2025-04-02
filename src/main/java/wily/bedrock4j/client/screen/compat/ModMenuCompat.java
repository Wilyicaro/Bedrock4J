//? if fabric {
package wily.bedrock4j.client.screen.compat;


import com.terraformersmc.modmenu.config.ModMenuConfig;
import wily.factoryapi.FactoryAPIClient;

public class ModMenuCompat {
    public static void init(){
        FactoryAPIClient.SECURE_EXECUTOR.executeWhen(()->{
            ModMenuConfig.MODIFY_TITLE_SCREEN.setValue(false);
            return false;
        });
    }
}
//?}
