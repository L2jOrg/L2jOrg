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
package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.util.GameUtils;

public class Warehouse extends Folk {
    public Warehouse(NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2WarehouseInstance);
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        if (GameUtils.isMonster(attacker)) {
            return true;
        }

        return super.isAutoAttackable(attacker);
    }

    @Override
    public boolean isWarehouse() {
        return true;
    }

    @Override
    public String getHtmlPath(int npcId, int val) {
        String pom = "";

        if (val == 0) {
            pom = Integer.toString(npcId);
        } else {
            pom = npcId + "-" + val;
        }

        return "data/html/warehouse/" + pom + ".htm";
    }
}
