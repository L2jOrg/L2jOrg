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
