package net.ryu.friendsystem.listener;

import net.ryu.friendsystem.FriendSystemPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private final FriendSystemPlugin plugin;

    public PlayerListener(FriendSystemPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        plugin.getDatabase().createTable(plugin.getDatabaseValues().getAllColumnLabels(), player.getUniqueId().toString().replace("-", ""));
        plugin.getUserHandler().loadFriends(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        plugin.getUserHandler().saveUser(player.getUniqueId());
        plugin.getUserHandler().friendsCache.remove(player.getUniqueId());
    }
}
