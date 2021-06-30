package net.ryu.friendsystem.commands.sub;

import net.ryu.friendsystem.commands.FriendCmd;
import net.ryu.friendsystem.commands.constructors.SubCommand;
import net.ryu.friendsystem.data.Friends;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ListFriendCmd extends SubCommand<FriendCmd> {
    public ListFriendCmd(FriendCmd command) {
        super(command, "friends.list", "", "Gives you a list of all your friends.", "list");
    }

    @Override
    public void execute(Player sender, String aliasUsed, String... args) {
        if (plugin.getUserHandler().getFriends(sender.getUniqueId()).isEmpty()) {
            sender.sendMessage(getText().parse("&cYou currently have no friends added to your list!"));
            return;
        }
        sender.sendMessage(getText().parse("&e&l(!) &e&nFriends List:&r\n" +
                " "));
        for (Friends friends : plugin.getUserHandler().getFriends(sender.getUniqueId())) {
            sender.sendMessage(getText().parse(" &6&l* &e" + Bukkit.getPlayer(friends.uuid).getName() + " &8- &fAdded: " + friends.date));
        }
    }
}
