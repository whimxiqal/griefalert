package com.minecraftonline.griefalert;

import com.minecraftonline.griefalert.tools.General;

import org.spongepowered.api.text.format.TextColor;

public class GriefAction {

    protected final String blockName;
    protected final TextColor alertColor;
    protected final boolean denied;
    protected final boolean stealthyAlert;
    protected final GriefType type;

    public enum GriefType {
        DEGRIEFED,
        DESTROYED,
        INTERACTED {
            @Override
            public String toString() {
                return "interacted with";
            }
        },
        USED;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    public GriefAction(String name, TextColor color, boolean deny, boolean silent, GriefType type) {
        this.blockName = name;
        this.alertColor = color;
        this.denied = deny;
        this.stealthyAlert = silent;
        this.type = type;
    }
    
    public GriefAction(String name, char color, boolean deny, boolean silent, GriefType type) {
    	this(name, General.charToColor(color), deny, silent, type);
    	
    }
    
    public String getBlockName() {
    	return blockName;
    }

    public TextColor getAlertColor() {
        return alertColor;
    }

    public boolean isDenied() {
        return denied;
    }

    public boolean isStealthyAlert() {
        return stealthyAlert;
    }

    public GriefType getType() {
        return type;
    }

}
