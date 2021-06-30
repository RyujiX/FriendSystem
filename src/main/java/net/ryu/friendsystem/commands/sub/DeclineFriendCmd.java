package net.ryu.friendsystem.commands.sub;

import net.ryu.friendsystem.commands.FriendCmd;
import net.ryu.friendsystem.commands.constructors.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Date;

public class DeclineFriendCmd extends SubCommand<FriendCmd> {
    public DeclineFriendCmd(FriendCmd command) {
        super(command, "friends.reject", "", "Allows you to decline a friends request", "reject", "decline");
    }

    @Override
    public void execute(Player sender, String aliasUsed, String... args) {
        if (args.length == 1) {
            Player player = Bukkit.getPlayer(args[0]);

            if (player != null) {
                if (plugin.getUserHandler().getRequest(sender.getUniqueId()) != null) {
                    if (plugin.getUserHandler().getRequest(sender.getUniqueId()).contains(player.getUniqueId())) {
                        plugin.getUserHandler().clearRequest(player.getUniqueId(), sender.getUniqueId());

                        sender.sendMessage(getText().parse("&cYou have successfully declined " + player.getName() + " friend request."));
                        player.sendMessage(getText().parse("&c" + sender.getName() + " has declined your friend request."));
                    } else {
                        sender.sendMessage(getText().parse("&cYou don't seem to have a friend request from this user at this time."));
                    }
                }
            }
        }
    }
}
