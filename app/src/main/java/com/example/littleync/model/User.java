package com.example.littleync.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.lang.Math;

public class User {
    private String databaseID = "random";

    private String userName;
    private int woodchoppingGearLevel;
    private int fishingGearLevel;
    private int combatGearLevel;
    private int aggregateLevel;
    private int wood;
    private int fish;
    private int gold;
    private ArrayList<String> trades;
    private int exp;

    public User(String userName, int woodchoppingGearLevel, int fishingGearLevel,
                int combatGearLevel, int aggregateLevel, int wood, int fish, int gold,
                ArrayList<String> trades, int exp) {
        this.userName = userName;
        this.woodchoppingGearLevel = woodchoppingGearLevel;
        this.fishingGearLevel = fishingGearLevel;
        this.combatGearLevel = combatGearLevel;
        this.aggregateLevel = aggregateLevel;
        this.wood = wood;
        this.fish = fish;
        this.gold = gold;
        this.trades = trades;
        this.exp = exp;
    }

    // Needed to automatically parse DB
    public User() {}

    // Formula for level: 50 * level ^ 1.8
    private int requiredExperience(int level) {
        return (int)(50 * Math.pow(level, 1.8));
    }

    private int computeAggregateLevelIndex() {
        int index = getAggregateLevel();
        int exp = getExp();
        while (index < 200 && exp >= requiredExperience(index)) {
            index++;
        }
        return index;
    }

    private Boolean checkNextLevel() {
        return getExp() >= requiredExperience(getAggregateLevel());
    }

    public void addGold(int gold) {
        setGold(getGold() + gold);
        addExp(gold);
        if (checkNextLevel()) {
            setAggregateLevel(computeAggregateLevelIndex());
        }
    }

    public void addWood() {
        setWood(getWood() + getWoodchoppingGearLevel());
        addExp(getWoodchoppingGearLevel());
        if (checkNextLevel()) {
            setAggregateLevel(computeAggregateLevelIndex());
        }
    }

    public void addFish() {
        setFish(getFish() + getFishingGearLevel());
        addExp(getFishingGearLevel());
        if (checkNextLevel()) {
            setAggregateLevel(computeAggregateLevelIndex());
        }
    }

    public void addTrade(String tradeID) {
        trades.add(tradeID);
    }

    public void addExp(int exp) {
        setExp(getExp() + exp);
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setDatabaseID(String databaseID) {
        this.databaseID = databaseID;
    }

    public void setWoodchoppingGearLevel(int woodchoppingGearLevel) {
        this.woodchoppingGearLevel = woodchoppingGearLevel;
    }

    public void setFishingGearLevel(int fishingGearLevel) {
        this.fishingGearLevel = fishingGearLevel;
    }

    public void setCombatGearLevel(int combatGearLevel) {
        this.combatGearLevel = combatGearLevel;
    }

    public void setAggregateLevel(int aggregateLevel) {
        this.aggregateLevel = aggregateLevel;
    }

    public void setWood(int wood) {
        this.wood = wood;
    }

    public void setFish(int fish) {
        this.fish = fish;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public void setTrades(ArrayList<String> trades) {
        this.trades = trades;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public String getUserName() {
        return this.userName;
    }

    public int getWoodchoppingGearLevel() {
        return this.woodchoppingGearLevel;
    }

    public int getFishingGearLevel() {
        return this.fishingGearLevel;
    }

    public int getCombatGearLevel() {
        return this.combatGearLevel;
    }

    public int getAggregateLevel() {
        return this.aggregateLevel;
    }

    public int getWood() {
        return this.wood;
    }

    public int getFish() {
        return this.fish;
    }

    public int getGold() {
        return this.gold;
    }

    public ArrayList<String> getTrades() {
        return this.trades;
    }

    public int getExp() {
        return this.exp;
    }

    public void writeToDatabase(OnlineDatabase db) {
        Map<String, Object> docData = new HashMap<>();
        docData.put("userName", getUserName());
        docData.put("woodchoppingGearLevel", getWoodchoppingGearLevel());
        docData.put("fishingGearLevel", getFishingGearLevel());
        docData.put("combatGearLevel", getCombatGearLevel());
        docData.put("aggregateLevel", getAggregateLevel());
        docData.put("wood", getWood());
        docData.put("fish", getFish());
        docData.put("gold", getGold());
        docData.put("trades", getTrades());
        docData.put("exp", getExp());
        db.userReadWrite().set(docData);
    }

}
