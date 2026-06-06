package pk.ajneb97;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pk.ajneb97.configs.MainConfigManager;
import pk.ajneb97.managers.MessagesManager;
import pk.ajneb97.managers.PlayerDataManager;
import pk.ajneb97.model.Kit;
import pk.ajneb97.model.internal.GiveKitInstructions;
import pk.ajneb97.model.internal.KitOutputMode;
import pk.ajneb97.model.internal.PlayerKitsMessageResult;
import pk.ajneb97.model.inventory.InventoryPlayer;
import pk.ajneb97.model.inventory.KitInventory;
import pk.ajneb97.utils.PlayerUtils;

import java.util.ArrayList;
import java.util.List;

public class MainCommand implements CommandExecutor, TabCompleter {

    private PlayerKits2 plugin;
    public MainCommand(PlayerKits2 plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        MessagesManager msgManager = plugin.getMessagesManager();
        FileConfiguration messagesConfig = plugin.getConfigsManager().getMessagesConfigManager().getConfig();

        if (!(sender instanceof Player)) {
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    reload(sender,args,messagesConfig,msgManager);
                }else if(args[0].equalsIgnoreCase("give")) {
                    give(sender,args,messagesConfig,msgManager);
                }else if(args[0].equalsIgnoreCase("delete")) {
                    delete(sender,args,messagesConfig,msgManager);
                }else if(args[0].equalsIgnoreCase("reset")) {
                    reset(sender,args,messagesConfig,msgManager);
                }else if(args[0].equalsIgnoreCase("migrate")) {
                    migrate(sender,args,messagesConfig,msgManager);
                }else if(args[0].equalsIgnoreCase("open")){
                    open(sender,args,messagesConfig,msgManager);
                }else if(args[0].equalsIgnoreCase("preview")){
                    preview(sender,args,messagesConfig,msgManager);
                }else if(args[0].equalsIgnoreCase("output")){
                    output(sender,args,messagesConfig,msgManager);
                }else{
                    help(sender,msgManager,messagesConfig);
                }
            }
            return true;
        }

        Player player = (Player) sender;

        boolean claimKitShortCommand = plugin.getConfigsManager().getMainConfigManager().getConfig().getBoolean("claim_kit_short_command");

        if(args.length >= 1){
            if(args[0].equalsIgnoreCase("claim") && !claimKitShortCommand){
                claim(player,args,messagesConfig,msgManager);
            }else if(args[0].equalsIgnoreCase("preview")){
                preview(player,args,messagesConfig,msgManager);
            }else if(args[0].equalsIgnoreCase("create")){
                create(player,args,messagesConfig,msgManager);
            }else if(args[0].equalsIgnoreCase("give")) {
                give(player,args,messagesConfig,msgManager);
            }else if(args[0].equalsIgnoreCase("delete")) {
                delete(player,args,messagesConfig,msgManager);
            }else if(args[0].equalsIgnoreCase("reload")) {
                reload(player,args,messagesConfig,msgManager);
            }else if(args[0].equalsIgnoreCase("reset")) {
                reset(sender,args,messagesConfig,msgManager);
            }else if(args[0].equalsIgnoreCase("edit")) {
                edit(player,args,messagesConfig,msgManager);
            }else if(args[0].equalsIgnoreCase("verify")){
                verify(player,messagesConfig,msgManager);
            }else if(args[0].equalsIgnoreCase("migrate")) {
                migrate(sender,args,messagesConfig,msgManager);
            }else if(args[0].equalsIgnoreCase("open")){
                open(sender,args,messagesConfig,msgManager);
            }else if(args[0].equalsIgnoreCase("output")){
                output(sender,args,messagesConfig,msgManager);
            }
            else{
                // /kit <kit> (short command)
                if(claimKitShortCommand){
                    KitOutputMode mode = args.length >= 2 ? getOutputMode(args[1]) : null;
                    if(args.length >= 2 && mode == null){
                        msgManager.sendMessage(player,messagesConfig.getString("commandOutputInvalidMode"),true);
                        return true;
                    }
                    claimKitShortCommand(player,messagesConfig,msgManager,args[0],mode);
                    return true;
                }else{
                    help(sender,msgManager,messagesConfig);
                }
            }
        }else{
            // /kit
            if(plugin.getVerifyManager().isCriticalErrors()){
                msgManager.sendMessage(player,messagesConfig.getString("pluginCriticalErrors"),true);
                return true;
            }
            plugin.getInventoryManager().openInventory(new InventoryPlayer(player,"main_inventory"));
        }


        return true;
    }

    public void help(CommandSender sender,MessagesManager msgManager,FileConfiguration messagesConfig){
        if(!PlayerUtils.isPlayerKitsAdmin(sender)){
            msgManager.sendMessage(sender,messagesConfig.getString("commandDoesNotExists"),true);
            return;
        }
        sender.sendMessage(MessagesManager.getLegacyColoredMessage("&7[ [ &8[&bPlayerKits&a²&8] &7] ]"));
        sender.sendMessage(MessagesManager.getLegacyColoredMessage(" "));
        sender.sendMessage(MessagesManager.getLegacyColoredMessage("&6/kit &8Opens the GUI."));
        sender.sendMessage(MessagesManager.getLegacyColoredMessage("&6/kit claim <kit> (optional)<armor/shulker> &8Claims a kit outside the GUI."));
        sender.sendMessage(MessagesManager.getLegacyColoredMessage("&6/kit output set <armor/shulker> (optional)<player> &8Sets output mode."));
        sender.sendMessage(MessagesManager.getLegacyColoredMessage("&6/kit output unset (optional)<player> &8Removes output mode and brings back the UI."));
        sender.sendMessage(MessagesManager.getLegacyColoredMessage("&6/kit output get (optional)<player> &8Gets output mode."));
        sender.sendMessage(MessagesManager.getLegacyColoredMessage("&6/kit create <kit> (optional)original &8Creates a new kit using the items in your inventory."));
        sender.sendMessage(MessagesManager.getLegacyColoredMessage("&6/kit edit <kit> &8Edits a kit."));
        sender.sendMessage(MessagesManager.getLegacyColoredMessage("&6/kit give <kit> <player> (optional)<armor/shulker> &8Gives a kit to a player."));
        sender.sendMessage(MessagesManager.getLegacyColoredMessage("&6/kit delete <kit> &8Deletes a kit."));
        sender.sendMessage(MessagesManager.getLegacyColoredMessage("&6/kit reset <kit> <player>/* &8Resets kit data for a player."));
        sender.sendMessage(MessagesManager.getLegacyColoredMessage("&6/kit preview <kit> (optional)<player> &8Previews a kit."));
        sender.sendMessage(MessagesManager.getLegacyColoredMessage("&6/kit open <inventory> <player> &8Opens a specific inventory for a player."));
        sender.sendMessage(MessagesManager.getLegacyColoredMessage("&6/kit reload &8Reloads the config."));
        sender.sendMessage(MessagesManager.getLegacyColoredMessage("&6/kit verify &8Checks the plugin for errors."));
        sender.sendMessage(MessagesManager.getLegacyColoredMessage(" "));
        sender.sendMessage(MessagesManager.getLegacyColoredMessage("&7[ [ &8[&bPlayerKits&a²&8] &7] ]"));
    }

    public void migrate(CommandSender sender,String[] args,FileConfiguration messagesConfig,MessagesManager msgManager){
        if(!PlayerUtils.isPlayerKitsAdmin(sender)){
            msgManager.sendMessage(sender,messagesConfig.getString("noPermissions"),true);
            return;
        }

        plugin.getMigrationManager().migrate(sender);
    }

    public void verify(Player player,FileConfiguration messagesConfig,MessagesManager msgManager){
        if(!PlayerUtils.isPlayerKitsAdmin(player)){
            msgManager.sendMessage(player,messagesConfig.getString("noPermissions"),true);
            return;
        }
        plugin.getVerifyManager().sendVerification(player);
    }

    public void reload(CommandSender sender,String[] args,FileConfiguration messagesConfig,MessagesManager msgManager){
        if(!PlayerUtils.isPlayerKitsAdmin(sender)){
            msgManager.sendMessage(sender,messagesConfig.getString("noPermissions"),true);
            return;
        }

        if(!plugin.getConfigsManager().reload()){
            sender.sendMessage(PlayerKits2.prefix+MessagesManager.getLegacyColoredMessage(" &cThere was an error reloading the config, check the console."));
            return;
        }
        msgManager.sendMessage(sender,messagesConfig.getString("commandReload"),true);
    }

    public void reset(CommandSender sender,String[] args,FileConfiguration messagesConfig,MessagesManager msgManager) {
        // /kits reset <kit> <player>
        if(!PlayerUtils.isPlayerKitsAdmin(sender)) {
            msgManager.sendMessage(sender, messagesConfig.getString("noPermissions"), true);
            return;
        }

        if(args.length < 3) {
            msgManager.sendMessage(sender, messagesConfig.getString("commandResetError"), true);
            return;
        }

        String kitName = args[1];
        String playerName = args[2];

        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        if(playerName.equals("*")){
            playerDataManager.resetKitForAllPlayers(kitName,result -> {
                String msg = messagesConfig.getString("kitResetCorrectAll");
                if (msg != null) msgManager.sendMessage(sender, msg.replace("%kit%",kitName), true);
            });
        }else{
            PlayerKitsMessageResult result = playerDataManager.resetKitForPlayer(playerName,kitName);
            if(result.isError()){
                msgManager.sendMessage(sender, result.getMessage(), true);
            }else{
                String msg = messagesConfig.getString("kitResetCorrect");
                if (msg != null) msgManager.sendMessage(sender, msg.replace("%kit%",kitName).replace("%player%",playerName), true);
            }
        }
    }

    public void open(CommandSender sender,String[] args,FileConfiguration messagesConfig,MessagesManager msgManager) {
        // /kits open <inventory> <player>
        if(!PlayerUtils.isPlayerKitsAdmin(sender)) {
            msgManager.sendMessage(sender, messagesConfig.getString("noPermissions"), true);
            return;
        }

        if(args.length < 3) {
            msgManager.sendMessage(sender, messagesConfig.getString("commandOpenError"), true);
            return;
        }

        String inventoryName = args[1];
        String playerName = args[2];

        if(plugin.getInventoryManager().getInventory(inventoryName) == null){
            msgManager.sendMessage(sender, messagesConfig.getString("inventoryNotExists"), true);
            return;
        }

        Player player = Bukkit.getPlayer(playerName);
        if(player == null){
            String msg = messagesConfig.getString("playerNotOnline");
            if (msg != null) msgManager.sendMessage(sender,msg.replace("%player%",playerName),true);
            return;
        }

        InventoryPlayer inventoryPlayer = new InventoryPlayer(player,inventoryName);
        plugin.getInventoryManager().openInventory(inventoryPlayer);
    }

    public void output(CommandSender sender,String[] args,FileConfiguration messagesConfig,MessagesManager msgManager){
        // /kit output <set/get/unset> ...
        if(args.length < 2){
            msgManager.sendMessage(sender,messagesConfig.getString("commandOutputError"),true);
            return;
        }

        String outputAction = args[1];
        if(outputAction.equalsIgnoreCase("set")){
            if(args.length < 3){
                msgManager.sendMessage(sender,messagesConfig.getString("commandOutputSetError"),true);
                return;
            }

            KitOutputMode outputMode = getOutputMode(args[2]);
            if(outputMode == null){
                msgManager.sendMessage(sender,messagesConfig.getString("commandOutputInvalidMode"),true);
                return;
            }

            Player target;
            if(args.length >= 4){
                if(!PlayerUtils.isPlayerKitsAdmin(sender)){
                    msgManager.sendMessage(sender,messagesConfig.getString("noPermissions"),true);
                    return;
                }
                target = Bukkit.getPlayer(args[3]);
                if(target == null){
                    String msg = messagesConfig.getString("playerNotOnline");
                    if (msg != null) msgManager.sendMessage(sender,msg.replace("%player%",args[3]),true);
                    return;
                }
            }else{
                if(!(sender instanceof Player)){
                    msgManager.sendMessage(sender,messagesConfig.getString("commandOutputSetError"),true);
                    return;
                }
                target = (Player) sender;
            }

            plugin.getPlayerDataManager().setDefaultKitOutputMode(target,outputMode);
            if(target.equals(sender)){
                String msg = messagesConfig.getString("commandOutputSetCorrect");
                if(msg != null) msgManager.sendMessage(sender,msg.replace("%mode%",outputMode.toCommandValue()),true);
            }else{
                String msg = messagesConfig.getString("commandOutputSetOtherCorrect");
                if(msg != null) msgManager.sendMessage(sender,msg.replace("%mode%",outputMode.toCommandValue())
                        .replace("%player%",target.getName()),true);
            }
            return;
        }

        if(outputAction.equalsIgnoreCase("unset")){
            Player target;
            if(args.length >= 3){
                if(!PlayerUtils.isPlayerKitsAdmin(sender)){
                    msgManager.sendMessage(sender,messagesConfig.getString("noPermissions"),true);
                    return;
                }
                target = Bukkit.getPlayer(args[2]);
                if(target == null){
                    String msg = messagesConfig.getString("playerNotOnline");
                    if (msg != null) msgManager.sendMessage(sender,msg.replace("%player%",args[2]),true);
                    return;
                }
            }else{
                if(!(sender instanceof Player)){
                    msgManager.sendMessage(sender,messagesConfig.getString("commandOutputUnsetError"),true);
                    return;
                }
                target = (Player) sender;
            }

            plugin.getPlayerDataManager().setDefaultKitOutputMode(target,null);
            if(target.equals(sender)){
                String msg = messagesConfig.getString("commandOutputUnsetCorrect");
                if(msg != null) msgManager.sendMessage(sender,msg,true);
            }else{
                String msg = messagesConfig.getString("commandOutputUnsetOtherCorrect");
                if(msg != null) msgManager.sendMessage(sender,msg.replace("%player%",target.getName()),true);
            }
            return;
        }

        if(outputAction.equalsIgnoreCase("get")){
            Player target;
            if(args.length >= 3){
                if(!PlayerUtils.isPlayerKitsAdmin(sender)){
                    msgManager.sendMessage(sender,messagesConfig.getString("noPermissions"),true);
                    return;
                }
                target = Bukkit.getPlayer(args[2]);
                if(target == null){
                    String msg = messagesConfig.getString("playerNotOnline");
                    if (msg != null) msgManager.sendMessage(sender,msg.replace("%player%",args[2]),true);
                    return;
                }
            }else{
                if(!(sender instanceof Player)){
                    msgManager.sendMessage(sender,messagesConfig.getString("commandOutputGetError"),true);
                    return;
                }
                target = (Player) sender;
            }

            KitOutputMode outputMode = plugin.getPlayerDataManager().getDefaultKitOutputMode(target);
            String modeValue = outputMode == null ? "not_set" : outputMode.toCommandValue();
            String msg = messagesConfig.getString("commandOutputCurrent");
            if(msg != null) msgManager.sendMessage(sender,msg.replace("%mode%",modeValue)
                    .replace("%player%",target.getName()),true);
            return;
        }

        msgManager.sendMessage(sender,messagesConfig.getString("commandOutputError"),true);
    }

    public void give(CommandSender sender,String[] args,FileConfiguration messagesConfig,MessagesManager msgManager){
        // /kits give <kit> <player>
        if(!PlayerUtils.isPlayerKitsAdmin(sender)){
            msgManager.sendMessage(sender,messagesConfig.getString("noPermissions"),true);
            return;
        }

        if(args.length < 3){
            msgManager.sendMessage(sender,messagesConfig.getString("commandGiveError"),true);
            return;
        }

        String kitName = args[1];
        Player player = Bukkit.getPlayer(args[2]);
        if(player == null){
            msgManager.sendMessage(sender,messagesConfig.getString("playerNotOnline")
                    .replace("%player%",args[2]),true);
            return;
        }

        KitOutputMode outputMode = KitOutputMode.ARMOR;
        if(args.length >= 4){
            outputMode = getOutputMode(args[3]);
            if(outputMode == null){
                msgManager.sendMessage(sender,messagesConfig.getString("commandOutputInvalidMode"),true);
                return;
            }
        }

        PlayerKitsMessageResult result = plugin.getKitsManager().giveKit(player,kitName,
                new GiveKitInstructions(true,false,false,false,outputMode));
        if(result.isError()){
            String msg = messagesConfig.getString("commandGiveError2");
            if (msg != null) msgManager.sendMessage(sender,msg.replace("%error%",result.getMessage()),true);
        }else{
            String msg = messagesConfig.getString("commandGiveCorrect");
            if (msg != null) msgManager.sendMessage(sender,msg.replace("%kit%",kitName).replace("%player%",args[2]),true);
        }
    }

    public void claim(Player player,String[] args,FileConfiguration messagesConfig,MessagesManager msgManager){
        // /kit claim <kit>
        if(args.length < 2){
            msgManager.sendMessage(player,messagesConfig.getString("commandClaimError"),true);
            return;
        }

        KitOutputMode outputMode = null;
        if(args.length >= 3){
            outputMode = getOutputMode(args[2]);
            if(outputMode == null){
                msgManager.sendMessage(player,messagesConfig.getString("commandOutputInvalidMode"),true);
                return;
            }
        }

        String kitName = args[1];
        claimKitShortCommand(player,messagesConfig,msgManager,kitName,outputMode);
    }

    public void preview(CommandSender sender,String[] args,FileConfiguration messagesConfig,MessagesManager msgManager){
        // /kit preview <kit> (optional)<player>
        MainConfigManager mainConfigManager = plugin.getConfigsManager().getMainConfigManager();
        if(!mainConfigManager.isKitPreview()){
            msgManager.sendMessage(sender,messagesConfig.getString("kitPreviewDisabled"),true);
            return;
        }

        if(args.length < 2){
            msgManager.sendMessage(sender,messagesConfig.getString("commandPreviewError"),true);
            return;
        }

        Kit kit = plugin.getKitsManager().getKitByName(args[1]);
        if(kit == null){
            String msg = messagesConfig.getString("kitDoesNotExists");
            if (msg != null) msgManager.sendMessage(sender,msg.replace("%kit%",args[1]),true);
            return;
        }

        Player player;
        if(args.length > 2){
            // Kit preview for someone else
            if(!PlayerUtils.isPlayerKitsAdmin(sender)){
                msgManager.sendMessage(sender,messagesConfig.getString("noPermissions"),true);
                return;
            }

            player = Bukkit.getPlayer(args[2]);
            if(player == null){
                String msg = messagesConfig.getString("playerNotOnline");
                if (msg != null) msgManager.sendMessage(sender,msg.replace("%player%",args[2]),true);
                return;
            }

            String msg2 = messagesConfig.getString("commandPreviewOtherCorrect");
            if (msg2 != null) msgManager.sendMessage(sender,msg2.replace("%kit%",args[1]).replace("%player%",args[2]),true);
        }else{
            if(kit.isPermissionRequired()){
                if(mainConfigManager.isKitPreviewRequiresKitPermission() && !kit.playerHasPermission(sender)){
                    msgManager.sendMessage(sender,messagesConfig.getString("cantPreviewError"),true);
                    return;
                }
            }

            if(!(sender instanceof Player)){
                msgManager.sendMessage(sender,messagesConfig.getString("onlyPlayerCommand"),true);
                return;
            }

            player = (Player)sender;
        }

        InventoryPlayer inventoryPlayer = new InventoryPlayer(player,"preview_inventory");
        inventoryPlayer.setKitName(args[1]);
        inventoryPlayer.setPreviousInventoryName("main_inventory");
        plugin.getInventoryManager().openInventory(inventoryPlayer);
    }

    public void claimKitShortCommand(Player player,FileConfiguration messagesConfig,MessagesManager msgManager,String kitName,KitOutputMode outputMode){
        // /kit <kit>
        KitOutputMode finalOutputMode = outputMode;
        if(finalOutputMode == null){
            finalOutputMode = plugin.getPlayerDataManager().getDefaultKitOutputMode(player);
            if(finalOutputMode == null){
                PlayerKitsMessageResult validationResult = plugin.getKitsManager().validateKitClaim(player, kitName, false);
                if(validationResult.isError()){
                    msgManager.sendMessage(player,validationResult.getMessage(),true);
                    return;
                }
                openOutputSelectionInventory(player,kitName);
                return;
            }
        }

        PlayerKitsMessageResult result = plugin.getKitsManager().giveKit(player,kitName,
                new GiveKitInstructions(false,false,false,false,finalOutputMode));
        if(result.isError()){
            msgManager.sendMessage(player,result.getMessage(),true);
        }else{
            if(result.isProceedToBuy()){
                //Open requirements inventory
                InventoryPlayer inventoryPlayer = new InventoryPlayer(player,"buy_requirements_inventory");
                inventoryPlayer.setKitName(kitName);
                inventoryPlayer.setPreviousInventoryName("main_inventory");
                inventoryPlayer.setSelectedOutputMode(finalOutputMode);
                plugin.getInventoryManager().openInventory(inventoryPlayer);
                return;
            }
            String msg = messagesConfig.getString("kitReceived");
            if (msg != null) {
                msgManager.sendMessage(player,msg.replace("%kit%",kitName),true);
            }
        }
    }

    private void openOutputSelectionInventory(Player player, String kitName){
        InventoryPlayer inventoryPlayer = new InventoryPlayer(player,"output_select_inventory");
        inventoryPlayer.setKitName(kitName);
        inventoryPlayer.setPreviousInventoryName("main_inventory");
        plugin.getInventoryManager().openInventory(inventoryPlayer);
    }

    private KitOutputMode getOutputMode(String outputMode){
        return KitOutputMode.fromString(outputMode);
    }

    public void create(Player player,String[] args,FileConfiguration messagesConfig,MessagesManager msgManager){
        // /kit create <kit> (optional)<original/configurable>
        if(!PlayerUtils.isPlayerKitsAdmin(player)){
            msgManager.sendMessage(player,messagesConfig.getString("noPermissions"),true);
            return;
        }

        if(args.length < 2){
            msgManager.sendMessage(player,messagesConfig.getString("commandCreateError"),true);
            return;
        }

        boolean saveOriginalItems = plugin.getConfigsManager().getMainConfigManager().isNewKitDefaultSaveModeOriginal();
        if(args.length >= 3){
            if(args[2].equalsIgnoreCase("original")){
                saveOriginalItems = true;
            }else if(args[2].equalsIgnoreCase("configurable")){
                saveOriginalItems = false;
            }else{
                msgManager.sendMessage(player,messagesConfig.getString("commandCreateError"),true);
                return;
            }
        }

        plugin.getKitsManager().createKit(args[1],player,saveOriginalItems);
    }

    public void delete(CommandSender sender,String[] args,FileConfiguration messagesConfig,MessagesManager msgManager){
        // /kit delete <kit>
        if(!PlayerUtils.isPlayerKitsAdmin(sender)){
            msgManager.sendMessage(sender,messagesConfig.getString("noPermissions"),true);
            return;
        }

        if(args.length < 2){
            msgManager.sendMessage(sender,messagesConfig.getString("commandDeleteError"),true);
            return;
        }

        plugin.getKitsManager().deleteKit(args[1],sender);
    }

    public void edit(Player player,String[] args,FileConfiguration messagesConfig,MessagesManager msgManager){
        // /kit edit <kit>
        if(!PlayerUtils.isPlayerKitsAdmin(player)){
            msgManager.sendMessage(player,messagesConfig.getString("noPermissions"),true);
            return;
        }

        if(args.length < 2){
            msgManager.sendMessage(player,messagesConfig.getString("commandEditError"),true);
            return;
        }

        if(plugin.getKitsManager().getKitByName(args[1]) == null){
            String msg = messagesConfig.getString("kitDoesNotExists");
            if (msg != null) msgManager.sendMessage(player,msg.replace("%kit%",args[1]),true);
            return;
        }

        InventoryPlayer inventoryPlayer = new InventoryPlayer(player,null);
        inventoryPlayer.setKitName(args[1]);
        plugin.getInventoryEditManager().openInventory(inventoryPlayer);
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        MainConfigManager mainConfigManager = plugin.getConfigsManager().getMainConfigManager();
        boolean claimKitShortCommand = mainConfigManager.isClaimKitShortCommand();
        boolean kitPreviewEnabled = mainConfigManager.isKitPreview();
        boolean isAdmin = PlayerUtils.isPlayerKitsAdmin(sender);

        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        if(args.length == 1) {
            if(claimKitShortCommand){
                List<String> kitCompletions = getKitCompletions(sender,args,0);
                if(kitCompletions != null){
                    commands.addAll(kitCompletions);
                }
            }else{
                commands.add("claim");
            }
            if(kitPreviewEnabled){
                commands.add("preview");
            }
            commands.add("output");
            if(isAdmin){
                commands.add("give");commands.add("delete");commands.add("create");
                commands.add("reload");commands.add("reset");commands.add("edit");
                commands.add("verify");commands.add("migrate");commands.add("open");
            }
            for(String c : commands) {
                if(args[0].isEmpty() || c.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(c);
                }
            }
            return completions;
        }else {
            if(claimKitShortCommand && !isKnownSubCommand(args[0],kitPreviewEnabled)){
                return getOutputModeCompletions(args[1]);
            }

            if(args.length == 2) {
                if(!claimKitShortCommand){
                    commands.add("claim");
                }
                if(kitPreviewEnabled){
                    commands.add("preview");
                }
                commands.add("output");
                if(isAdmin){
                    commands.add("give");commands.add("delete");
                    commands.add("reset");commands.add("edit");
                    commands.add("open");
                }
                for(String c : commands) {
                    if(args[0].equalsIgnoreCase(c)){
                        if(c.equals("open")){
                            return getInventoryCompletions(args,1);
                        }else if(c.equals("output")){
                            completions.add("set");
                            completions.add("unset");
                            completions.add("get");
                            return filterCompletions(completions,args[1]);
                        }else{
                            return getKitCompletions(sender,args,1);
                        }

                    }
                }
            }else if(args.length == 3){
                if(args[0].equalsIgnoreCase("create") && isAdmin){
                    commands.add("original");commands.add("configurable");
                    return filterCompletions(commands,args[2]);
                }else if(args[0].equalsIgnoreCase("reset") && isAdmin){
                    for(Player p : Bukkit.getOnlinePlayers()) {
                        completions.add(p.getName());
                    }
                    completions.add("*");
                    return filterCompletions(completions,args[2]);
                }else if(args[0].equalsIgnoreCase("claim") && !claimKitShortCommand){
                    return getOutputModeCompletions(args[2]);
                }else if(args[0].equalsIgnoreCase("output")){
                    if(args[1].equalsIgnoreCase("set")){
                        return getOutputModeCompletions(args[2]);
                    }else if(args[1].equalsIgnoreCase("unset") && isAdmin){
                        return getPlayerCompletions(args[2]);
                    }else if(args[1].equalsIgnoreCase("get") && isAdmin){
                        return getPlayerCompletions(args[2]);
                    }
                }else if(args[0].equalsIgnoreCase("give") && isAdmin){
                    return getPlayerCompletions(args[2]);
                }
            }else if(args.length == 4){
                if(args[0].equalsIgnoreCase("give") && isAdmin){
                    return getOutputModeCompletions(args[3]);
                }else if(args[0].equalsIgnoreCase("output") && args[1].equalsIgnoreCase("set") && isAdmin){
                    return getPlayerCompletions(args[3]);
                }
            }
        }

        return null;
    }

    private boolean isKnownSubCommand(String arg,boolean previewEnabled){
        if(arg.equalsIgnoreCase("output")){
            return true;
        }
        if(previewEnabled && arg.equalsIgnoreCase("preview")){
            return true;
        }

        return arg.equalsIgnoreCase("claim")
                || arg.equalsIgnoreCase("give")
                || arg.equalsIgnoreCase("delete")
                || arg.equalsIgnoreCase("create")
                || arg.equalsIgnoreCase("reload")
                || arg.equalsIgnoreCase("reset")
                || arg.equalsIgnoreCase("edit")
                || arg.equalsIgnoreCase("verify")
                || arg.equalsIgnoreCase("migrate")
                || arg.equalsIgnoreCase("open");
    }

    private List<String> getOutputModeCompletions(String arg){
        List<String> completions = new ArrayList<>();
        completions.add("armor");
        completions.add("shulker");
        return filterCompletions(completions,arg);
    }

    private List<String> getPlayerCompletions(String arg){
        List<String> completions = new ArrayList<>();
        for(Player p : Bukkit.getOnlinePlayers()) {
            completions.add(p.getName());
        }
        return filterCompletions(completions,arg);
    }

    private List<String> filterCompletions(List<String> values,String arg){
        List<String> completions = new ArrayList<>();
        for(String value : values){
            if(arg.isEmpty() || value.toLowerCase().startsWith(arg.toLowerCase())){
                completions.add(value);
            }
        }
        if(completions.isEmpty()){
            return null;
        }
        return completions;
    }

    public List<String> getKitCompletions(CommandSender sender,String[] args,int argKitPos){
        List<String> completions = new ArrayList<>();
        String argKit = args[argKitPos];

        List<Kit> kits = plugin.getKitsManager().getKits();
        for(Kit kit : kits) {
            if(argKit.isEmpty() || kit.getName().toLowerCase().startsWith(argKit.toLowerCase())) {
                if(kit.playerHasPermission(sender)){
                    completions.add(kit.getName());
                }
            }
        }

        if(completions.isEmpty()){
            return null;
        }
        return completions;
    }

    public List<String> getInventoryCompletions(String[] args,int argInvPos){
        List<String> completions = new ArrayList<>();
        String argInv = args[argInvPos];

        List<KitInventory> inventories = plugin.getInventoryManager().getInventories();
        for(KitInventory inv : inventories) {
            if((argInv.isEmpty() || inv.getName().toLowerCase().startsWith(argInv.toLowerCase()))
                && !inv.getName().equals("preview_inventory") && !inv.getName().equals("buy_requirements_inventory")) {
                completions.add(inv.getName());
            }
        }

        if(completions.isEmpty()){
            return null;
        }
        return completions;
    }
}
