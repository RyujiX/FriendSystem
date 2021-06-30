package net.ryu.friendsystem.commands.constructors;

import net.ryu.friendsystem.FriendSystemPlugin;
import net.ryu.friendsystem.utils.Txt;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Used to separate functionality in commands.
 */
public class SubCommand<C extends Command<?>> {

    protected final FriendSystemPlugin plugin;
    protected final C command;

    private final String[] names;

    private final String usage;
    private final String description;
    private final String permission;

    private byte requiredLength;

    private final Txt text = new Txt();

    public SubCommand(C command, String permission, String usage, String description, String... names) {
        Validate.notEmpty(names, "Sub command must have at least 1 name");

        this.plugin = command.getPlugin();
        this.command = command;
        this.names = names;
        this.usage = usage == null ? names[0] : usage;
        this.description = description;
        this.permission = permission;

        requiredLength = (byte) StringUtils.countMatches(usage, "<");

        command.addSubCommand(this);
    }

    /**
     * @param sender    Command sender
     * @param aliasUsed The alias of the sub command used. Can be null if this is a default command.
     * @param args      Arguments proceeding the sub command (does not include the sub command alias)
     */
    public void execute(CommandSender sender, String aliasUsed, String... args) {
    }

    public void execute(Player sender, String aliasUsed, String... args) {
    }

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

    public String[] getNames() {
        return names;
    }

    public String getUsage() {
        return usage;
    }

    public FriendSystemPlugin getPlugin() { return command.getPlugin(); }

    public boolean parseInt(String number) {
        try {
            Integer.parseInt(number);
        } catch (NumberFormatException exception) {
            return false;
        }
        return true;
    }

    public boolean parseDouble(String decimal) {
        try {
            Double.parseDouble(decimal);
        } catch (NumberFormatException exception) {
            return false;
        }
        return true;
    }

    public Txt getText() {
        return text;
    }
    public String getDescription() {
        return description;
    }

    public String getPermission() {
        return permission;
    }

    public byte getRequiredLength() {
        return requiredLength;
    }
}