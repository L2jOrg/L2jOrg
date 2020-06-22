/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
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

    public static final String MONSTER_ARENA_VARIABLE = "MA_C";

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

    public void resetRaidBonus() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement st = con.createStatement()) {
            st.execute("DELETE FROM global_variables WHERE var like 'MA_C%'");
        } catch (SQLException t) {
            LOGGER.warn(t.getMessage(), t);
        }
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