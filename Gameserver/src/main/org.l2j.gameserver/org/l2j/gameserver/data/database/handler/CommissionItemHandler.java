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
package org.l2j.gameserver.data.database.handler;

import org.l2j.commons.database.HandlersSupport;
import org.l2j.commons.database.QueryDescriptor;
import org.l2j.commons.database.TypeHandler;
import org.l2j.gameserver.data.database.data.CommissionItemData;
import org.l2j.gameserver.data.database.data.ItemData;
import org.l2j.gameserver.model.commission.CommissionItem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author JoeAlisson
 */
public class CommissionItemHandler implements TypeHandler<CommissionItem> {

    @Override
    public CommissionItem defaultValue() {
        return null;
    }

    @Override
    public CommissionItem handleResult(QueryDescriptor queryDescriptor) throws SQLException {
        var resultSet = queryDescriptor.getResultSet();
        if(resultSet.next()){
            return handleType(resultSet, CommissionItem.class);
        }
        return defaultValue();
    }

    @Override
    public CommissionItem handleType(ResultSet resultSet, Class<?> type) throws SQLException {
        var itemHandler = HandlersSupport.handlerFromClass(ItemData.class);
        ItemData itemData = itemHandler.handleType(resultSet, ItemData.class);
        var commissionHandler = HandlersSupport.handlerFromClass(CommissionItemData.class);
        CommissionItemData data =  commissionHandler.handleType(resultSet, CommissionItemData.class);
        return new CommissionItem(data, itemData);
    }

    @Override
    public CommissionItem handleColumn(ResultSet resultSet, int column) throws SQLException {
        return null;
    }

    @Override
    public void setParameter(PreparedStatement statement, int parameterIndex, CommissionItem arg) throws SQLException {
        statement.setLong(parameterIndex, arg.getCommissionId());
    }

    @Override
    public String type() {
        return CommissionItem.class.getName();
    }
}
