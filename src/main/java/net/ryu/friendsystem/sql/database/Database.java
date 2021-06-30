package net.ryu.friendsystem.sql.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.ryu.friendsystem.FriendSystemPlugin;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

public class Database {
    private final DatabaseAuthentication authentication;
    private final DatabaseValues values;
    private HikariDataSource hikariDataSource;

    public Database(DatabaseAuthentication authentication) {
        this.authentication = authentication;
        this.values = new DatabaseValues();
    }

    public void connect() {
        try {
            HikariConfig hikariConfig = new HikariConfig();

            hikariConfig.setJdbcUrl("jdbc:mysql://" + authentication.getHost() + ":" + authentication.getPort() + "/" + authentication.getDatabase() + "?characterEncoding=utf8");
            hikariConfig.setUsername(authentication.getUsername());
            hikariConfig.setPassword(authentication.getPassword());
            hikariConfig.setMinimumIdle(0);
            hikariConfig.setMaximumPoolSize(30);

            hikariConfig.addDataSourceProperty("useSSL", false);
            hikariConfig.addDataSourceProperty("cachePrepStmts", true);
            hikariConfig.addDataSourceProperty("prepStmtCacheSize", 250);
            hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
            hikariConfig.addDataSourceProperty("useServerPrepStmts", true);
            hikariConfig.addDataSourceProperty("useLocalSessionState", true);
            hikariConfig.addDataSourceProperty("useLocalTransactionState", true);
            hikariConfig.addDataSourceProperty("rewriteBatchedStatements", true);
            hikariConfig.addDataSourceProperty("cacheResultSetMetadata", true);
            hikariConfig.addDataSourceProperty("cacheServerConfiguration", true);
            hikariConfig.addDataSourceProperty("elideSetAutoCommits", true);
            hikariConfig.addDataSourceProperty("maintainTimeStats", false);

            hikariConfig.setIdleTimeout(60000);
            hikariConfig.setConnectionTimeout(60000);
            hikariConfig.setValidationTimeout(3000);

            this.hikariDataSource = new HikariDataSource(hikariConfig);
        } catch (Exception exception) {
            Bukkit.getServer().getPluginManager().disablePlugin(FriendSystemPlugin.getProvidingPlugin(FriendSystemPlugin.class));
            Bukkit.getLogger().warning("Disabling plugin due to cannot connect to the Database!");
        }
    }

    public Connection getConnection() throws SQLException {
        return hikariDataSource.getConnection();
    }

    public ResultSet executeQuery(boolean hideErrors, String sql, Object... objects) {
        try(Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            int count = 1;

            for(Object object : objects) {
                preparedStatement.setObject(count, object);
                count++;
            }

            return preparedStatement.executeQuery();
        } catch (SQLException ex) {
            if(!hideErrors) {
                ex.printStackTrace();
            }
        }

        return null;
    }

    public ResultSet executeQuery(String sql, Object... objects) {
        return executeQuery(false, sql, objects);
    }

    public void executeUpdate(boolean hideErrors, String sql, Object... objects) {
        try(Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int count = 1;

            for(Object object : objects) {
                preparedStatement.setObject(count, object);
                count++;
            }

            preparedStatement.execute();
        } catch (SQLException ex) {
            if(!hideErrors) {
                ex.printStackTrace();
            }
        }
    }

    public void executeUpdate(String sql, Object... objects) {
        executeUpdate(false, sql, objects);
    }

    public void deleteFrom(String table, UUID uuid) {
        executeUpdate("DELETE FROM " + table + " WHERE uuid = '" + uuid.toString() + "'");
    }

    public void createTable(List<String> columns, String table) {
        StringBuilder stringBuilder = new StringBuilder("(");
        Queue<String> queue = new LinkedList<>(columns);

        while(!queue.isEmpty()) {
            stringBuilder.append(queue.poll());

            if(!queue.isEmpty()) {
                stringBuilder.append(", ");
            }
        }

        stringBuilder.append(");");

        executeUpdate("CREATE TABLE IF NOT EXISTS " + table + " " + stringBuilder);
    }
}
