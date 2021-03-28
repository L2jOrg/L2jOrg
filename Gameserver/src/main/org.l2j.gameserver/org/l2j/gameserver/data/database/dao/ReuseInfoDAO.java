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

import io.github.joealisson.primitive.HashLongSet;
import io.github.joealisson.primitive.LongMap;
import io.github.joealisson.primitive.LongSet;
import org.l2j.commons.database.ProvidedDAO;
import org.l2j.gameserver.model.TimeStamp;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author JoeAlisson
 */
public class ReuseInfoDAO extends ProvidedDAO<BuffInfo> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReuseInfoDAO.class);

    private static final String ADD_SKILL_SAVE = "INSERT INTO character_skills_save (charId,skill_id,skill_level,skill_sub_level,remaining_time,reuse_delay,systime,restore_type,buff_index) VALUES (?,?,?,?,?,?,?,?,?)";
    private static final String ADD_ITEM_REUSE_SAVE = "INSERT INTO character_item_reuse_save (charId,itemId,itemObjId,reuseDelay,systime) VALUES (?,?,?,?,?)";

    @Override
    public boolean save(BuffInfo model) {
        return false;
    }

    public void saveBuffInfoReuse(Player player, Collection<BuffInfo> models)  {
        try {
            executeInBatch(ADD_SKILL_SAVE, statement -> addBuffInfoParam(player, models, statement));
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void addBuffInfoParam(Player player, Collection<BuffInfo> models, PreparedStatement statement){
        final LongSet storedSkills = new HashLongSet();
        var currentTime = System.currentTimeMillis();
        int bufferIndex = addBuffInfo(player, models, statement, storedSkills, currentTime);
        addSkillReuse(player, statement, storedSkills, currentTime, bufferIndex);
    }

    private void addSkillReuse(Player player, PreparedStatement statement, LongSet storedSkills, long currentTime, int bufferIndex) {
        for (LongMap.Entry<TimeStamp> entry : player.getSkillReuseTimeStamps().entrySet()) {
            if(storedSkills.contains(entry.getKey())) {
                continue;
            }

            var timeStamp = entry.getValue();
            if(currentTime < timeStamp.getStamp()) {
                try {
                    storedSkills.add(entry.getKey());

                    statement.setInt(1, player.getObjectId());
                    statement.setInt(2, timeStamp.getSkillId());
                    statement.setInt(3, timeStamp.getSkillLvl());
                    statement.setInt(4, timeStamp.getSkillSubLvl());
                    statement.setInt(5, -1);
                    statement.setLong(6, timeStamp.getReuse());
                    statement.setDouble(7, timeStamp.getStamp());
                    statement.setInt(8, 1); // Restore type 1, skill reuse.
                    statement.setInt(9, ++bufferIndex);
                    statement.addBatch();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    private int addBuffInfo(Player player, Collection<BuffInfo> models, PreparedStatement statement, LongSet storedSkills, long currentTime) {
        int buffIndex = 0;
        for (BuffInfo info : models) {
            var skill = info.getSkill();

            if(!info.canBeSaved() || storedSkills.contains(skill.getReuseHashCode())) {
                continue;
            }

            storedSkills.add(skill.getReuseHashCode());

            try {

                statement.setInt(1, player.getObjectId());
                statement.setInt(2, skill.getId());
                statement.setInt(3, skill.getLevel());
                statement.setInt(4, skill.getSubLevel());
                statement.setInt(5, info.getTime());

                final TimeStamp t = player.getSkillReuseTimeStamp(skill.getReuseHashCode());
                statement.setLong(6, (t != null) && (currentTime < t.getStamp()) ? t.getReuse() : 0);
                statement.setDouble(7, (t != null) && (currentTime < t.getStamp()) ? t.getStamp() : 0);

                statement.setInt(8, 0); // Store type 0, active buffs/debuffs.
                statement.setInt(9, ++buffIndex);
                statement.addBatch();
            } catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return buffIndex;
    }

    public void saveItemReuse(int playerId, Collection<TimeStamp> models)  {
        try {
            executeInBatch(ADD_ITEM_REUSE_SAVE, statement -> addTimeStampParam(playerId, models, statement));
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void addTimeStampParam(int playerId, Collection<TimeStamp> models, PreparedStatement statement) {
        final long currentTime = System.currentTimeMillis();
        for (TimeStamp ts : models) {
            if (currentTime < ts.getStamp()) {
                try {
                    statement.setInt(1, playerId);
                    statement.setInt(2, ts.getItemId());
                    statement.setInt(3, ts.getItemObjectId());
                    statement.setLong(4, ts.getReuse());
                    statement.setDouble(5, ts.getStamp());
                    statement.addBatch();
                } catch (SQLException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

}
