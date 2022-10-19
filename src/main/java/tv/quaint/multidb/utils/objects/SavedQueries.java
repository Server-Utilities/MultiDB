package tv.quaint.multidb.utils.objects;

import tv.quaint.multidb.utils.SingleSet;

import java.util.TreeMap;

public class SavedQueries {
    public String playerUUID;
    public TreeMap<String, SingleSet<Integer, String>> results; // < Identifier , < Till Expiry,  Query Result > >

    public SavedQueries(String playerUUID, TreeMap<String, SingleSet<Integer, String>> results) {
        this.playerUUID = playerUUID;
        this.results = results;
    }

    public SavedQueries(String playerUUID) {
        this(playerUUID, new TreeMap<>());
    }

    public SavedQueries append(String identifier, int tillExpiry, String result) {
        results.put(identifier, new SingleSet<>(tillExpiry, result));

        return this;
    }

    public SavedQueries clone() {
        return new SavedQueries(playerUUID, results);
    }

    public int updateTillExpiry(String identifier, int newTillExpiry) {
        SingleSet<Integer, String> set = results.get(identifier);
        if (set == null) return -1;
        set.updateKey(newTillExpiry);
        results.put(identifier, set);
        return newTillExpiry;
    }

    public int getTillExpiry(String identifier) {
        SingleSet<Integer, String> set = results.get(identifier);
        if (set == null) return -1;
        return set.key;
    }

    public String getResult(String identifier) {
        SingleSet<Integer, String> set = results.get(identifier);
        if (set == null) return null;
        return set.value;
    }
}
