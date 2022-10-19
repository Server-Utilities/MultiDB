package tv.quaint.multidb.utils.objects;

public class AutoSyncInfo {
    public int interval;
    public boolean onJoin;
    public boolean onLeave;

    public AutoSyncInfo(int interval, boolean onJoin, boolean onLeave) {
        this.interval = interval;
        this.onJoin = onJoin;
        this.onLeave = onLeave;
    }
}
