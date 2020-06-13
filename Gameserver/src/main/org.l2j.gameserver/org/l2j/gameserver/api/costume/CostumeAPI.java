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
package org.l2j.gameserver.api.costume;

import org.l2j.gameserver.engine.costume.Costume;
import org.l2j.gameserver.engine.costume.CostumeEngine;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.costume.ExCostumeUseItem;
import org.l2j.gameserver.network.serverpackets.costume.ExSendCostumeList;

import java.util.EnumSet;

import static java.util.Objects.isNull;

/**
 * @author JoeAlisson
 */
public class CostumeAPI {

    public static void imprintCostumeOnPlayer(Player player, int costumeId) {
        imprintCostume(player, CostumeEngine.getInstance().getCostume(costumeId));
    }

    private static void imprintCostume(Player player, Costume costume) {
        if(isNull(costume)) {
            return;
        }

        var playerCostume = player.addCostume(costume.id());
        if(playerCostume.getAmount() == 1) {
            player.addSkill(costume.skill(), true);
            CostumeEngine.getInstance().checkCostumeCollection(player, costume.id());
        }

        player.sendPacket(new ExCostumeUseItem(costume.id(), true));
        player.sendPacket(new ExSendCostumeList(playerCostume));
    }

    public static void imprintRandomCostumeOnPlayer(Player player, EnumSet<CostumeGrade> grades) {
        imprintCostume(player, CostumeEngine.getInstance().getRandomCostume(grades));
    }
}
