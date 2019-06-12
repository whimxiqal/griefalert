package com.minecraftonline.griefalert.tools;

import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

public class General {

	public static TextColor charToColor(char c) throws IllegalArgumentException {
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
        	throw new IllegalArgumentException();
        }
	}
	
	
}
