package tv.quaint.multidb.utils.objects;

import tv.quaint.multidb.MultiDB;

public class PullAndPushInfo {
    public String column;
    public String host;
    public String table;
    public String where;

    public PullAndPushInfo(String column, String host, String table, String where) {
        this.column = column;
        this.host = host;
        this.table = table;
        this.where = where;
    }

    public Host getHostAsHost() {
        MultiDB.configHandler.reloadHosts();

        for (Host host : MultiDB.configHandler.loadedHosts) {
            if (host.identifier.equals(this.host)) return host;
        }

        return null;
    }
}
