/* Created by PietElite */

package com.minecraftonline.griefalert.api.commands;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

public class CommandKey {

  private final Text key;
  private final Text description;

  private CommandKey(Text key, Text description) {
    this.key = key;
    this.description = description;
  }

  public static CommandKey of(Text key, Text description) {
    return new CommandKey(key, description);
  }

  public Text get() {
    return Text.builder().append(Text.of(key)).onHover(TextActions.showText(description)).build();
  }

}
