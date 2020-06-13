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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.ClanInfo;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.AllianceInfo;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * This class ...
 *
 * @version $Revision: 1479 $ $Date: 2005-11-09 00:47:42 +0100 (mer., 09 nov. 2005) $
 */
public final class RequestAllyInfo extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        SystemMessage sm;
        final int allianceId = activeChar.getAllyId();
        if (allianceId > 0) {
            final AllianceInfo ai = new AllianceInfo(allianceId);
            client.sendPacket(ai);

            // send for player
            sm = SystemMessage.getSystemMessage(SystemMessageId.ALLIANCE_INFORMATION);
            client.sendPacket(sm);

            sm = SystemMessage.getSystemMessage(SystemMessageId.ALLIANCE_NAME_S1);
            sm.addString(ai.getName());
            client.sendPacket(sm);

            sm = SystemMessage.getSystemMessage(SystemMessageId.ALLIANCE_LEADER_S2_OF_S1);
            sm.addString(ai.getLeaderC());
            sm.addString(ai.getLeaderP());
            client.sendPacket(sm);

            sm = SystemMessage.getSystemMessage(SystemMessageId.CONNECTION_S1_TOTAL_S2);
            sm.addInt(ai.getOnline());
            sm.addInt(ai.getTotal());
            client.sendPacket(sm);

            sm = SystemMessage.getSystemMessage(SystemMessageId.AFFILIATED_CLANS_TOTAL_S1_CLAN_S);
            sm.addInt(ai.getAllies().length);
            client.sendPacket(sm);

            sm = SystemMessage.getSystemMessage(SystemMessageId.CLAN_INFORMATION);
            for (ClanInfo aci : ai.getAllies()) {
                client.sendPacket(sm);

                sm = SystemMessage.getSystemMessage(SystemMessageId.CLAN_NAME_S1);
                sm.addString(aci.getClan().getName());
                client.sendPacket(sm);

                sm = SystemMessage.getSystemMessage(SystemMessageId.CLAN_LEADER_S1);
                sm.addString(aci.getClan().getLeaderName());
                client.sendPacket(sm);

                sm = SystemMessage.getSystemMessage(SystemMessageId.CLAN_LEVEL_S1);
                sm.addInt(aci.getClan().getLevel());
                client.sendPacket(sm);

                sm = SystemMessage.getSystemMessage(SystemMessageId.CONNECTION_S1_TOTAL_S2);
                sm.addInt(aci.getOnline());
                sm.addInt(aci.getTotal());
                client.sendPacket(sm);

                sm = SystemMessage.getSystemMessage(SystemMessageId.SEPARATOR_DASHES);
            }

            sm = SystemMessage.getSystemMessage(SystemMessageId.SEPARATOR_EQUALS);
            client.sendPacket(sm);
        } else {
            client.sendPacket(SystemMessageId.YOU_ARE_NOT_CURRENTLY_ALLIED_WITH_ANY_CLANS);
        }
    }
}
