package com.minecraftonline.griefalert.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.core.GriefInstance;

import javax.annotation.Nonnull;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.spongepowered.api.text.format.TextColors.RED;
import static org.spongepowered.api.text.format.TextColors.YELLOW;

/**
 * The CommandExecutor for the command to check alerts for GriefInstances in game
 */
public final class GriefCheckCommand implements CommandExecutor {
	
	/** The main plugin object. */
    private final GriefAlert plugin;

    /**
     * Basic constructor.
     * @param plugin The main plugin object.
     */
    public GriefCheckCommand(GriefAlert plugin) {
        this.plugin = plugin;
    }


    @Override
    @Nonnull
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {  // Check if a non-player is running the command
        	throw new CommandException(Text.of("Only in game players can use this command!"));
        }
        
        Optional<Integer> arg = args.getOne("code");
        if (!arg.isPresent()) {
        	// Missing the code argument
            throw new CommandException(Text.builder("Syntax: /gcheck <id>").color(RED).build());
        }
        
        int code = arg.get();
        Player checker = (Player) src;
        GriefInstance instance;
        
        try {
        	instance = plugin.getRealtimeGriefInstanceManager().get(arg.get());
        } catch (IndexOutOfBoundsException e) {
        	// Code is not within the valid range
        	throw new CommandException(Text.builder("Grief Alert ID " + code + " is out of range.").color(RED).build());
        }
        
        if (instance == null) {
        	// No Grief Instance at that array location
            throw new CommandException(Text.builder("There is no current alert at ID " + code + ".").color(RED).build());
        }

        plugin.getRealtimeGriefInstanceManager().printToStaff(formatPlayerName(checker).toBuilder().append(
                Text.builder(" is checking ").color(TextColors.YELLOW).build()).append(
                Text.builder(Integer.toString(code)).color(TextColors.WHITE).build()).append(
                Text.builder(" for grief.").color(TextColors.YELLOW).build()).build());
        GriefInstance grief = plugin.getRealtimeGriefInstanceManager().get(code);
        
        // Teleport checker to a safe location near the grief. If failed, notify the checker
        if (!checker.setLocationSafely(grief.getGrieferSnapshot().getLocation().get())) {
        	checker.sendMessage(Text.builder("No safe location was found for teleportation.").color(YELLOW).build());
        }
        
        try {
        	checker.setRotation(grief.getGrieferSnapshot().getTransform().get().getRotation());
        } catch (NoSuchElementException e) {
        	plugin.getLogger().warn("When checking for grief, player " + checker.getName() + " did not find the transform within"
        			+ "the snapshot of the griefer.");
        }
        return CommandResult.success();
    }

    /**
     * Format the grief checker's name to include prefix and suffix
     * @param player The grief checker
     * @return The Text form of the grief checker's name
     */
    private Text formatPlayerName(Player player) {
        return TextSerializers.FORMATTING_CODE.deserialize(player.getOption("prefix").orElse("") + player.getName() + player.getOption("suffix").orElse(""));
    }
}