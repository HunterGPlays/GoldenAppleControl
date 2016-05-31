package net.terrocidepvp.goldenapplecontrol;

import net.terrocidepvp.goldenapplecontrol.commands.CommandManager;
import net.terrocidepvp.goldenapplecontrol.handlers.ItemManager;
import net.terrocidepvp.goldenapplecontrol.hooks.ClipPAPIHook;
import net.terrocidepvp.goldenapplecontrol.hooks.MaximPAPIHook;
import net.terrocidepvp.goldenapplecontrol.listeners.ConsumeListener;
import net.terrocidepvp.goldenapplecontrol.utils.ColorCodeUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class GoldenAppleControl extends JavaPlugin {

    private static GoldenAppleControl instance;
    public static GoldenAppleControl getInstance() {
        return instance;
    }

    private double serverVersion;
    private ItemManager itemManager;

    private String noPerm;
    private String noActiveCooldowns;
    private List<String> remainingTime;
    private List<String> placeholders = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        reloadConfig();

        if (!getConfig().isSet("config-version")) {
            getLogger().severe("The config.yml file is broken!");
            getLogger().severe("The plugin failed to detect a 'config-version'.");
            getLogger().severe("The plugin will not load until you generate a new, working config OR if you fix the config.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        int configVersion = 8;
        /*
         Updated for: Full recode
         Release: v1.8.1
        */
        if (getConfig().getInt("config-version") != configVersion) {
            getLogger().severe("Your config is outdated!");
            getLogger()
                    .severe("The plugin will not load unless you change the config version to " + configVersion + ".");
            getLogger().severe(
                    "This means that you will need to reset your config, as there may have been major changes to the plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("Loading values from config...");
        noPerm = ColorCodeUtil.translate(getConfig().getString("plugin-messages.no-permission"));
        noActiveCooldowns = ColorCodeUtil.translate(getConfig().getString("plugin-messages.no-active-cooldowns"));
        remainingTime = ColorCodeUtil.translate(getConfig().getStringList("plugin-messages.remaining-time"));
        itemManager = ItemManager.createItemManager(this);

        serverVersion = getMCVersion();
        getLogger().info("Running server version " + serverVersion);

        if (getServer().getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")) {
            getLogger().info("Attempting to register placeholders for Maxim's PAPI...");
            MaximPAPIHook.hook(this);
        }
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            getLogger().info("Attempting to hook into Clip's PAPI...");
            new ClipPAPIHook(this).hook();
        }

        CommandManager commandManager = new CommandManager();
        getCommand("gapple").setExecutor(commandManager);
        getCommand("goldenapplecontrol").setExecutor(commandManager);
        getCommand("gac").setExecutor(commandManager);
        getCommand("goldenapple").setExecutor(commandManager);
        getCommand("gapplecooldown").setExecutor(commandManager);
        getCommand("gcd").setExecutor(commandManager);

        new ConsumeListener(this);
    }

    private double getMCVersion() {
        String version = new String(Bukkit.getVersion());
        int pos = version.indexOf("(MC: ");
        version = version.substring(pos + 5).replace(")", "");
        String[] splitVersion = version.split("\\.");
        return Double.parseDouble(splitVersion[0] + "." + splitVersion[1]);
    }

    public double getServerVersion() {
        return serverVersion;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public String getNoPerm() {
        return noPerm;
    }

    public String getNoActiveCooldowns() {
        return noActiveCooldowns;
    }

    public List<String> getRemainingTime() {
        return remainingTime;
    }

    public List<String> getPlaceholders() {
        return placeholders;
    }
}
