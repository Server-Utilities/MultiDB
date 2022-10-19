package tv.quaint.multidb.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import tv.quaint.multidb.MultiDB;
import tv.quaint.multidb.utils.DataSource;
import tv.quaint.multidb.utils.objects.Syncable;

public class MainListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for (Syncable syncable : MultiDB.configHandler.loadedSyncables) {
            DataSource.sync(syncable, player);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (Syncable syncable : MultiDB.configHandler.loadedSyncables) {
            DataSource.sync(syncable, player);
        }
    }
}
