/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.instancemanager;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.model.variables.AbstractVariables;

import java.sql.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Global Variables Manager.
 *
 * @author xban1x
 */
public final class GlobalVariablesManager extends AbstractVariables {
    // Public variable names
    public static final String COC_TOP_MARKS = "COC_TOP_MARKS";
    public static final String COC_TOP_MEMBER = "COC_TOP_MEMBER";
    public static final String COC_TRUE_HERO = "COC_TRUE_HERO";
    public static final String COC_TRUE_HERO_REWARDED = "COC_TRUE_HERO_REWARDED";
    private static final Logger LOGGER = Logger.getLogger(GlobalVariablesManager.class.getName());
    // SQL Queries.
    private static final String SELECT_QUERY = "SELECT * FROM global_variables";
    private static final String DELETE_QUERY = "DELETE FROM global_variables";
    private static final String INSERT_QUERY = "INSERT INTO global_variables (var, value) VALUES (?, ?)";

    protected GlobalVariablesManager() {
        restoreMe();
    }

    /**
     * Gets the single instance of {@code GlobalVariablesManager}.
     *
     * @return single instance of {@code GlobalVariablesManager}
     */
    public static GlobalVariablesManager getInstance() {
        return SingletonHolder._instance;
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
            LOGGER.warning(getClass().getSimpleName() + ": Couldn't restore global variables");
            return false;
        } finally {
            compareAndSetChanges(true, false);
        }
        LOGGER.info(getClass().getSimpleName() + ": Loaded " + getSet().size() + " variables.");
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
            LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Couldn't save global variables to database.", e);
            return false;
        } finally {
            compareAndSetChanges(true, false);
        }
        LOGGER.info(getClass().getSimpleName() + ": Stored " + getSet().size() + " variables.");
        return true;
    }

    @Override
    public boolean deleteMe() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement del = con.createStatement()) {
            del.execute(DELETE_QUERY);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Couldn't delete global variables to database.", e);
            return false;
        }
        return true;
    }

    private static class SingletonHolder {
        protected static final GlobalVariablesManager _instance = new GlobalVariablesManager();
    }
}