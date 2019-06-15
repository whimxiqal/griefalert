package com.minecraftonline.griefalert.commands;

import static org.spongepowered.api.text.format.TextColors.RED;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.core.GriefInstance;

public class GriefRecentCommand implements CommandExecutor {

	/** The main plugin object. */
    private final GriefAlert plugin;

    /**
     * Basic constructor.
     * @param plugin The main plugin object.
     */
    public GriefRecentCommand(GriefAlert plugin) {
        this.plugin = plugin;
    }


    @Override
    @Nonnull
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {  // Check if a non-player is running the command
        	throw new CommandException(Text.of("Only in game players can use this command!"));
        }
        Player sender = (Player) src;
        Optional<String> username = args.getOne("username");
        if (!username.isPresent()) {
        	// Missing the code argument
            throw new CommandException(Text.builder("Syntax: /grecent <username>").color(RED).build());
        }
        
        sender.sendMessage(Text.builder("Showing all recent grief alerts from player " + username.get()).color(RED).build());
        for (Pair<Integer,GriefInstance> griefInstance : plugin.getRealtimeGriefInstanceManager().getRecentGriefInstances()) {
        	if (username.get().equals(griefInstance.getValue().getGrieferAsPlayer().getName()))
        		sender.sendMessage(plugin.getRealtimeGriefInstanceManager().generateAlertMessage(griefInstance.getKey(), griefInstance.getValue()));
        }
        
        return CommandResult.success();
    }

}
