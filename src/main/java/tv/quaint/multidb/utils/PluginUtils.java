package tv.quaint.multidb.utils;

import org.bukkit.entity.Player;
import tv.quaint.multidb.MultiDB;
import tv.quaint.multidb.utils.objects.CustomSQLInfo;
import tv.quaint.multidb.utils.objects.SavedQueries;
import tv.quaint.multidb.utils.objects.Syncable;

import java.util.ArrayList;
import java.util.List;

public class PluginUtils {
    public static List<String> getOnlinePlayerNames() {
        List<String> strings = new ArrayList<>();

        for (Player player : MultiDB.instance.getServer().getOnlinePlayers()) {
            strings.add(player.getName());
        }

        return strings;
    }

    public static List<Player> getOnlinePlayers() {
        return new ArrayList<>(MultiDB.instance.getServer().getOnlinePlayers());
    }

    public static List<String> getSyncablesAsStrings() {
        List<String> strings = new ArrayList<>();

        for (Syncable syncable : MultiDB.configHandler.loadedSyncables) {
            strings.add(syncable.identifier);
        }

        return strings;
    }

    public static Syncable getSyncableByIdentifier(String identifier) {
        for (Syncable syncable : MultiDB.configHandler.loadedSyncables) {
            if (identifier.equals(syncable.identifier)) return syncable;
        }

        return null;
    }

    public static List<String> getQueriesAsStrings() {
        List<String> strings = new ArrayList<>();

        for (CustomSQLInfo sqlInfo : MultiDB.configHandler.loadedQueries) {
            strings.add(sqlInfo.identifier);
        }

        return strings;
    }

    public static CustomSQLInfo getQueryByIdentifier(String identifier) {
        for (CustomSQLInfo sqlInfo : MultiDB.configHandler.loadedQueries) {
            if (identifier.equals(sqlInfo.identifier)) return sqlInfo;
        }

        return null;
    }

    public static List<String> getExecutionsAsStrings() {
        List<String> strings = new ArrayList<>();

        for (CustomSQLInfo sqlInfo : MultiDB.configHandler.loadedExecutions) {
            strings.add(sqlInfo.identifier);
        }

        return strings;
    }

    public static CustomSQLInfo getExecutionByIdentifier(String identifier) {
        for (CustomSQLInfo sqlInfo : MultiDB.configHandler.loadedExecutions) {
            if (identifier.equals(sqlInfo.identifier)) return sqlInfo;
        }

        return null;
    }

    public static SavedQueries getSavedQueryByPlayer(String playerUUID) {
        for (SavedQueries q : MultiDB.configHandler.loadedSavedQueries) {
            if (q.playerUUID.equals(playerUUID)) return q;
        }

        return null;
    }

    public static String getQueryResult(String playerUUID, String identifier) {
        MultiDB.configHandler.reloadSavedQueries();
        SavedQueries q = getSavedQueryByPlayer(playerUUID);
        if (q == null) return null;
        return q.getResult(identifier);
    }

    public static SavedQueries putQueryResult(String playerUUID, String identifer, String result) {
        SavedQueries q = getSavedQueryByPlayer(playerUUID);
        if (q == null) {
            q = createNewSavedQueries(playerUUID);
        }
        q = q.append(identifer, MultiDB.configHandler.getResyncSeconds(), result);
        MultiDB.configHandler.addSavedQueries(q);
        MultiDB.configHandler.saveQueriedResult(q);
        return q;
    }

    public static SavedQueries createNewSavedQueries(String playerUUID) {
        return MultiDB.configHandler.addSavedQueries(new SavedQueries(playerUUID));
    }

    public static void tickTillExpiry(SavedQueries q) {
        try {
            for (String identifier : q.results.keySet()) {
                int tillExpiry = q.getTillExpiry(identifier);

                if (tillExpiry > 0) {
                    q.updateTillExpiry(identifier, tillExpiry - 1);
                }
            }
        } catch (Exception e) {
            // do nothing.
        }
    }
}
