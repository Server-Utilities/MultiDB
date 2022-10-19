package tv.quaint.multidb.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import tv.quaint.multidb.MultiDB;
import tv.quaint.multidb.utils.objects.*;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.HashMap;

public class DataSource {
    public static HashMap<Host, HikariDataSource> loadedHosts = new HashMap<>();

    public static void clearHikariHosts() {
        for (HikariDataSource source : loadedHosts.values()) {
            source.close();
        }

        loadedHosts.clear();
    }

    public static void reloadHikariHosts() {
        clearHikariHosts();

        for (Host host : MultiDB.configHandler.loadedHosts) {
            try {
                HikariConfig config = new HikariConfig();
                HikariDataSource ds;

                config.setJdbcUrl(host.link);
                config.setUsername(host.user);
                config.setPassword(host.pass);
                config.addDataSourceProperty("cachePrepStmts", "true");
                config.addDataSourceProperty("prepStmtCacheSize", "250");
                config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                config.addDataSourceProperty("allowMultiQueries", "true");
                if (host.link.endsWith("<host>:<port>/<database>")) throw new Exception("Host not set-up!");
                ds = new HikariDataSource(config);

                loadedHosts.put(host, ds);
            } catch (Exception e) {
                MultiDB.instance.getLogger().warning("In your config.yml, Host '" + host.identifier + "' is not set up correctly!");
            }
        }
    }

    public static String sync(Syncable syncable, Player on) {
        String pulled = pull(syncable.pullFrom, on);
        push(syncable.pushTo, pulled, on, syncable.isString);

        return pulled;
    }

    public static String pull(PullAndPushInfo pullFrom, Player on) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();

            HikariDataSource dataSource = loadedHosts.get(pullFrom.getHostAsHost());
            Connection connection = dataSource.getConnection();

            String sql = "SELECT " + pullFrom.column + " FROM " + pullFrom.table + " WHERE " + pullFrom.where;

            PreparedStatement statement = connection.prepareStatement(PlaceholderAPI.setPlaceholders(on, sql));

            ResultSet result = statement.executeQuery();

            String pulled = "";

            while (result.next()) {
                pulled = String.valueOf(result.getObject(1));
            }

            statement.close();
            return pulled;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return MultiDB.configHandler.getValuesError();
        }
    }

    public static void push(PullAndPushInfo pushTo, String thing, Player on, boolean isString) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();

            HikariDataSource dataSource = loadedHosts.get(pushTo.getHostAsHost());
            Connection connection = dataSource.getConnection();

            String sql;
            if (! isString) {
                sql = "UPDATE " + pushTo.table + " SET " + pushTo.column + " = " + thing + " WHERE " + pushTo.where;
            } else {
                sql = "UPDATE " + pushTo.table + " SET " + pushTo.column + " = '" + thing + "' WHERE " + pushTo.where;
            }

            PreparedStatement statement = connection.prepareStatement(PlaceholderAPI.setPlaceholders(on, sql));

            statement.execute();
            statement.close();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static String query(CustomSQLInfo sqlInfo, Player on, String... extra) {
        SavedQueries q = PluginUtils.getSavedQueryByPlayer(on.getUniqueId().toString());
        if (q != null) {
            if (q.getTillExpiry(sqlInfo.identifier) > 0) {
                return q.getResult(sqlInfo.identifier);
            }
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();

            HikariDataSource dataSource = loadedHosts.get(sqlInfo.getHostAsHost());
            Connection connection = dataSource.getConnection();

            PreparedStatement statement = connection.prepareStatement(PlaceholderAPI.setPlaceholders(on, StringUtils.replaceArgs(sqlInfo.sql, extra)));

            ResultSet result = statement.executeQuery();

            String pulled = "";

            while (result.next()) {
                pulled = String.valueOf(result.getObject(1));
            }

            PluginUtils.putQueryResult(on.getUniqueId().toString(), sqlInfo.identifier, pulled);

            statement.close();
            return pulled;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return MultiDB.configHandler.getValuesError();
        }
    }

    public static void execute(CustomSQLInfo sqlInfo, Player on, String... extra) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();

            HikariDataSource dataSource = loadedHosts.get(sqlInfo.getHostAsHost());
            Connection connection = dataSource.getConnection();

            PreparedStatement statement = connection.prepareStatement(PlaceholderAPI.setPlaceholders(on, StringUtils.replaceArgs(sqlInfo.sql, extra)));

            statement.execute();
            statement.close();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
