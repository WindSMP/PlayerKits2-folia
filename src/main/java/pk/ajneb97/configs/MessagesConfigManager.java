package pk.ajneb97.configs;

import org.bukkit.configuration.file.FileConfiguration;
import pk.ajneb97.PlayerKits2;
import pk.ajneb97.configs.model.CommonConfig;
import pk.ajneb97.managers.MessagesManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MessagesConfigManager {

    private PlayerKits2 plugin;
    private CommonConfig configFile;

    public MessagesConfigManager(PlayerKits2 plugin){
        this.plugin = plugin;
        this.configFile = new CommonConfig("messages.yml",plugin,null, false);
        this.configFile.registerConfig();
        checkUpdate();
    }

    public void configure(){
        FileConfiguration config = configFile.getConfig();

        //Configure messages
        MessagesManager msgManager = new MessagesManager();
        msgManager.setTimeSeconds(config.getString("seconds"));
        msgManager.setTimeMinutes(config.getString("minutes"));
        msgManager.setTimeHours(config.getString("hours"));
        msgManager.setTimeDays(config.getString("days"));
        msgManager.setPrefix(config.getString("prefix"));
        msgManager.setRequirementsMessageStatusSymbolTrue(config.getString("requirementsMessageStatusSymbolTrue"));
        msgManager.setRequirementsMessageStatusSymbolFalse(config.getString("requirementsMessageStatusSymbolFalse"));
        msgManager.setCooldownPlaceholderReady(config.getString("cooldownPlaceholderReady"));

        this.plugin.setMessagesManager(msgManager);
    }

    public void saveConfig(){
        configFile.saveConfig();
    }

    public boolean reloadConfig(){
        if(!configFile.reloadConfig()){
            return false;
        }
        configure();
        return true;
    }

    public FileConfiguration getConfig(){
        return configFile.getConfig();
    }

    public void checkUpdate(){
        Path pathConfig = Paths.get(configFile.getRoute());
        try{
            String text = new String(Files.readAllBytes(pathConfig));
            if(!text.contains("commandPreviewOtherCorrect:")){
                getConfig().set("onlyPlayerCommand", "&cOnly a player can use this command.");
                getConfig().set("commandPreviewOtherCorrect", "&aPreviewing kit &7%kit% &ato &e%player%&a.");
                saveConfig();
            }
            if(!text.contains("kitResetCorrectAll:")){
                getConfig().set("kitResetCorrectAll", "&aKit &7%kit% &areset for &7all players&a!");
                saveConfig();
            }
            if(!text.contains("commandOpenError:")){
                getConfig().set("commandOpenError", "&cYou need to use: &7/kit open <inventory> <player>");
                getConfig().set("inventoryNotExists", "&cThat inventory doesn't exists.");
                saveConfig();
            }
            if(!text.contains("commandPreviewError:")){
                getConfig().set("commandPreviewError", "&cYou need to use: &7/kit preview <kit>");
                getConfig().set("kitPreviewDisabled", "&cKit preview is disabled.");
                saveConfig();
            }
            if(!text.contains("pluginCriticalErrors:")){
                getConfig().set("pluginCriticalErrors", "&cThe plugin has detected some errors. Check them using &7/kit verify");
                saveConfig();
            }
            if(!text.contains("commandOutputError:")){
                getConfig().set("commandClaimError", "&cYou need to use: &7/kit claim <kit> (optional)<armor/shulker>");
                getConfig().set("commandGiveError", "&cYou need to use: &7/kit give <kit> <player> (optional)<armor/shulker>");
                getConfig().set("commandOutputError", "&cYou need to use: &7/kit output <set/get/unset> ...");
                getConfig().set("commandOutputSetError", "&cYou need to use: &7/kit output set <armor/shulker> (optional)<player>");
                getConfig().set("commandOutputUnsetError", "&cYou need to use: &7/kit output unset (optional)<player>");
                getConfig().set("commandOutputGetError", "&cYou need to use: &7/kit output get (optional)<player>");
                getConfig().set("commandOutputInvalidMode", "&cInvalid mode. Use &7armor &cor &7shulker&c.");
                getConfig().set("commandOutputSetCorrect", "&aDefault output mode set to &7%mode%&a.");
                getConfig().set("commandOutputSetOtherCorrect", "&aSet output mode to &7%mode% &afor &e%player%&a.");
                getConfig().set("commandOutputUnsetCorrect", "&aDefault output mode removed. The selector UI will open again.");
                getConfig().set("commandOutputUnsetOtherCorrect", "&aRemoved output mode for &e%player%&a.");
                getConfig().set("commandOutputCurrent", "&aOutput mode for &7%player%&a: &e%mode%&a.");
                getConfig().set("shulkerNotSupportedError", "&cThis server version doesn't support shulker output.");
                saveConfig();
            }
            if(!text.contains("commandOutputUnsetError:")){
                getConfig().set("commandOutputError", "&cYou need to use: &7/kit output <set/get/unset> ...");
                getConfig().set("commandOutputUnsetError", "&cYou need to use: &7/kit output unset (optional)<player>");
                getConfig().set("commandOutputUnsetCorrect", "&aDefault output mode removed. The selector UI will open again.");
                getConfig().set("commandOutputUnsetOtherCorrect", "&aRemoved output mode for &e%player%&a.");
                saveConfig();
            }

        }catch(IOException e){
            plugin.getLogger().log(java.util.logging.Level.SEVERE, "An error occurred in PlayerKits2", e);
        }
    }
}
