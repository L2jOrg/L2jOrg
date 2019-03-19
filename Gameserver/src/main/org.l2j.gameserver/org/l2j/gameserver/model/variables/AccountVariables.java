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
public class AccountVariables extends AbstractVariables {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountVariables.class);

    // SQL Queries.
    private static final String SELECT_QUERY = "SELECT * FROM account_gsdata WHERE account_name = ?";
    private static final String DELETE_QUERY = "DELETE FROM account_gsdata WHERE account_name = ?";
    private static final String INSERT_QUERY = "INSERT INTO account_gsdata (account_name, var, value) VALUES (?, ?, ?)";

    private final String _accountName;

    public AccountVariables(String accountName) {
        _accountName = accountName;
        restoreMe();
    }

    @Override
    public boolean restoreMe() {
        // Restore previous variables.
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement st = con.prepareStatement(SELECT_QUERY)) {
            st.setString(1, _accountName);
            try (ResultSet rset = st.executeQuery()) {
                while (rset.next()) {
                    set(rset.getString("var"), rset.getString("value"));
                }
            }
        } catch (SQLException e) {
            LOGGER.warn(getClass().getSimpleName() + ": Couldn't restore variables for: " + _accountName, e);
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
                st.setString(1, _accountName);
                st.execute();
            }

            // Insert all variables.
            try (PreparedStatement st = con.prepareStatement(INSERT_QUERY)) {
                st.setString(1, _accountName);
                for (Entry<String, Object> entry : getSet().entrySet()) {
                    st.setString(2, entry.getKey());
                    st.setString(3, String.valueOf(entry.getValue()));
                    st.addBatch();
                }
                st.executeBatch();
            }
        } catch (SQLException e) {
            LOGGER.warn(getClass().getSimpleName() + ": Couldn't update variables for: " + _accountName, e);
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
                st.setString(1, _accountName);
                st.execute();
            }

            // Clear all entries
            getSet().clear();
        } catch (Exception e) {
            LOGGER.warn(getClass().getSimpleName() + ": Couldn't delete variables for: " + _accountName, e);
            return false;
        }
        return true;
    }
}
