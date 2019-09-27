package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.griefevents.profiles.GriefProfile;
import com.minecraftonline.griefalert.griefevents.profiles.GriefProfileSpecialtyBehavior;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

public class GriefAlertSaveprofileCommand extends AbstractCommand {


  public GriefAlertSaveprofileCommand(GriefAlert plugin) {
    super(plugin, GriefAlert.Permission.GRIEFALERT_COMMAND_ADDPROFILE, Text.of("Save an alert profile which has been selected with the 'addprofile' tool"));
    addAlias("saveprofile");
    addAlias("save");
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    src.sendMessage(Text.of(TextColors.GREEN, "Double success!"));
    return CommandResult.success();
  }

  public static class GriefProfileBuilder {
    /**
     * String representation of the griefed object.
     */
    private String griefedId = "";
    /**
     * TextColor representation of the color in which an alert will appear.
     */
    private TextColor alertColor = TextColors.RED;
    /**
     * Denotes whether this action will be cancelled upon triggering.
     */
    private boolean denied = false;
    /**
     * Denotes whether this is muted from alerting.
     */
    private boolean stealthy = false;
    /**
     * The type of grief.
     */
    private GriefAlert.GriefType type = GriefAlert.GriefType.DESTROY;

    private GriefProfileSpecialtyBehavior specialtyBehavior = GriefProfileSpecialtyBehavior.NONE;

    /**
     * The wrapper for information regarding whether a dimension is marked for this grief event.
     */
    private GriefProfile.DimensionParameterArray dimensionParameterArray = new GriefProfile.DimensionParameterArray();

    public void setAlertColor(TextColor alertColor) {
      this.alertColor = alertColor;
    }

    public void setDenied(boolean denied) {
      this.denied = denied;
    }

    public GriefProfile.DimensionParameterArray getDimensionParameterArray() {
      return this.dimensionParameterArray;
    }

    public void setGriefedId(String griefedId) {
      this.griefedId = griefedId;
    }

    public void setStealthy(boolean stealthy) {
      this.stealthy = stealthy;
    }

    public void setType(GriefAlert.GriefType type) {
      this.type = type;
    }
  }
}
