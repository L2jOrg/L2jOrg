/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.ProvidedDAO;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author JoeAlisson
 */
public class SkillsDAO extends ProvidedDAO<Skill> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SkillsDAO.class);

    private static final String ADD_NEW_SKILLS = "REPLACE INTO character_skills (charId,skill_id,skill_level) VALUES (?,?,?)";

    @Override
    public boolean save(Skill model) {
        return false;
    }

    public void save(int playerId, Collection<Skill> skills) {
        try {
            executeInBatch(ADD_NEW_SKILLS, statement -> addSkillParam(playerId, skills, statement));
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void addSkillParam(int playerId, Collection<Skill> skills, PreparedStatement statement) {
        for (var skill : skills) {
            try {
                statement.setInt(1, playerId);
                statement.setInt(2, skill.getId());
                statement.setInt(3, skill.getLevel());
                statement.addBatch();
            } catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
