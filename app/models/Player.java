package models;

import play.db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Player {

    private UUID uuid;
    private String name;
    private HashMap<String, Integer> stats;
    private EnumMap<Kit.KitName, Stats> kits;
    private HashMap<String, Stats> overall;
    private List<Kit.KitName> mostGames;
    private boolean loaded;
    private Integer score;

    public Player(UUID uuid) {
        this.uuid = uuid;
        loadName();
        loadPlayer();
    }

    public Player(String name) {
        this.name = name;
        loadUuid();
        loadPlayer();
    }

    void loadPlayer() {
        stats = new HashMap<>();
        kits = new EnumMap<>(Kit.KitName.class);
        overall = new HashMap<>();

        if (isLoaded()) {
            loadStats();

            for (Kit.KitName kit : Kit.KitName.values())
                kits.put(kit, new Stats());

            parseSndStats();
            calculateSndScore();
        }
    }

    public Map<String, Stats> getOverall() {
        return overall;
    }

    public Integer getScore() {
        return score;
    }

    public Map<Kit.KitName, Stats> getKits() {
        return kits;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public HashMap<String, Integer> getStats() {
        return stats;
    }

    public boolean isLoaded() {
        return loaded;
    }

    void calculateSndScore() {

        Stats snd = new Stats();

        Integer deaths = stats.get("Game.Search and Destroy.Deaths");
        Integer kills = stats.get("Game.Search and Destroy.Kills");
        Integer wins = stats.get("Game.Search and Destroy.Wins");
        Integer losses = stats.get("Game.Search and Destroy.Losses");

        snd.setStat(deaths, Stats.Type.DEATHS);
        snd.setStat(kills, Stats.Type.KILLS);
        snd.setStat(wins, Stats.Type.WINS);
        snd.setStat(losses, Stats.Type.LOSSES);

        if (stats.get("Game.Search and Destroy.Assists") != null)
            snd.setStat(stats.get("Game.Search and Destroy.Assists"), Stats.Type.ASSISTS);
        else
            snd.setStat(0, Stats.Type.ASSISTS);

        if (stats.get("Game.Search and Destroy.Armed") != null)
            snd.getUniqueIntegers().put("Armed", stats.get("Game.Search and Destroy.Armed"));
        else
            snd.getUniqueIntegers().put("Armed", 0);

        if (stats.get("Game.Search and Destroy.Defused") != null)
            snd.getUniqueIntegers().put("Defused", stats.get("Game.Search and Destroy.Defused"));
        else
            snd.getUniqueIntegers().put("Defused", 0);

        snd.setStat(stats.get("Game.Search and Destroy.Killstreak"), Stats.Type.KILLSTREAK);

        snd.calculate();

        overall.put("Search and Destroy", snd);

        if (deaths == null)
            deaths = 0;

        if (kills == null)
            kills = 0;

        if (wins == null)
            wins = 0;

        if (losses == null)
            losses = 0;

        Double doubleScore = ((((double) kills / deaths) + ((double) wins / losses * 10)) * 90
                + snd.getAssists() / 8
                + ((snd.getUniqueIntegers().get("Armed") + snd.getUniqueIntegers().get("Defused")) / 6));

        score = doubleScore.intValue();

    }

    void parseSndStats() {

        stats.entrySet().forEach(stat -> {

            for (Kit.KitName kit : Kit.KitName.values()) {

                if (kit.equals(Kit.KitName.MEDIC))
                    if (stat.getKey().toUpperCase().contains(kit.toString()) && stat.getKey().contains("Heals")) {
                        kits.get(Kit.KitName.MEDIC).getUniqueIntegers().put("Heals", stat.getValue());
                        continue;
                    }

                if (stat.getKey().toUpperCase().contains(kit.toString())) {
                    Stats.Type type = null;

                    if (stat.getKey().toLowerCase().contains("killstreak")) {
                        type = Stats.Type.KILLSTREAK;
                    } else if (stat.getKey().toLowerCase().contains("deaths")) {
                        type = Stats.Type.DEATHS;
                    } else if (stat.getKey().toLowerCase().contains("assists")) {
                        type = Stats.Type.ASSISTS;
                    } else if (stat.getKey().toLowerCase().contains("wins")) {
                        type = Stats.Type.WINS;
                    } else if (stat.getKey().toLowerCase().contains("losses")) {
                        type = Stats.Type.LOSSES;
                    } else if (stat.getKey().toLowerCase().contains("kills")) {
                        type = Stats.Type.KILLS;
                    }

                    if (type != null)
                        kits.get(kit).setStat(stat.getValue(), type);

                    break;
                }
            }

        });

        kits.values().forEach(Stats::calculate);

    }

    private void loadUuid() {

        Connection connection = DB.getConnection();

        PreparedStatement statement;
        ResultSet result;
        String query;

        try {

            query = "SELECT * FROM playerinfo WHERE info = ? AND type = 1";
            statement = connection.prepareStatement(query);

            statement.setString(1, getName());

            result = statement.executeQuery();

            if (result.next()) {
                loaded = true;
                uuid = UUID.fromString(result.getString("uuid"));
                name = result.getString("info");
            }

            if (connection != null)
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            if (statement != null)
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            if (result != null)
                try {
                    result.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void loadName() {

        Connection connection;
        PreparedStatement statement;
        ResultSet result;
        String query;

        try {

            connection = DB.getConnection();

            query = "SELECT * FROM playerinfo WHERE uuid = ? AND type = 1";
            statement = connection.prepareStatement(query);

            statement.setString(1, String.valueOf(getUuid()));

            result = statement.executeQuery();

            if (result.next()) {
                loaded = true;
                name = result.getString("info");
            }

            if (connection != null)
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            if (statement != null)
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            if (result != null)
                try {
                    result.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void loadStats() {

        Connection connection;
        PreparedStatement statement;
        ResultSet result;
        String query;

        try {

            connection = DB.getDataSource().getConnection();

            query = "SELECT * FROM stats INNER JOIN mappings ON stats.type=mappings.value WHERE uuid = ?";
            statement = connection.prepareStatement(query);

            statement.setString(1, String.valueOf(getUuid()));

            result = statement.executeQuery();

            while (result.next()) {
                stats.put(result.getString("name"), result.getInt("value"));
            }

            connection.close();
            statement.close();
            result.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
