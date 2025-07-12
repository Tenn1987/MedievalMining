package com.brandon.globaleconomy.nations.politics;

public class PoliticalNPC {
    private final int npcId; // Citizens NPC ID
    private final String name;
    private final PoliticalRole role;
    private final String nationName; // Link to the Nation by name

    public PoliticalNPC(int npcId, String name, PoliticalRole role, String nationName) {
        this.npcId = npcId;
        this.name = name;
        this.role = role;
        this.nationName = nationName;
    }

    public int getNpcId() {
        return npcId;
    }

    public String getName() {
        return name;
    }

    public PoliticalRole getRole() {
        return role;
    }

    public String getNationName() {
        return nationName;
    }

    public boolean isLeader() {
        return role == PoliticalRole.KING || role == PoliticalRole.QUEEN;
    }
}