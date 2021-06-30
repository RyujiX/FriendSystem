package net.ryu.friendsystem.commands.sub;

import net.ryu.friendsystem.commands.FriendCmd;
import net.ryu.friendsystem.commands.constructors.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RemoveFriendCmd extends SubCommand<FriendCmd> {
    public RemoveFriendCmd(FriendCmd command) {
        super(command, "friends.remove", "", "Remove a player from your friends list.", "remove");
    }

    @Override
    public void execute(Player sender, String aliasUsed, String... args) {
        if (args.length == 1) {
            Player player = Bukkit.getPlayer(args[0]);

            if (player != null) {
                if (!plugin.getUserHandler().alreadyFriends(sender.getUniqueId(), player.getUniqueId())) {
                    sender.sendMessage(getText().parse("&cYou don't seem to be friends with this user."));
                    return;
                }
                sender.sendMessage(getText().parse("&cYou have successfully removed " + player.getName() + " from your friends list."));
                plugin.getUserHandler().removeUserFriend(sender.getUniqueId(), player.getUniqueId());
                player.sendMessage(getText().parse("&c" + sender.getName() + " has removed you from their friends list."));
                plugin.getUserHandler().removeUserFriend(player.getUniqueId(), sender.getUniqueId());
            } else {
                sender.sendMessage(getText().parse("&cThe player you're attempting to add is not currently online!"));
            }
        }
    }
}
