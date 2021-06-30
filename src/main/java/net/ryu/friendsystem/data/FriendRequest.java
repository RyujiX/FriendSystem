package net.ryu.friendsystem.data;

import lombok.Data;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.UUID;

@Data
public class FriendRequest {
    public List<UUID> request;
    public BukkitTask task;
}
