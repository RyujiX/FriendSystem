package net.ryu.friendsystem;

import lombok.Getter;
import net.ryu.friendsystem.commands.FriendCmd;
import net.ryu.friendsystem.commands.constructors.CommandManager;
import net.ryu.friendsystem.listener.PlayerListener;
import net.ryu.friendsystem.sql.UserHandler;
import net.ryu.friendsystem.sql.database.Database;
import net.ryu.friendsystem.sql.database.DatabaseAuthentication;
import net.ryu.friendsystem.sql.database.DatabaseValues;
import org.bukkit.plugin.java.JavaPlugin;

public class FriendSystemPlugin extends JavaPlugin {
    @Getter public Database database;
    @Getter public DatabaseAuthentication authentication;
    @Getter public DatabaseValues databaseValues;
    @Getter public UserHandler userHandler;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        authentication = new DatabaseAuthentication();
        databaseValues = new DatabaseValues();

        connect();

        new CommandManager(this).registerCommand(new FriendCmd(this));
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        userHandler = new UserHandler(this);

        if (!getServer().getOnlinePlayers().isEmpty()) {
            getServer().getOnlinePlayers().forEach(player -> {
                getDatabase().createTable(getDatabaseValues().getAllColumnLabels(), player.getUniqueId().toString().replace("-", ""));
                getUserHandler().loadFriends(player.getUniqueId());
            });
        }
    }

    @Override
    public void onDisable() {
        if (!getServer().getOnlinePlayers().isEmpty()) {
            getServer().getOnlinePlayers().forEach(player -> getUserHandler().saveUser(player.getUniqueId()));
        }
        if (!getUserHandler().friendsCache.isEmpty()) {
            getUserHandler().friendsCache.clear();
        }
        if (!getUserHandler().friendRequest.isEmpty()) {
            getUserHandler().friendRequest.clear();
        }
    }

    public void connect() {
        authentication.setHost(getConfig().getString("MySQL.host"));
        authentication.setPort(getConfig().getString("MySQL.port"));
        authentication.setUsername(getConfig().getString("MySQL.username"));
        authentication.setPassword(getConfig().getString("MySQL.password"));
        authentication.setDatabase(getConfig().getString("MySQL.database"));

        database = new Database(authentication);
        database.connect();
    }
}
