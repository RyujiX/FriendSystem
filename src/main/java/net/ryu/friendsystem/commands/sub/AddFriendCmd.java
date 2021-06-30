package net.ryu.friendsystem.commands.sub;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.ryu.friendsystem.commands.FriendCmd;
import net.ryu.friendsystem.commands.constructors.SubCommand;
import net.ryu.friendsystem.data.FriendRequest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddFriendCmd extends SubCommand<FriendCmd> {
    public AddFriendCmd(FriendCmd command) {
        super(command, "friends.add", "", "Send a request to add the player to your friends.", "add");
    }

    @Override
    public void execute(Player sender, String aliasUsed, String... args) {
        if (args.length == 1) {
            Player player = Bukkit.getPlayer(args[0]);

            if (player != null) {
                if (sender == player) {
                    sender.sendMessage(getText().parse("&cYou cannot add yourself as a friend."));
                    return;
                }

                if (plugin.getUserHandler().alreadyFriends(sender.getUniqueId(), player.getUniqueId())) {
                    sender.sendMessage(getText().parse("&cYou have already added this user to your friends list."));
                    return;
                }

                if (plugin.getUserHandler().getRequest(sender.getUniqueId()) != null) {
                    if (plugin.getUserHandler().getRequest(sender.getUniqueId()).contains(player.getUniqueId())) {
                        sender.sendMessage(getText().parse("&cAlready have an outgoing request to this user."));
                        return;
                    }
                }

                TextComponent accept = new TextComponent("" + ChatColor.GREEN + ChatColor.BOLD + "[Accept]" + ChatColor.RESET);
                accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.YELLOW + "Click here to accept the request.")));
                accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friends accept " + sender.getName()));
                TextComponent decline = new TextComponent("" + ChatColor.RED + ChatColor.BOLD + "[Decline]" + ChatColor.RESET);
                decline.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.YELLOW + "Click here to decline the request.")));
                decline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friends reject " + sender.getName()));

                player.sendMessage(getText().parse("&b-----------------------------------------------------\n" +
                        "&eFriend request from &r" + sender.getDisplayName()));
                player.spigot().sendMessage(new TextComponent(getText().parse("&eClick one: ")), accept, new TextComponent(getText().parse(" &8- ")), decline);
                player.sendMessage(getText().parse("&b-----------------------------------------------------"));
                sender.sendMessage(getText().parse("&b-----------------------------------------------------\n" +
                        "&eYou have successfully sent a friend request to " + player.getName() + ". They have 30 seconds to accept it!\n" +
                        "&b-----------------------------------------------------"));

                List<UUID> request = plugin.getUserHandler().getRequest(sender.getUniqueId()) != null ? plugin.getUserHandler().getRequest(sender.getUniqueId()) : new ArrayList<>();
                List<UUID> request2 = plugin.getUserHandler().getRequest(player.getUniqueId()) != null ? plugin.getUserHandler().getRequest(player.getUniqueId()) : new ArrayList<>();
                BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    request.remove(player.getUniqueId());
                    request2.remove(sender.getUniqueId());

                    player.sendMessage(getText().parse("&cThe friend request from " + sender.getName() + " has expired!"));
                    sender.sendMessage(getText().parse("&cThe friend request you sent to " + player.getName() + " has expired!"));
                }, 20L * 30);

                request.add(player.getUniqueId());
                request2.add(sender.getUniqueId());
                FriendRequest friendRequest = new FriendRequest();

                friendRequest.setRequest(request);
                friendRequest.setTask(task);
                plugin.getUserHandler().friendRequest.put(sender.getUniqueId(), friendRequest);

                friendRequest.setRequest(request2);
                friendRequest.setTask(task);
                plugin.getUserHandler().friendRequest.put(player.getUniqueId(), friendRequest);
            } else {
                sender.sendMessage(getText().parse("&cThe player you're attempting to add is not currently online!"));
            }
        }
    }
}
