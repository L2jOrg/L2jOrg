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
package org.l2j.commons.database.handler;

import org.l2j.commons.database.helpers.QueryDescriptor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author JoeAlisson
 */
public class VoidHandler implements TypeHandler<Void> {

    @Override
    public Void defaultValue() {
        return null;
    }

    @Override
    public Void handleResult(QueryDescriptor queryDescriptor) {
        return null;
    }

    @Override
    public Void handleType(ResultSet resultSet, Class<?> type) {
        return null;
    }

    @Override
    public Void handleColumn(ResultSet resultSet, int column) {
        return null;
    }

    @Override
    public void setParameter(PreparedStatement statement, int parameterIndex, Void arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String type() {
        return Void.TYPE.getName();
    }
}
