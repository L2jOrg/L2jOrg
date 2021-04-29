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
package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.settings.CharacterSettings;

public class Warehouse extends Folk {

    public Warehouse(NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2WarehouseInstance);
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        return false;
    }

    @Override
    public boolean isWarehouse() {
        return true;
    }

    @Override
    public String getHtmlPath(int npcId, int val) {
        String fileName;

        if (val == 0) {
            fileName = Integer.toString(npcId);
        } else {
            fileName = npcId + "-" + val;
        }

        return "data/html/warehouse/" + fileName + ".htm";
    }

    @Override
    public void showChatWindow(Player player, int val) {
        if(player.getReputation() < 0 && !CharacterSettings.canPkUseWareHouse() && showPkDenyChatWindow(player, "warehouse")) {
            return;
        }
        super.showChatWindow(player, val);
    }
}
