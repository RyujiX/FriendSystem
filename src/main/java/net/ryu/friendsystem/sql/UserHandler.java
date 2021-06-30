package net.ryu.friendsystem.sql;

import net.ryu.friendsystem.FriendSystemPlugin;
import net.ryu.friendsystem.data.FriendRequest;
import net.ryu.friendsystem.data.Friends;
import net.ryu.friendsystem.data.FriendsList;
import net.ryu.friendsystem.sql.database.DatabaseUser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UserHandler {
    public Map<UUID, FriendsList> friendsCache;
    public Map<UUID, FriendRequest> friendRequest;
    private final FriendSystemPlugin plugin;
    private final DatabaseUser databaseUser;

    public UserHandler(FriendSystemPlugin plugin) {
        this.plugin = plugin;
        databaseUser = new DatabaseUser(plugin);
        friendsCache = new HashMap<>();
        friendRequest = new HashMap<>();
    }

    public void loadFriends(UUID user) {
        List<Friends> list = new ArrayList<>();
        String table = user.toString().replace("-", "");
        ResultSet resultSet = plugin.getDatabase().executeQuery("SELECT uuid FROM " + table + ";");

        try {
            if (resultSet != null) {
                while (resultSet.next()) {
                    String result = resultSet.getString("uuid");
                    UUID uuid = UUID.fromString(result);
                    String date = (String) databaseUser.getValue(result, plugin.getDatabaseValues().getDate(), table);

                    list.add(new Friends().setUUID(uuid).setDate(date));
                }
                FriendsList friendsList = new FriendsList();
                friendsList.setFriends(list);

                friendsCache.put(user, friendsList);
            } else {
                friendsCache.put(user, new FriendsList());
                friendRequest.put(user, new FriendRequest());
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public List<UUID> getRequest(UUID uuid) {
        return friendRequest.containsKey(uuid) ? friendRequest.get(uuid).getRequest() : null;
    }

    public List<Friends> getFriends(UUID uuid) {
        return friendsCache.containsKey(uuid) ? friendsCache.get(uuid).getFriends() : null;
    }

    public void addUserFriends(UUID user, UUID friend, String date) {
        List<Friends> user1 = getFriends(user) == null ? new ArrayList<>() : getFriends(user);
        user1.add(new Friends().setUUID(friend).setDate(date));
        FriendsList userList= new FriendsList();
        userList.setFriends(user1);
        friendsCache.put(user, userList);
    }

    public void clearRequest(UUID user, UUID friend) {
        friendRequest.get(user).getRequest().remove(friend);
        plugin.getServer().getScheduler().cancelTask(friendRequest.get(user).getTask().getTaskId());
        friendRequest.get(friend).getRequest().remove(user);
        plugin.getServer().getScheduler().cancelTask(friendRequest.get(friend).getTask().getTaskId());
    }

    public boolean alreadyFriends(UUID user, UUID friend) {
        List<Friends> friendsList = getFriends(user) == null ? new ArrayList<>() : getFriends(user);
        plugin.getDatabase().deleteFrom(user.toString().replace("-", ""), friend);

        return friendsList.stream().anyMatch(friends -> friends.getUuid().equals(friend));
    }

    public void removeUserFriend(UUID user, UUID friend) {
        if (databaseUser.doesExist(friend.toString(), user.toString().replace("-", ""))) {
            plugin.getDatabase().deleteFrom(user.toString().replace("-", ""), friend);
        }
        List<Friends> friendsList = getFriends(user) == null ? new ArrayList<>() : getFriends(user);
        for (Friends friends : friendsList) {
            if (friends.getUuid().equals(friend)) {
                friendsList.remove(friends);
                return;
            }
        }
    }

    public void saveUser(UUID uuid) {
        if (getFriends(uuid).isEmpty()) return;
        getFriends(uuid).forEach(list -> databaseUser.setValues(uuid.toString().replace("-", ""), list.getUuid().toString(), list.getDate()));
    }
}
