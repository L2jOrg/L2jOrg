package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.ProvidedDAO;
import org.l2j.gameserver.instancemanager.GlobalVariablesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author JoeAlisson
 */
public class GlobalVariablesDAO extends ProvidedDAO<GlobalVariablesManager> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalVariablesDAO.class);
    private static final String INSERT_QUERY = "INSERT INTO global_variables (var, value) VALUES (?, ?)";
    private static final String SELECT_QUERY = "SELECT * FROM global_variables";
    private static final String TRUNCATE_QUERY = "TRUNCATE global_variables";

    @Override
    public boolean save(GlobalVariablesManager model) {
        try {
            executeInBatch(INSERT_QUERY, statement -> addVariables(model, statement));
        } catch (SQLException e) {
            LOGGER.warn("Couldn't save global variables to database.", e);
        }
        return true;
    }

    private void addVariables(GlobalVariablesManager model, PreparedStatement statement)  {
        try {
            for (var entry : model.getSet().entrySet()) {
                statement.setString(1, entry.getKey());
                statement.setString(2, String.valueOf(entry.getValue()));
                statement.addBatch();
            }
        } catch (SQLException e) {
            LOGGER.error("Couldn't save global variables to database.", e);
        }
    }

    public Map<String, Object> findAll() {
        Map<String, Object> result = new HashMap<>();
        try {
            query(SELECT_QUERY, resultSet -> processQuery(resultSet, result));
        } catch (SQLException e) {
            LOGGER.error("Couldn't restore global variables");
        }
        return result;

    }

    private void processQuery(ResultSet resultSet, Map<String, Object> result) {
        try {
            while (resultSet.next()) {
                result.put(resultSet.getString("var"), resultSet.getString("value"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }


    public boolean deleteAll() {
        try {
            execute(TRUNCATE_QUERY);
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }
        return true;
    }
}
