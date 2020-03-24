package org.l2j.commons.database.helpers;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author JoeAlisson
 */
public interface MapParameterStrategy {

    void setParameters(PreparedStatement statement, Object[] args) throws SQLException;

    void setParameters(PreparedStatement statement, Object obj) throws SQLException;
}
