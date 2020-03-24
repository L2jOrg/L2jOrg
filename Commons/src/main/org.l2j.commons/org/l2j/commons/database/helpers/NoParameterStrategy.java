package org.l2j.commons.database.helpers;

import java.sql.PreparedStatement;

/**
 * @author JoeAlisson
 */
public class NoParameterStrategy implements MapParameterStrategy {

    @Override
    public void setParameters(PreparedStatement statement, Object[] args) {
        // there is no parameters
    }

    @Override
    public void setParameters(PreparedStatement statement, Object obj) {
        // no params
    }
}
