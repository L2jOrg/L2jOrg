package org.l2j.commons.database.helpers;

import java.sql.PreparedStatement;

public class NoParameterStrategy implements MapParameterStrategy {

    @Override
    public void setParameters(PreparedStatement statement, Object[] args) {
        // there is no parameters
    }
}
