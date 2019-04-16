package org.l2j.commons.database.helpers;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface MapParameterStrategy {

    void setParameters(PreparedStatement statement, Object[] args) throws SQLException;
}
