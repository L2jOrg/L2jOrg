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
import org.l2j.gameserver.data.database.data.MacroCmdData;
import org.l2j.gameserver.data.database.data.MacroData;
import org.l2j.gameserver.model.Macro;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author JoeAlisson
 */
public class MacroHandler implements TypeHandler<Macro> {

    @Override
    public Macro defaultValue() {
        return null;
    }

    @Override
    public Macro handleResult(QueryDescriptor queryDescriptor) throws SQLException {
        var resultSet = queryDescriptor.getResultSet();
        if(resultSet.next()){
            return handleType(resultSet, Macro.class);
        }
        return defaultValue();
    }

    @Override
    public Macro handleType(ResultSet resultSet, Class<?> type) throws SQLException {
        var macroHandler = HandlersSupport.handlerFromClass(MacroData.class);
        var macroData = macroHandler.handleType(resultSet, MacroData.class);
        var macroId = macroData.getId();

        var macroCmdHandler = HandlersSupport.handlerFromClass(MacroCmdData.class);
        List<MacroCmdData> commands = new ArrayList<>();
        var macroCmd = macroCmdHandler.handleType(resultSet, MacroCmdData.class);
        commands.add(macroCmd);

        while (resultSet.next()) {
            macroCmd = macroCmdHandler.handleType(resultSet, MacroCmdData.class);
            if(macroCmd.getMacroId() != macroId){
                resultSet.previous();
                break;
            }
            commands.add(macroCmd);
        }
        return new Macro(macroData, commands);
    }

    @Override
    public Macro handleColumn(ResultSet resultSet, int column) throws SQLException {
        return null;
    }

    @Override
    public void setParameter(PreparedStatement statement, int parameterIndex, Macro macro) throws SQLException {
        statement.setInt(parameterIndex, macro.getId());
    }

    @Override
    public String type() {
        return Macro.class.getName();
    }
}
