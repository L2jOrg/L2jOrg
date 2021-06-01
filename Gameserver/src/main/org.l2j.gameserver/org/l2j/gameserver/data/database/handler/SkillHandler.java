package org.l2j.gameserver.data.database.handler;

import org.l2j.commons.database.QueryDescriptor;
import org.l2j.commons.database.TypeHandler;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SkillHandler implements TypeHandler<Skill> {

    @Override
    public Skill defaultValue() {
        return null;
    }

    @Override
    public Skill handleResult(QueryDescriptor queryDescriptor) throws SQLException {
        var resultSet = queryDescriptor.getResultSet();
        if(resultSet.next()){
            return handleType(resultSet, Skill.class);
        }
        return defaultValue();
    }

    @Override
    public Skill handleType(ResultSet resultSet, Class<?> type) throws SQLException {
        var id  = resultSet.getInt("skill_id");
        var level = resultSet.getInt("skill_level");
        return SkillEngine.getInstance().getSkill(id, level);
    }

    @Override
    public Skill handleColumn(ResultSet resultSet, int column) throws SQLException {
        return handleType(resultSet, null);
    }

    @Override
    public void setParameter(PreparedStatement statement, int parameterIndex, Skill arg) throws SQLException {
        statement.setInt(parameterIndex, arg.getId());
        statement.setInt(++parameterIndex, arg.getLevel());
    }

    @Override
    public String type() {
        return Skill.class.getName();
    }
}
