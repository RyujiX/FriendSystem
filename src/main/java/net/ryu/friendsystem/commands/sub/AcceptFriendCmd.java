package net.ryu.friendsystem.commands.sub;

import net.ryu.friendsystem.commands.FriendCmd;
import net.ryu.friendsystem.commands.constructors.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AcceptFriendCmd extends SubCommand<FriendCmd> {
    public AcceptFriendCmd(FriendCmd command) {
        super(command, "friends.accept", "", "Allows you to accept a friends request.", "accept");
    }

    @Override
    public void execute(Player sender, String aliasUsed, String... args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (args.length == 1) {
            Player player = Bukkit.getPlayer(args[0]);

            if (player != null) {
                if (plugin.getUserHandler().getRequest(sender.getUniqueId()) != null) {
                    if (plugin.getUserHandler().getRequest(sender.getUniqueId()).contains(player.getUniqueId())) {
                        plugin.getUserHandler().clearRequest(player.getUniqueId(), sender.getUniqueId());

                        plugin.getUserHandler().addUserFriends(sender.getUniqueId(), player.getUniqueId(), sdf.format(new Date(System.currentTimeMillis())));
                        sender.sendMessage(getText().parse("&aYou successfully accepted " + player.getName() + " friend request."));
                        plugin.getUserHandler().addUserFriends(player.getUniqueId(), sender.getUniqueId(), sdf.format(new Date(System.currentTimeMillis())));
                        player.sendMessage(getText().parse("&a" + sender.getName() + " has accepted your friend request."));
                    } else {
                        sender.sendMessage(getText().parse("&cYou don't seem to have a friend request from this user at this time."));
                    }
                }
            }
        }
    }
}
