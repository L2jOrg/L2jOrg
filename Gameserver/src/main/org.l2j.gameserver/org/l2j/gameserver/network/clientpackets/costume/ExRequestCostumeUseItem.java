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
package org.l2j.gameserver.network.clientpackets.costume;

import org.l2j.gameserver.engine.costume.CostumeEngine;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public class ExRequestCostumeUseItem extends ClientPacket {

    private int itemObjectId;

    @Override
    protected void readImpl() throws Exception {
        itemObjectId = readInt();
    }

    @Override
    protected void runImpl() {
        var player = client.getPlayer();
        var item = player.getInventory().getItemByObjectId(itemObjectId);
        if(nonNull(item) && CostumeEngine.getInstance().checkCostumeAction(player)) {
            item.forEachSkill(ItemSkillType.NORMAL, skill -> SkillCaster.triggerCast(player, player, skill.getSkill(), item, true));
        }
    }
}
