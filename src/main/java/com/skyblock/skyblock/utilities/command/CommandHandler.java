package com.skyblock.skyblock.utilities.command;

import com.skyblock.skyblock.Skyblock;
import lombok.Getter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CommandHandler implements CommandExecutor {

    private final Skyblock plugin;

    private final HashMap<String, Runnable> commandMessages;

    @Getter
    private final List<Command> commands;

    private CommandSender sender = null;
    private String[] args = null;

    public CommandHandler(Skyblock plugin, Command... commands) {
        this.plugin = plugin;

        this.commandMessages = new HashMap<>();
        this.commands = Arrays.asList(commands);

        this.commandMessages.put("only-players", () -> this.sender.sendMessage(this.plugin.getPrefix() + "Only players can execute this command."));
        this.commandMessages.put("base-command", () -> {
            boolean hasPermission = this.commands.stream().anyMatch(command -> command.permission().getName().equalsIgnoreCase("skyblock.*") || sender.hasPermission(command.permission()));

            String message = this.plugin.getPrefix() + "Running " + "Skyblock v" + this.plugin.getVersion() + ".\n"
                    + " > "
                    + (
                    hasPermission ?
                            "Type /skyblock help for more information." :
                            "You do not have permission to use any commands."
            );

            this.sender.sendMessage(message);
        });
        this.commandMessages.put("no-permission", () -> this.sender.sendMessage(this.plugin.getPrefix() + "You do not have permission to execute this command."));
        this.commandMessages.put("not-found", () -> this.sender.sendMessage(this.plugin.getPrefix() + "Command not found."));
    }

    private boolean executeCommandMessage(String message) {
        this.commandMessages.get(message).run();

        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        this.sender = sender;
        this.args = args;

        if (args.length == 0) return executeCommandMessage("base-command");

        for (Command command : this.commands) {
            boolean requiresPlayer = command.requiresPlayer();
            Permission permission = command.permission();
            String name = command.name();

            if (!this.args[0].equalsIgnoreCase(name)) continue;
            if (!this.sender.hasPermission(permission) && !command.permission().getName().equalsIgnoreCase("skyblock.*"))
                return executeCommandMessage("no-permission");

            String[] arguments = new String[args.length - 1];
            System.arraycopy(args, 1, arguments, 0, arguments.length);

            if (requiresPlayer) {
                if (!(this.sender instanceof Player)) return this.executeCommandMessage("only-players");

                command.execute((Player) this.sender, arguments, this.plugin);
            } else {
                command.execute(this.sender, arguments, this.plugin);
            }

            return true;
        }

        return this.executeCommandMessage("not-found");
    }
}