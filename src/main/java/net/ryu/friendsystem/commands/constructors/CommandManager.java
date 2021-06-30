package net.ryu.friendsystem.commands.constructors;

import net.ryu.friendsystem.FriendSystemPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Predicate;

/**
 * Handles the processing of commands.
 */
public class CommandManager {
    private static SimpleCommandMap COMMAND_REGISTER;
    private final List<Command<?>> commands = new ArrayList<>();

    public CommandManager(FriendSystemPlugin plugin) {
        String version = plugin.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        String name = "org.bukkit.craftbukkit."+version+".CraftServer";
        Class<?> craftserver = null;
        try {
            craftserver = Class.forName(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (craftserver != null) {
                COMMAND_REGISTER = (SimpleCommandMap) craftserver.cast(plugin.getServer()).getClass().getMethod("getCommandMap")
                        .invoke(plugin.getServer());
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void registerCommand(Command<?>... commands) {
        for (Command<?> command : commands) {
            registerCommand(command);
        }
    }

    private void registerCommand(Command<?> command) {
        commands.add(command);

        COMMAND_REGISTER.register("", new org.bukkit.command.Command(command.getAliases()[0], command.getDescription(), "/" + command.getName().toLowerCase(),
                Arrays.asList(Arrays.copyOfRange(command.getAliases(), 1, command.getAliases().length))) {
            {
                command.setBukkitCommand(this);
            }

            @Override
            public final boolean execute(CommandSender sender, String aliasUsed, String[] args) {
                if (command.getSubCommands() != null) {
                    SubCommand<?> defaultSubCommand = command.getDefaultSubCommand();
                    SubCommand<?> subCommand = args.length == 0 ? defaultSubCommand : command.getSubCommands().get(args[0]);
                    boolean usedLabel = args.length > 0 && subCommand != null;

                    if (subCommand == null) {
                        subCommand = defaultSubCommand;
                    }

                    if (!sender.hasPermission(subCommand.getPermission())) {
                        sender.sendMessage(command.getText().parse("&cSorry, you don't have permission to execute this command!"));
                        return true;
                    }

                    if ((usedLabel ? args.length - 1 : args.length) < subCommand.getRequiredLength()) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInvalid usage! Correct use: /" + aliasUsed + (args.length == 0 ? "" : " " + args[0]) + " " + ChatColor.stripColor(subCommand.getUsage())));
                        return false;
                    }

                    if (!usedLabel && subCommand.equals(defaultSubCommand)) {
                        defaultSubCommand.execute(sender, aliasUsed, args);

                        if (sender instanceof Player) {
                            defaultSubCommand.execute((Player) sender, aliasUsed, args);
                        } else {
                            defaultSubCommand.execute((ConsoleCommandSender) sender, aliasUsed, args);
                        }
                    } else {
                        aliasUsed = args[0];
                        args = Arrays.copyOfRange(args, 1, args.length);

                        subCommand.execute(sender, aliasUsed, args);

                        if (sender instanceof Player) {
                            subCommand.execute((Player) sender, aliasUsed, args);
                        } else {
                            subCommand.execute((ConsoleCommandSender) sender, aliasUsed, args);
                        }
                    }

                    return true;
                }

                if (args.length < command.getRequiredLength()) {
                    command.usage(sender, aliasUsed);
                    return false;
                }

                command.execute(sender, aliasUsed, args);

                if (sender instanceof Player) {
                    Player player = (Player) sender;

                    if (command.getCanExecute() != null) {
                        for (Predicate<Player> canExecute : command.getCanExecute()) {
                            if (!canExecute.test(player)) {
                                return false;
                            }
                        }
                    }

                    command.execute(player, aliasUsed, args);
                } else if (sender instanceof ConsoleCommandSender) {
                    command.execute((ConsoleCommandSender) sender, aliasUsed, args);
                }

                return true;
            }
            
            /**
             * @param sender    The player or console requesting tab completion
             * @param aliasUsed The alias of the command being tabbed
             * @param args      The current arguments the sender has before tab completion
             * @param location  The position looked at by the Player, null if Console
             * @return If sub commands are active, args[0] will tab to the nearest one,
             * otherwise if also not overridden, it will return player names from online players.
             * Return {@link Collections#emptyList()} if wanting to tab complete with nothing
             */
            @Override
            public final List<String> tabComplete(CommandSender sender, String aliasUsed, String[] args, Location location) {
                Map<String, SubCommand<?>> subCommands = command.getSubCommands();

                if (subCommands != null && args.length > 0) {
                    if (args.length == 1) {
                        return findClosest(subCommands.keySet(), args[0]);
                    } else {
                        SubCommand<?> subCommand = subCommands.get(args[0]);

                        if (subCommand != null) {
                            args = Arrays.copyOfRange(args, 1, args.length);
                            List<String> tab = subCommand.tabComplete(sender, aliasUsed, args);

                            if (tab == null && sender instanceof Player) {
                                tab = subCommand.tabComplete((Player) sender, aliasUsed, args);
                            }

                            if (tab == null && sender instanceof ConsoleCommandSender) {
                                tab = subCommand.tabComplete((ConsoleCommandSender) sender, aliasUsed, args);
                            }

                            if (tab != null) {
                                return tab;
                            }
                        }
                    }

                    return super.tabComplete(sender, aliasUsed, args, location);
                }

                List<String> tab = null;

                if (sender instanceof Player) {
                    tab = command.tabComplete((Player) sender, aliasUsed, args);
                } else if (sender instanceof ConsoleCommandSender) {
                    tab = command.tabComplete((ConsoleCommandSender) sender, aliasUsed, args);
                }

                if (tab == null) {
                    tab = command.tabComplete(sender, aliasUsed, args);
                }

                return tab != null ? tab : super.tabComplete(sender, aliasUsed, args, location);
            }
        });
    }

    public List<String> findClosest(Collection<String> list, String startsWith) {
        List<String> closest = new ArrayList<>();

        if (startsWith == null || startsWith.isEmpty()) {
            closest.addAll(list);
        } else {
            for (String string : list) {
                if (string.toLowerCase().startsWith(startsWith.toLowerCase())) {
                    closest.add(string);
                }
            }
        }

        return closest;
    }

    public List<Command<?>> getCommands() {
        return commands;
    }
}