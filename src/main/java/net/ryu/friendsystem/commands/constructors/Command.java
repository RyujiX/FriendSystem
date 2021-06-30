package net.ryu.friendsystem.commands.constructors;

import com.google.common.base.Joiner;
import net.ryu.friendsystem.FriendSystemPlugin;
import net.ryu.friendsystem.utils.Txt;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Base for all commands.
 */
public abstract class Command<P extends FriendSystemPlugin> {
    protected final P plugin;

    private final String[] aliases;
    private final String description;

    private String usage;
    private byte requiredLength = 0;

    private List<Predicate<Player>> canExecute;

    private final Txt text = new Txt();

    private Map<String, SubCommand<?>> subCommands;
    private SubCommand<?> defaultSubCommand;

    private org.bukkit.command.Command bukkitCommand;

    public Command(P plugin, String description, String... aliases) {
        this.plugin = plugin;
        this.aliases = aliases;
        this.description = description;
    }

    /**
     * This will only be called if the command has no sub commands
     *
     * @param sender The CommandSender, either Player or Console
     * @param aliasUsed The alias used to execute this command
     * @param args Arguments proceeding the label
     */
    public void execute(CommandSender sender, String aliasUsed, String... args) {
    }

    /**
     * This will only be called if the command has no sub commands AND the sender is a Player
     *
     * @param sender    The Player sender
     * @param aliasUsed The alias used to execute this command
     * @param args      Arguments proceeding the label
     */
    public void execute(Player sender, String aliasUsed, String... args) {
    }

    /**
     * This will only be called if the command has no sub commands AND the sender is Console
     *
     * @param sender    The Console sender
     * @param aliasUsed The alias used to execute this command
     * @param args      Arguments proceeding the label
     */
    public void execute(ConsoleCommandSender sender, String aliasUsed, String... args) {
    }
    
    public List<String> tabComplete(CommandSender sender, String aliasUsed, String... args) {
        return null;
    }

    public List<String> tabComplete(Player sender, String aliasUsed, String... args) {
        return null;
    }

    public List<String> tabComplete(ConsoleCommandSender sender, String aliasUsed, String... args) {
        return null;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
        this.requiredLength = (byte) StringUtils.countMatches(usage, "<");
    }

    public boolean attemptExecute(Player player, String aliasUsed, String... args) {
        if (!canExecute(player)) {
            return false;
        }

        execute(player, aliasUsed, args);

        return true;
    }

    public boolean canExecute(Player player) {
        if (canExecute == null || canExecute.isEmpty()) {
            return true;
        }
        for (Predicate<Player> canExecute : canExecute) {
            if (!canExecute.test(player)) {
                return false;
            }
        }
        return true;
    }

    public byte getRequiredLength() {
        return requiredLength;
    }

    public void setUsage(String... usage) {
        setUsage(Joiner.on(' ').join(usage));
    }

    public boolean hasUsage() {
        return getUsage() != null && !getUsage().isEmpty();
    }

    public P getPlugin() {
        return plugin;
    }
    
    public String getName() {
        return aliases[0];
    }

    public String[] getAliases() {
        return aliases;
    }

    public String getDescription() {
        return description;
    }

    public Txt getText() {
        return text;
    }

    public void setBukkitCommand(org.bukkit.command.Command bukkitCommand) {
        this.bukkitCommand = bukkitCommand;
    }

    public void usage(CommandSender sender, String aliasUsed) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"Invalid usage! Correct use: /" + aliasUsed.toLowerCase() + " " + ChatColor.stripColor(getUsage())));
    }

    public boolean parseInt(CommandSender sender, String number) {
        try {
            Integer.parseInt(number);
        } catch (NumberFormatException exception) {
            return false;
        }
        return true;
    }

    public boolean parseDouble(CommandSender sender, String decimal) {
        try {
            Double.parseDouble(decimal);
        } catch (NumberFormatException exception) {
            return false;
        }
        return true;
    }

    protected void runAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    protected void runSync(Runnable runnable) {
        Bukkit.getScheduler().runTask(plugin, runnable);
    }

    public List<Predicate<Player>> getCanExecute() {
        return canExecute;
    }

    public SubCommand<?> getDefaultSubCommand() {
        return defaultSubCommand;
    }

    public Map<String, SubCommand<?>> getSubCommands() {
        return subCommands;
    }

    public void addSubCommand(SubCommand<?> subCommand) {
        if (subCommands == null) {
            subCommands = new HashMap<>();
        }

        for (String name : subCommand.getNames()) {
            subCommands.put(name.toLowerCase(), subCommand);
        }
    }

    public void setDefaultSubCommand(SubCommand<?> subCommand) {
        Objects.requireNonNull(subCommands, "Command doesn't have any sub commands yet");
        Objects.requireNonNull(subCommand, "Command must have a default sub command");
        defaultSubCommand = subCommand;
        setUsage(subCommand.getUsage());
    }
}