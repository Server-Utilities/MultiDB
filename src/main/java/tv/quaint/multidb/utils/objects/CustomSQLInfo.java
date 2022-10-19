package tv.quaint.multidb.utils.objects;

import tv.quaint.multidb.MultiDB;

public class CustomSQLInfo {
    public String identifier;
    public String host;
    public String sql;

    public CustomSQLInfo(String identifier, String host, String sql) {
        this.identifier = identifier;
        this.host = host;
        this.sql = sql;
    }

    public Host getHostAsHost() {
        MultiDB.configHandler.reloadHosts();

        for (Host host : MultiDB.configHandler.loadedHosts) {
            if (host.identifier.equals(this.host)) return host;
        }

        return null;
    }
}
