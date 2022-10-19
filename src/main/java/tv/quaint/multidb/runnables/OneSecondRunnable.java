package tv.quaint.multidb.runnables;

import org.bukkit.entity.Player;
import tv.quaint.multidb.MultiDB;
import tv.quaint.multidb.utils.DataSource;
import tv.quaint.multidb.utils.PluginUtils;
import tv.quaint.multidb.utils.objects.AutoSyncInfo;
import tv.quaint.multidb.utils.objects.SavedQueries;
import tv.quaint.multidb.utils.objects.Syncable;

public class OneSecondRunnable implements Runnable {
    public int reset;
    public int count;
    public int sReset;
    public int sCount;
    public int resyncTickedSeconds = 0;

    public OneSecondRunnable() {
        this.reset = 1;
        this.count = 0;
        this.sCount = 0;
    }

    @Override
    public void run() {
        if (this.count <= 0) {
            this.count = this.reset;
            done();
        }

        this.count --;
    }

    public void done() {
        AutoSyncInfo syncInfo = MultiDB.configHandler.getAutoSync();
        if (syncInfo.interval == -1) return;

        sReset = syncInfo.interval;
        if (this.sCount <= 0) {
            this.sCount = this.sReset;

            MultiDB.configHandler.reloadHosts();
            MultiDB.configHandler.reloadSyncables();
            MultiDB.configHandler.reloadQueries();
            MultiDB.configHandler.reloadExecutions();

            for (Player player : MultiDB.instance.getServer().getOnlinePlayers()) {
                for (Syncable syncable : MultiDB.configHandler.loadedSyncables) {
                    DataSource.sync(syncable, player);
                }
            }
        }

        if (MultiDB.configHandler.getResyncSeconds() <= resyncTickedSeconds) {
            MultiDB.configHandler.reloadSavedQueries();

            for (Player player : PluginUtils.getOnlinePlayers()) {
                SavedQueries q = PluginUtils.getSavedQueryByPlayer(player.getUniqueId().toString());
                if (q == null) continue;

                PluginUtils.tickTillExpiry(q);
            }
            resyncTickedSeconds = 0;
        }

        resyncTickedSeconds ++;
        this.sCount --;
    }
}
