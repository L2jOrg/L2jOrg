package org.l2j.gameserver.model.variables;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map.Entry;

/**
 * @author UnAfraid
 */
public class PlayerVariables extends AbstractVariables {
    // Public variable names
    public static final String HAIR_ACCESSORY_VARIABLE_NAME = "HAIR_ACCESSORY_ENABLED";
    public static final String WORLD_CHAT_VARIABLE_NAME = "WORLD_CHAT_USED";
    public static final String VITALITY_ITEMS_USED_VARIABLE_NAME = "VITALITY_ITEMS_USED";
    public static final String CEREMONY_OF_CHAOS_PROHIBITED_PENALTIES = "CEREMONY_OF_CHAOS_PENALTIES";
    public static final String CEREMONY_OF_CHAOS_MARKS = "CEREMONY_OF_CHAOS_MARKS";
    public static final String ABILITY_POINTS_MAIN_CLASS = "ABILITY_POINTS";
    public static final String ABILITY_POINTS_DUAL_CLASS = "ABILITY_POINTS_DUAL_CLASS";
    public static final String ABILITY_POINTS_USED_MAIN_CLASS = "ABILITY_POINTS_USED";
    public static final String ABILITY_POINTS_USED_DUAL_CLASS = "ABILITY_POINTS_DUAL_CLASS_USED";
    public static final String REVELATION_SKILL_1_MAIN_CLASS = "RevelationSkill1";
    public static final String REVELATION_SKILL_2_MAIN_CLASS = "RevelationSkill2";
    public static final String REVELATION_SKILL_1_DUAL_CLASS = "DualclassRevelationSkill1";
    public static final String REVELATION_SKILL_2_DUAL_CLASS = "DualclassRevelationSkill2";
    public static final String EXTEND_DROP = "EXTEND_DROP";
    public static final String FORTUNE_TELLING_VARIABLE = "FortuneTelling";
    public static final String FORTUNE_TELLING_BLACK_CAT_VARIABLE = "FortuneTellingBlackCat";
    public static final String HUNTING_ZONE_RESET_TIME = "HUNTING_ZONE_RESET_TIME_";

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerVariables.class.getName());
    // SQL Queries.
    private static final String SELECT_QUERY = "SELECT * FROM character_variables WHERE charId = ?";
    private static final String DELETE_QUERY = "DELETE FROM character_variables WHERE charId = ?";
    private static final String INSERT_QUERY = "INSERT INTO character_variables (charId, var, val) VALUES (?, ?, ?)";
    private final int _objectId;

    public PlayerVariables(int objectId) {
        _objectId = objectId;
        restoreMe();
    }

    @Override
    public boolean restoreMe() {
        // Restore previous variables.
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement st = con.prepareStatement(SELECT_QUERY)) {
            st.setInt(1, _objectId);
            try (ResultSet rset = st.executeQuery()) {
                while (rset.next()) {
                    set(rset.getString("var"), rset.getString("val"));
                }
            }
        } catch (SQLException e) {
            LOGGER.warn("Couldn't restore variables for: " + getPlayer(), e);
            return false;
        } finally {
            compareAndSetChanges(true, false);
        }
        return true;
    }

    @Override
    public boolean storeMe() {
        // No changes, nothing to store.
        if (!hasChanges()) {
            return false;
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            // Clear previous entries.
            try (PreparedStatement st = con.prepareStatement(DELETE_QUERY)) {
                st.setInt(1, _objectId);
                st.execute();
            }

            // Insert all variables.
            try (PreparedStatement st = con.prepareStatement(INSERT_QUERY)) {
                st.setInt(1, _objectId);
                for (Entry<String, Object> entry : getSet().entrySet()) {
                    st.setString(2, entry.getKey());
                    st.setString(3, String.valueOf(entry.getValue()));
                    st.addBatch();
                }
                st.executeBatch();
            }
        } catch (SQLException e) {
            LOGGER.warn("Couldn't update variables for: " + getPlayer(), e);
            return false;
        } finally {
            compareAndSetChanges(true, false);
        }
        return true;
    }

    @Override
    public boolean deleteMe() {
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            // Clear previous entries.
            try (PreparedStatement st = con.prepareStatement(DELETE_QUERY)) {
                st.setInt(1, _objectId);
                st.execute();
            }

            // Clear all entries
            getSet().clear();
        } catch (Exception e) {
            LOGGER.warn("Couldn't delete variables for: " + getPlayer(), e);
            return false;
        }
        return true;
    }

    public Player getPlayer() {
        return World.getInstance().findPlayer(_objectId);
    }

    public void updateExtendDrop(int id, long count) {
        StringBuilder result = new StringBuilder();
        final String data = getString(EXTEND_DROP, "");
        if (data.isEmpty()) {
            result = new StringBuilder(id + "," + count);
        } else if (data.contains(";")) {
            for (String s : data.split(";")) {
                final String[] drop = s.split(",");
                if (drop[0].equals(Integer.toString(id))) {
                    continue;
                }

                result.append(";").append(s);
            }
            result = new StringBuilder(result.substring(1));
        } else {
            result = new StringBuilder(id + "," + count);
        }
        set(EXTEND_DROP, result.toString());
    }

    public long getExtendDropCount(int id) {
        final String data = getString(EXTEND_DROP, "");
        for (String s : data.split(";")) {
            final String[] drop = s.split(",");
            if (drop[0].equals(Integer.toString(id))) {
                return Long.parseLong(drop[1]);
            }
        }
        return 0;
    }
}
