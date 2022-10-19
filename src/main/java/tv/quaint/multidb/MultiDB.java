package tv.quaint.multidb;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import tv.quaint.multidb.commands.ExecuteCommand;
import tv.quaint.multidb.commands.QueryCommand;
import tv.quaint.multidb.commands.SyncCommand;
import tv.quaint.multidb.config.ConfigHandler;
import tv.quaint.multidb.listeners.MainListener;
import tv.quaint.multidb.placeholders.MultiDBExpansion;
import tv.quaint.multidb.runnables.OneSecondRunnable;
import tv.quaint.multidb.utils.DataSource;

public final class MultiDB extends JavaPlugin {
    public static MultiDB instance;
    public static ConfigHandler configHandler;
//    public static TreeMap<String, TreeMap<String, String>> holders;
    public static boolean locked;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        configHandler = new ConfigHandler();
        locked = false;
        try {
            DataSource.reloadHikariHosts(); // Populate the Hikari Hosts.
        } catch (Exception e) {
            getLogger().severe("Something is mis-configured! Caught error: " + e.getMessage());
        }

        // Small check to make sure that PlaceholderAPI is installed
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new MultiDBExpansion(this).register();
        }

        // Listeners.
        this.getServer().getPluginManager().registerEvents(new MainListener(), this);

        // Commands.
        getCommand("sync").setExecutor(new SyncCommand());
        getCommand("query").setExecutor(new QueryCommand());
        getCommand("execute").setExecutor(new ExecuteCommand());

        // Runnables.
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new OneSecondRunnable(), 0, 20);
//        getServer().getScheduler().scheduleSyncRepeatingTask(this, new TestSavesRunnable(), 0, 60);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
