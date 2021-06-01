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
package org.l2j.gameserver.network.clientpackets.teleport;

import org.l2j.gameserver.data.xml.impl.TeleportEngine;
import org.l2j.gameserver.data.xml.model.TeleportData;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.actor.request.TeleportRequest;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.util.GameUtils;

import static org.l2j.gameserver.network.SystemMessageId.YOU_CANNOT_TELEPORT_TO_A_VILLAGE_THAT_IS_IN_A_SIEGE;
import static org.l2j.gameserver.network.SystemMessageId.YOU_CAN_T_TELEPORT_DURING_AN_OLYMPIAD_MATCH;

/**
 * @author JoeAlisson
 */
public class ExRequestTeleport extends ClientPacket {
    private int id;

    @Override
    protected void readImpl() throws Exception {
        id = readInt();
    }

    @Override
    protected void runImpl()  {
        TeleportEngine.getInstance().getInfo(id).ifPresent(this::teleport);
    }

    private void teleport(TeleportData info) {
        var player = client.getPlayer();

        if(info.getCastleId() != -1 && CastleManager.getInstance().getCastleById(info.getCastleId()).isInSiege()) {
            player.sendPacket(YOU_CANNOT_TELEPORT_TO_A_VILLAGE_THAT_IS_IN_A_SIEGE);
            return;
        }

        if(player.isInOlympiadMode()) {
            player.sendPacket(YOU_CAN_T_TELEPORT_DURING_AN_OLYMPIAD_MATCH);
            return;
        }

        if(GameUtils.canTeleport(player) && (player.getLevel() <= CharacterSettings.maxFreeTeleportLevel() || player.reduceAdena("Teleport", info.getPrice(), null, true))) {
            player.addRequest(new TeleportRequest(player, id));
            player.useSkill(CommonSkill.TELEPORT.getSkill(), null, false, true);
        }
    }
}