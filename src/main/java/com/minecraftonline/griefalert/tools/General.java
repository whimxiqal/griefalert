package com.minecraftonline.griefalert.tools;

import com.minecraftonline.griefalert.GriefAlert;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

/**
 * A General tools class to house static methods for small data manipulations
 */
public abstract class General {
	
	/**
	 * Return the TextColor Minecraft associated with the input character.
	 * @param c the input character to convert to TextColor
	 * @return The desired TextColor
	 * @throws IllegalColorCodeException Throws a subclass of an IllegalArgumentException 
	 * if there was an invalid input character
	 */
	public static TextColor charToColor(char c) throws IllegalColorCodeException {
        switch (Character.toUpperCase(c)) {
        case '0':
            return TextColors.BLACK;
        case '1':
            return TextColors.DARK_BLUE;
        case '2':
            return TextColors.DARK_GREEN;
        case '3':
            return TextColors.DARK_AQUA;
        case '4':
            return TextColors.DARK_RED;
        case '5':
            return TextColors.DARK_PURPLE;
        case '6':
            return TextColors.GOLD;
        case '7':
            return TextColors.GRAY;
        case '8':
            return TextColors.DARK_GRAY;
        case '9':
            return TextColors.BLUE;
        case 'A':
            return TextColors.GREEN;
        case 'B':
            return TextColors.AQUA;
        case 'C':
            return TextColors.RED;
        case 'D':
            return TextColors.LIGHT_PURPLE;
        case 'E':
            return TextColors.YELLOW;
        case 'F':
            return TextColors.WHITE;
        default:
        	throw new IllegalColorCodeException(c);
        }
	}

  /**
   * Return the TextColor Minecraft associated with the input character.
   * @param s the input string to convert to TextColor
   * @return The desired TextColor
   * @throws IllegalColorCodeException Throws a subclass of an IllegalArgumentException
   * if there was an invalid input character
   */
  public static TextColor stringToColor(String s) throws IllegalColorCodeException {
    switch (s.toLowerCase()) {
      case "black":
        return TextColors.BLACK;
      case "dark_blue":
        return TextColors.DARK_BLUE;
      case "dark_green":
        return TextColors.DARK_GREEN;
      case "dark_aqua":
        return TextColors.DARK_AQUA;
      case "dark_red":
        return TextColors.DARK_RED;
      case "dark_purple":
        return TextColors.DARK_PURPLE;
      case "gold":
        return TextColors.GOLD;
      case "gray":
        return TextColors.GRAY;
      case "dark_gray":
        return TextColors.DARK_GRAY;
      case "blue":
        return TextColors.BLUE;
      case "green":
        return TextColors.GREEN;
      case "aqua":
        return TextColors.AQUA;
      case "red":
        return TextColors.RED;
      case "light_purple":
        return TextColors.LIGHT_PURPLE;
      case "yellow":
        return TextColors.YELLOW;
      case "white":
        return TextColors.WHITE;
      default:
        throw new IllegalColorCodeException(s);
    }
  }

  public static void stampConsole() {
    Sponge.getServer().getConsole().sendMessage(Text.of(
        "\n",
        TextColors.DARK_PURPLE, "     ____          " + "\n",
        TextColors.DARK_PURPLE, "   //         //\\     ", TextColors.GOLD, "GriefAlert ", TextColors.DARK_GRAY, "v." + GriefAlert.VERSION + "\n",
        TextColors.DARK_PURPLE, "  ||    ===  //__\\    ", TextColors.DARK_AQUA, "Built for MinecraftOnline" + "\n",
        TextColors.DARK_PURPLE, "   \\\\____/  //    \\ " + "\n"
    ));
  }

  /**
	 * An exception to throw if a method tries to convert an invalid character
	 * to a Minecraft text color.
	 */
	@SuppressWarnings("serial")
	public static class IllegalColorCodeException extends IllegalArgumentException {
		IllegalColorCodeException(char c) {
			super("This color character is invalid: " + c);
		}
    IllegalColorCodeException(String s) {
      super("This color name is invalid: " + s);
    }
	}
	
	/** 
	 * Converts all indefinite articles (<b>an, a</b>) to the appropriate version
	 * in a string by reading whether the next word begins with a vowel.
	 * @param string The string to read and correct
	 * @return The corrected string
	 */
    public static String correctIndefiniteArticles(String string) {
    	String[] tokens = string.replaceAll(" an ", " a ").split(" a ");
    	String output = tokens[0];
    	for (int i = 1; i < tokens.length; i++) {
    		if ("aeiou".contains(String.valueOf(tokens[i].charAt(0)).toLowerCase())) {
    			output = String.join(" an ", output, tokens[i]);
    		} else {
    			output = String.join(" a ", output, tokens[i]);
    		}
    	}
        return output;
    }
    
    /**
     * Format the grief checker's name to include prefix and suffix
     * @param player The grief checker
     * @return The Text form of the grief checker's name
     */
    public static Text formatPlayerName(Player player) {
        return TextSerializers.FORMATTING_CODE.deserialize(player.getOption("prefix").orElse("") + player.getName() + player.getOption("suffix").orElse(""));
    }
	
}
