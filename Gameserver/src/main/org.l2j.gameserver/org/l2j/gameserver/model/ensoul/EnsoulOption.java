/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.model.ensoul;

import org.l2j.gameserver.model.holders.SkillHolder;

/**
 * @author UnAfraid
 */
public class EnsoulOption extends SkillHolder {
    private final int _id;
    private final String _name;
    private final String _desc;

    public EnsoulOption(int id, String name, String desc, int skillId, int skillLevel) {
        super(skillId, skillLevel);
        _id = id;
        _name = name;
        _desc = desc;
    }

    public int getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public String getDesc() {
        return _desc;
    }

    @Override
    public String toString() {
        return "Ensoul Id: " + _id + " Name: " + _name + " Desc: " + _desc;
    }
}
