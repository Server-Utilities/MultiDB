package tv.quaint.multidb.config;

import de.leonhard.storage.Config;
import de.leonhard.storage.SimplixBuilder;
import de.leonhard.storage.sections.FlatFileSection;
import tv.quaint.multidb.MultiDB;
import tv.quaint.multidb.utils.DataSource;
import tv.quaint.multidb.utils.PluginUtils;
import tv.quaint.multidb.utils.objects.*;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ConfigHandler {
    Config config;
    Config queries;
    String cstring = "config.yml";
    String qstring = "saved-queries.yml";
    File file = new File(MultiDB.instance.getDataFolder(), cstring);
    File qfile = new File(MultiDB.instance.getDataFolder(), qstring);

    public List<Host> loadedHosts = new ArrayList<>();
    public List<Syncable> loadedSyncables = new ArrayList<>();
    public List<CustomSQLInfo> loadedQueries = new ArrayList<>();
    public List<CustomSQLInfo> loadedExecutions = new ArrayList<>();
    public List<SavedQueries> loadedSavedQueries = new ArrayList<>();
//    public List<SavedQueries> toSave = new ArrayList<>();

    public ConfigHandler() {
        this.config = loadConfig();

        this.loadedHosts = getHosts();
        this.loadedSyncables = getSyncables();
        this.loadedQueries = getQueries();
        this.loadedExecutions = getExecutions();

        this.queries = loadSavedQueries();
        this.loadedSavedQueries = getSavedQueries();
    }

    public Config loadConfig() {
        if (! file.exists()) {
            try {
                MultiDB.instance.getDataFolder().mkdirs();
                try (InputStream in = MultiDB.instance.getResource(cstring)) {
                    Files.copy(in, file.toPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return SimplixBuilder.fromFile(file).createConfig();
    }

    public Config loadSavedQueries() {
        if (! qfile.exists()) {
            try {
                MultiDB.instance.getDataFolder().mkdirs();
                qfile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return SimplixBuilder.fromFile(qfile).createConfig();
    }

    public void reloadConfig() {
        config = loadConfig();
    }

    public void reloadSavedQueries() {
        queries = loadSavedQueries();
    }

    public void reloadHosts() {
        loadedHosts = getHosts();

        DataSource.reloadHikariHosts();
    }

    public void reloadSyncables() {
        loadedSyncables = getSyncables();
    }

    public void reloadQueries() {
        loadedQueries = getQueries();
    }

    public void reloadExecutions() {
        loadedExecutions = getExecutions();
    }

    public String getValuesNotSet() {
        reloadConfig();

        return config.getString("values.not-set-yet");
    }

    public String getValuesError() {
        reloadConfig();

        return config.getString("values.error");
    }

    public String getValuesNoQuery() {
        reloadConfig();

        return config.getString("values.no-query");
    }

    public List<Host> getHosts() {
        reloadConfig();

        FlatFileSection section = config.getSection("hosts");

        List<Host> list = new ArrayList<>();

        for (String key : section.singleLayerKeySet()) {
            FlatFileSection host_section = config.getSection("hosts." + key);

            Host host = new Host(
                    key,
                    host_section.getString("link"),
                    host_section.getString("user"),
                    host_section.getString("pass")
            );

            list.add(host);
        }

        return list;
    }

    public List<Syncable> getSyncables() {
        reloadConfig();

        FlatFileSection section = config.getSection("syncables");

        List<Syncable> list = new ArrayList<>();

        for (String key : section.singleLayerKeySet()) {
            FlatFileSection host_section = config.getSection("syncables." + key);
            FlatFileSection pull_section = config.getSection("syncables." + key + ".pull");
            FlatFileSection push_section = config.getSection("syncables." + key + ".push");

            PullAndPushInfo pull = new PullAndPushInfo(
                    pull_section.getString("column"),
                    pull_section.getString("host"),
                    pull_section.getString("table"),
                    pull_section.getString("where")
            );
            PullAndPushInfo push = new PullAndPushInfo(
                    push_section.getString("column"),
                    push_section.getString("host"),
                    push_section.getString("table"),
                    push_section.getString("where")
            );

            Syncable syncable = new Syncable(
                    key,
                    host_section.getBoolean("is-string"),
                    pull,
                    push
            );

            list.add(syncable);
        }

        return list;
    }

    public AutoSyncInfo getAutoSync() {
        reloadConfig();

        FlatFileSection section = config.getSection("auto-sync");

        return new AutoSyncInfo(
                section.getInt("every"),
                section.getBoolean("join"),
                section.getBoolean("leave")
        );
    }

    public List<CustomSQLInfo> getQueries() {
        reloadConfig();

        FlatFileSection section = config.getSection("queries");

        List<CustomSQLInfo> list = new ArrayList<>();

        for (String key : section.singleLayerKeySet()) {
            FlatFileSection sql_section = config.getSection("queries." + key);

            CustomSQLInfo csql = new CustomSQLInfo(
                    key,
                    sql_section.getString("host"),
                    sql_section.getString("sql")
            );

            list.add(csql);
        }

        return list;
    }

    public List<CustomSQLInfo> getExecutions() {
        FlatFileSection section = config.getSection("executions");

        List<CustomSQLInfo> list = new ArrayList<>();

        for (String key : section.singleLayerKeySet()) {
            FlatFileSection sql_section = config.getSection("executions." + key);

            CustomSQLInfo csql = new CustomSQLInfo(
                    key,
                    sql_section.getString("host"),
                    sql_section.getString("sql")
            );

            list.add(csql);
        }

        return list;
    }

    public boolean isValidSyncable(String toTest) {
        for (String valid : PluginUtils.getSyncablesAsStrings()) {
            if (valid.equals(toTest)) return true;
        }

        return false;
    }

    public boolean isValidQuery(String toTest) {
        for (String valid : PluginUtils.getQueriesAsStrings()) {
            if (valid.equals(toTest)) return true;
        }

        return false;
    }

    public boolean isValidExecution(String toTest) {
        for (String valid : PluginUtils.getExecutionsAsStrings()) {
            if (valid.equals(toTest)) return true;
        }

        return false;
    }

    public List<SavedQueries> getSavedQueries() {
        reloadSavedQueries();

        List<SavedQueries> qs = new ArrayList<>();

        for (String key : queries.singleLayerKeySet()) {
            SavedQueries saved = new SavedQueries(key);

            for (String identifier : queries.getSection(key).singleLayerKeySet()) {
                saved = saved.append(identifier, getResyncSeconds(), queries.getSection(key).getString(identifier));
            }

            qs.add(saved);
        }

        return qs;
    }

    public void saveAllQueriedResults() {
        try {
            queries.getFile().delete();
            queries.getFile().createNewFile();
            for (SavedQueries q : loadedSavedQueries) {
                for (String identifier : q.results.keySet()) {
                    queries.set(q.playerUUID + "." + identifier, q.results.get(identifier));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveQueriedResult(SavedQueries q) {
//        MultiDB.instance.getLogger().info("Player Name: " + q.playerUUID);
        for (String identifier : q.results.keySet()) {
//            MultiDB.instance.getLogger().info("Setting: < " + identifier + " , " + q.results.get(identifier).value + ">");
            queries.set(q.playerUUID + "." + identifier, q.results.get(identifier).value);
        }
    }

//    public void putToBeSaved(SavedQueries q) {
//        toSave.add(q);
//    }
//
//    public void takeToBeSaved(SavedQueries q) {
//        toSave.remove(q);
//    }

    public SavedQueries addSavedQueries(SavedQueries q) {
        loadedSavedQueries.removeIf(qu -> qu.playerUUID.equals(q.playerUUID));

        loadedSavedQueries.add(q);

        return q;
    }

    public int getResyncSeconds() {
        reloadConfig();

        return config.getInt("resync.every");
    }
}
