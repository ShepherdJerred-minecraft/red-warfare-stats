package models;

import controllers.Leaderboards;
import org.apache.commons.lang3.text.WordUtils;
import play.Logger;
import play.db.DB;
import play.mvc.WebSocket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Mode {

    private HashMap<String, Integer> mappings = new HashMap<>();
    private List<Player> players;
    private ModeName mode;
    private Stats.Type type;
    private Leaderboards.Sort sort;
    private Integer page;

    public Mode(ModeName mode) {
        this.mode = mode;
        type = Stats.Type.WINS;
        sort = Leaderboards.Sort.DESC;
        page = 1;
        this.players = new ArrayList<>();
        loadMappings();
        loadStats();
    }

    public Mode(ModeName mode, Stats.Type type) {
        this.mode = mode;
        this.type = type;
        sort = Leaderboards.Sort.DESC;
        page = 1;
        this.players = new ArrayList<>();
        loadMappings();
        loadStats();
    }

    public Mode(ModeName mode, Stats.Type type, Leaderboards.Sort sort) {
        this.mode = mode;
        this.type = type;
        this.sort = sort;
        page = 1;
        this.players = new ArrayList<>();
        loadMappings();
        loadStats();
    }

    public ModeName getMode() {
        return mode;
    }

    public Leaderboards.Sort getSort() {
        return sort;
    }

    public Integer getPage() {
        return page;
    }

    public Mode(ModeName mode, Stats.Type type, Leaderboards.Sort sort, Integer page) {
        this.mode = mode;
        this.type = type;
        this.sort = sort;
        this.page = page;

        this.players = new ArrayList<>();
        loadMappings();
        loadStats();
    }

    public Stats.Type getType() {
        return type;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    private void loadMappings() {

        Connection connection;
        PreparedStatement statement;
        ResultSet result;
        String query;

        try {

            connection = DB.getDataSource().getConnection();

            query = "SELECT * FROM mappings WHERE name LIKE '%" + mode.toString().replace("_", " ") + "%'";
            statement = connection.prepareStatement(query);

            result = statement.executeQuery();

            String key;

            if (mode == ModeName.SEARCH_AND_DESTROY)
                key = "Search and Destroy";
            else if (mode == ModeName.SURVIVAL_GAMES)
                key = "Survival Games";
            else
                return;

            while (result.next()) {
                mappings.put(result.getString("name").replace("Game.", "").replace(key + ".", "").toUpperCase(), result.getInt("value"));
            }

            connection.close();
            statement.close();
            result.close();

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

            query = "SELECT * FROM stats WHERE type = '" + mappings.get(type.toString()) + "' ORDER BY stats.value " + sort.toString() + " LIMIT 20 OFFSET " + (page * 20 - 20);
            statement = connection.prepareStatement(query);

            Logger.info(type.toString());
            Logger.info(query);

            result = statement.executeQuery();

            while (result.next()) {
                Player player = new Player(UUID.fromString(result.getString("uuid")));
                getPlayers().add(player);
            }

            connection.close();
            statement.close();
            result.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public enum ModeName {

        SEARCH_AND_DESTROY, SURVIVAL_GAMES

    }

}
