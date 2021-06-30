package net.ryu.friendsystem.data;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class FriendsList {
    public UUID uuid;
    public List<Friends> friends;
}
