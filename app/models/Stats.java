package models;

import java.util.HashMap;

public class Stats {

    private Integer kills, deaths, assists, wins, losses, totalGames, killstreak;
    private Double killDeathRatio, winLossRatio;
    private HashMap<String, Double> uniqueDoubles;
    private HashMap<String, Integer> uniqueIntegers;

    public Stats() {
        kills = 0;
        deaths = 0;
        assists = 0;
        wins = 0;
        losses = 0;
        killstreak = 0;

        uniqueDoubles = new HashMap<>();
        uniqueIntegers = new HashMap<>();

        calculate();
    }

    public HashMap<String, Double> getUniqueDoubles() {
        return uniqueDoubles;
    }

    public HashMap<String, Integer> getUniqueIntegers() {
        return uniqueIntegers;
    }

    public void setStat(Integer value, Type type) {

        switch (type) {

            case KILLS:
                kills = value;
                break;
            case DEATHS:
                deaths = value;
                break;
            case ASSISTS:
                assists = value;
                break;
            case WINS:
                wins = value;
                break;
            case LOSSES:
                losses = value;
                break;
            case KILLSTREAK:
                killstreak = value;
                break;

        }

    }

    public void calculate() {

        if (wins == null)
            wins = 0;

        if (losses == null)
            losses = 0;

        if (kills == null)
            kills = 0;

        if (deaths == null)
            deaths = 0;

        totalGames = wins + losses;

        if (kills == 0 || deaths == 0)
            killDeathRatio = 0.0;
        else
            killDeathRatio = Math.round((double) kills / deaths * 100.0) / 100.0;

        if (wins == 0 || losses == 0)
            winLossRatio = 0.0;
        else
            winLossRatio = Math.round((double) wins / losses * 100.0) / 100.0;
    }

    public Integer getAssists() {
        return assists;
    }

    public Integer getKills() {
        return kills;
    }

    public Integer getDeaths() {
        return deaths;
    }

    public Integer getWins() {
        return wins;
    }

    public Integer getLosses() {
        return losses;
    }

    public Integer getTotalGames() {
        return totalGames;
    }

    public Integer getKillstreak() {
        return killstreak;
    }

    public Double getKillDeathRatio() {
        return killDeathRatio;
    }

    public Double getWinLossRatio() {
        return winLossRatio;
    }

    public enum Type {

        KILLS, DEATHS, ASSISTS, WINS, LOSSES, KILLSTREAK

    }

}
