package org.l2j.gameserver.model.variables;

import org.l2j.commons.database.DatabaseFactory;
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
public class ClanVariables extends AbstractVariables {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClanVariables.class);

    // SQL Queries.
    private static final String SELECT_QUERY = "SELECT * FROM clan_variables WHERE clanId = ?";
    private static final String DELETE_QUERY = "DELETE FROM clan_variables WHERE clanId = ?";
    private static final String INSERT_QUERY = "INSERT INTO clan_variables (clanId, var, val) VALUES (?, ?, ?)";

    private final int _objectId;

    public ClanVariables(int objectId) {
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
                    set(rset.getString("var"), rset.getString("val"), false);
                }
            }
        } catch (SQLException e) {
            LOGGER.warn(getClass().getSimpleName() + ": Couldn't restore variables for: " + _objectId, e);
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
            LOGGER.warn(getClass().getSimpleName() + ": Couldn't update variables for: " + _objectId, e);
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
            LOGGER.warn(getClass().getSimpleName() + ": Couldn't delete variables for: " + _objectId, e);
            return false;
        }
        return true;
    }
}
