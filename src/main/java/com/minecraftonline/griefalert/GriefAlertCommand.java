package com.minecraftonline.griefalert;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import javax.annotation.Nonnull;
import java.util.Optional;

import static org.spongepowered.api.text.format.TextColors.RED;
import static org.spongepowered.api.text.format.TextColors.WHITE;
//TODO: PietElite: Fix
public final class GriefAlertCommand implements CommandExecutor {
    private final GriefAlert plugin;

    public GriefAlertCommand(GriefAlert plugin) {
        this.plugin = plugin;
    }

    @Override
    @Nonnull
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) throws CommandException {
        if (src instanceof Player) {  // Check if a player is running the command
            Optional<Integer> arg = args.getOne("code");
            if (!arg.isPresent()) { // Missing player name
                throw new CommandException(Text.builder("ERROR: ").color(RED).append(Text.builder("Missing check number").color(WHITE).build()).build());
            }
            int code = arg.get();
            if (code > plugin.getConfigInt("alertsCodeLimit") || code < 1) {
                throw new CommandException(Text.builder("GriefAlert ERROR: ").color(RED).append(Text.builder("Check number out of range").color(WHITE).build()).build());
            }
            Player checker = (Player) src;
            GriefInstance instance = plugin.getTracker().get(arg.get());
            if (instance == null) {
                throw new CommandException(Text.builder("GriefAlert ERROR: ").color(RED).append(Text.builder("There is no current alert at that code").color(WHITE).build()).build());
            }

            plugin.getTracker().printToStaff(formatPlayerName(checker).toBuilder().append(
                    Text.builder(" is checking ").color(TextColors.YELLOW).build()).append(
                    Text.builder(Integer.toString(code)).color(TextColors.WHITE).build()).append(
                    Text.builder(" for grief.").color(TextColors.YELLOW).build()).build());
            GriefInstance grief = plugin.getTracker().get(code);
            checker.setLocationSafely(grief.getGriefer().getLocation().get());
            checker.setRotation(grief.getRotation());
            return CommandResult.success();
        }
        throw new CommandException(Text.of("Only in game players can use this command!"));
    }

    private Text formatPlayerName(Player player) {
        return TextSerializers.FORMATTING_CODE.deserialize(player.getOption("prefix").orElse("") + player.getName() + player.getOption("suffix").orElse(""));
    }
}