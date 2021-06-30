package net.ryu.friendsystem.commands;

import net.ryu.friendsystem.FriendSystemPlugin;
import net.ryu.friendsystem.commands.constructors.Command;
import net.ryu.friendsystem.commands.constructors.SubCommand;
import net.ryu.friendsystem.commands.sub.*;
import org.bukkit.command.CommandSender;

public class FriendCmd extends Command<FriendSystemPlugin> {
    public FriendCmd(FriendSystemPlugin plugin) {
        super(plugin, "", "friend", "friends");

        setDefaultSubCommand(new HelpSubCommand(this));

        addSubCommand(new AddFriendCmd(this));
        addSubCommand(new RemoveFriendCmd(this));
        addSubCommand(new ListFriendCmd(this));
        addSubCommand(new AcceptFriendCmd(this));
        addSubCommand(new DeclineFriendCmd(this));
    }

    private class HelpSubCommand extends SubCommand<FriendCmd> {

        public HelpSubCommand(FriendCmd command) {
            super(command, "friends.help", "", "Gives you a list of commands.", "help");
        }

        @Override
        public void execute(CommandSender sender, String aliasUsed, String... args) {
            sender.sendMessage(getText().parse("&e&l(!)&e&n Friends Commands:&r\n" +
                    " \n" +
                    " &6&l* &e/friends add [player] &6- &7Send a request to add the player to your friends.\n" +
                    " &6&l* &e/friends remove [player] &6- &7Remove a player from your friends list.\n" +
                    " &6&l* &e/friends list &6- &7Gives you a list of all your friends.\n" +
                    " &6&l* &e/friends accept [player] &6- &7Allows you to accept a friends request.\n" +
                    " &6&l* &e/friends decline [player] &6- &7Allows you to decline a friends request."));
        }
    }
}
