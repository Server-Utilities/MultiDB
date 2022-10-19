package tv.quaint.multidb.placeholders;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tv.quaint.multidb.MultiDB;
import tv.quaint.multidb.utils.DataSource;
import tv.quaint.multidb.utils.PluginUtils;
import tv.quaint.multidb.utils.StringUtils;
import tv.quaint.multidb.utils.objects.CustomSQLInfo;

public class MultiDBExpansion extends PlaceholderExpansion {
    private final MultiDB plugin;

    public MultiDBExpansion(MultiDB plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "multidb";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Quaint";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.contains(".")) {
            String[] things = params.split("\\.");
            String[] args = StringUtils.argsMinus(things, 0);
            if (args[0] == null) return MultiDB.configHandler.getValuesError();

            if (args[0].equals("self")) args[0] = player.getName();
            else if (args[0].equals("selfuuid")) args[0] = player.getUniqueId().toString();

            String[] query = StringUtils.argsMinus(args, 0);

            for (int i = 0; i < query.length; i ++) {
                query[i] = PlaceholderAPI.setPlaceholders(player, query[i]);
            }

            CustomSQLInfo sqlInfo = PluginUtils.getQueryByIdentifier(things[0]);
            if (sqlInfo == null) return MultiDB.configHandler.getValuesNoQuery();

            return DataSource.query(sqlInfo, player.getPlayer(), query);
        } else if (params.contains("_")) {
            String[] things = params.split("_");
            String[] args = StringUtils.argsMinus(things, 0);
            if (args[0] == null) return MultiDB.configHandler.getValuesError();

            if (args[0].equals("self")) args[0] = player.getName();
            else if (args[0].equals("selfuuid")) args[0] = player.getUniqueId().toString();

            String[] query = StringUtils.argsMinus(args, 0);

            for (int i = 0; i < query.length; i ++) {
                query[i] = PlaceholderAPI.setPlaceholders(player, query[i]);
            }

            CustomSQLInfo sqlInfo = PluginUtils.getQueryByIdentifier(things[0]);
            if (sqlInfo == null) return MultiDB.configHandler.getValuesNoQuery();

            return DataSource.query(sqlInfo, player.getPlayer(), query);
        } else {
            CustomSQLInfo sqlInfo = PluginUtils.getQueryByIdentifier(params);
            if (sqlInfo == null) return MultiDB.configHandler.getValuesNoQuery();

            return DataSource.query(sqlInfo, player.getPlayer());
        }

//        return MultiDB.configHandler.getValuesNotSet();
    }

    @Override
    public boolean register() {
        return super.register();
    }

    @Override
    public boolean persist() {
        return super.persist();
    }
}
