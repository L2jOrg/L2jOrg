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

import org.l2j.commons.database.annotation.Table;

/**
 * @author JoeAlisson
 */
@Table("character_summons")
public class SummonData {
    private int ownerId;
    private int summonId;
    private int summonSkillId;
    private int curHp;
    private int curMp;
    private int time;

    public int getOwnerId() {
        return ownerId;
    }

    public int getSummonId() {
        return summonId;
    }

    public int getSummonSkillId() {
        return summonSkillId;
    }

    public int getCurHp() {
        return curHp;
    }

    public int getCurMp() {
        return curMp;
    }

    public int getTime() {
        return time;
    }
}
