package org.l2j.gameserver.instancemanager;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.model.variables.AbstractVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Map.Entry;

/**
 * Global Variables Manager.
 *
 * @author xban1x
 */
public final class GlobalVariablesManager extends AbstractVariables {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalVariablesManager.class);
    // Public variable names
    public static final String COC_TOP_MARKS = "COC_TOP_MARKS";
    public static final String COC_TOP_MEMBER = "COC_TOP_MEMBER";
    public static final String COC_TRUE_HERO = "COC_TRUE_HERO";
    public static final String COC_TRUE_HERO_REWARDED = "COC_TRUE_HERO_REWARDED";
    // SQL Queries.
    private static final String SELECT_QUERY = "SELECT * FROM global_variables";
    private static final String DELETE_QUERY = "DELETE FROM global_variables";
    private static final String INSERT_QUERY = "INSERT INTO global_variables (var, value) VALUES (?, ?)";

    private GlobalVariablesManager() {
    }

    @Override
    public boolean restoreMe() {
        // Restore previous variables.
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement st = con.createStatement();
             ResultSet rset = st.executeQuery(SELECT_QUERY)) {
            while (rset.next()) {
                set(rset.getString("var"), rset.getString("value"));
            }
        } catch (SQLException e) {
            LOGGER.warn("Couldn't restore global variables");
            return false;
        } finally {
            compareAndSetChanges(true, false);
        }
        LOGGER.info("Loaded {} variables", getSet().size());
        return true;
    }

    @Override
    public boolean storeMe() {
        // No changes, nothing to store.
        if (!hasChanges()) {
            return false;
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement del = con.createStatement();
             PreparedStatement st = con.prepareStatement(INSERT_QUERY)) {
            // Clear previous entries.
            del.execute(DELETE_QUERY);

            // Insert all variables.
            for (Entry<String, Object> entry : getSet().entrySet()) {
                st.setString(1, entry.getKey());
                st.setString(2, String.valueOf(entry.getValue()));
                st.addBatch();
            }
            st.executeBatch();
        } catch (SQLException e) {
            LOGGER.warn("Couldn't save global variables to database.", e);
            return false;
        } finally {
            compareAndSetChanges(true, false);
        }
        LOGGER.info("Stored {} variables", getSet().size());
        return true;
    }

    @Override
    public boolean deleteMe() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement del = con.createStatement()) {
            del.execute(DELETE_QUERY);
        } catch (Exception e) {
            LOGGER.warn(getClass().getSimpleName() + ": Couldn't delete global variables to database.", e);
            return false;
        }
        return true;
    }

    public static void init() {
        getInstance().restoreMe();
    }

    public static GlobalVariablesManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final GlobalVariablesManager INSTANCE = new GlobalVariablesManager();
    }
}