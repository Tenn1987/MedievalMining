package com.brandon.globaleconomy.npcnation.politics;

public enum PoliticalRole {
    KING("King"),
    QUEEN("Queen"),
    DIPLOMAT("Diplomat");

    private final String displayName;

    PoliticalRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isRoyalty() {
        return this == KING || this == QUEEN;
    }
}
