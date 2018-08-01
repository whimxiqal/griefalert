package com.minecraftonline.griefalert;

public class GriefAction {
    // Version 13 : 07/11 11h25 GMT+2
    // for servermod123-124+

    public String blockName;
    public char alertColor;
    public boolean denied;
    public boolean stealth;
    public int onlyin;

    GriefAction(String name, char color, boolean deny, boolean silent) {
        this.blockName = name;
        this.alertColor = color;
        this.denied = deny;
        this.stealth = silent;
        this.onlyin = 0;
    }

    GriefAction(String name, char color, boolean deny, boolean silent, int onlyin) {
        this.blockName = name;
        this.alertColor = color;
        this.denied = deny;
        this.stealth = silent;
        this.onlyin = onlyin;
    }

}
