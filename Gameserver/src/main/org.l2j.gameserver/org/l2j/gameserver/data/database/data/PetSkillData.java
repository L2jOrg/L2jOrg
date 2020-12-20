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
package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

/**
 * @author JoeAlisson
 */
@Table("pet_skills_save")
public class PetSkillData {

    @Column("item_id")
    private int itemId;

    @Column("skill_id")
    private int skillId;

    @Column("skill_level")
    private int skillLevel;

    @Column("remaining_time")
    private int remainingTime;

    @Column("buff_index")
    private int buffIndex;

    public int getSkillId() {
        return skillId;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public static PetSkillData of(int controlObjectId, int skillId, int skillLevel, int time, int index) {
        var data = new PetSkillData();
        data.itemId = controlObjectId;
        data.skillId = skillId;
        data.skillLevel = skillLevel;
        data.remainingTime = time;
        data.buffIndex = index;
        return data;
    }
}
