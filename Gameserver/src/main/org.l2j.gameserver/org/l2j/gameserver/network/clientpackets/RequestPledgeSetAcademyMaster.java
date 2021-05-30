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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.database.data.ClanMember;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * Format: (ch) dSS
 *
 * @author -Wooden-
 */
public final class RequestPledgeSetAcademyMaster extends ClientPacket {
    private String _currPlayerName;
    private int _set; // 1 set, 0 delete
    private String _targetPlayerName;

    @Override
    public void readImpl() {
        _set = readInt();
        _currPlayerName = readString();
        _targetPlayerName = readString();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        final Clan clan = activeChar.getClan();
        if (clan == null) {
            return;
        }

        if (!activeChar.hasClanPrivilege(ClanPrivilege.CL_APPRENTICE)) {
            activeChar.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_RIGHT_TO_DISMISS_AN_APPRENTICE);
            return;
        }

        final ClanMember currentMember = clan.getClanMember(_currPlayerName);
        final ClanMember targetMember = clan.getClanMember(_targetPlayerName);
        if ((currentMember == null) || (targetMember == null)) {
            return;
        }

        final var apprentice = targetMember.getPlayerInstance();
        final var sponsor = currentMember.getPlayerInstance();

        SystemMessage sm;
        if (_set == 0) {
            // test: do we get the current sponsor & apprentice from this packet or no?
            if (apprentice != null) {
                apprentice.setSponsor(0);
            } else {
                targetMember.setApprenticeAndSponsor(0, 0);
            }

            if (sponsor != null) {
                sponsor.setApprentice(0);
            } else {
                currentMember.setApprenticeAndSponsor(0, 0);
            }

            targetMember.saveApprenticeAndSponsor(0, 0);
            currentMember.saveApprenticeAndSponsor(0, 0);

            sm = SystemMessage.getSystemMessage(SystemMessageId.S2_CLAN_MEMBER_C1_S_APPRENTICE_HAS_BEEN_REMOVED);
        } else {
            if ((targetMember.getSponsor() != 0) || (currentMember.getApprentice() != 0) || (targetMember.getApprentice() != 0) || (currentMember.getSponsor() != 0)) {
                // TODO retail message
                activeChar.sendMessage("Remove previous connections first.");
                return;
            }
            if (apprentice != null) {
                apprentice.setSponsor(currentMember.getObjectId());
            } else {
                targetMember.setApprenticeAndSponsor(0, currentMember.getObjectId());
            }

            if (sponsor != null) {
                sponsor.setApprentice(targetMember.getObjectId());
            } else {
                currentMember.setApprenticeAndSponsor(targetMember.getObjectId(), 0);
            }

            // saving to database even if online, since both must match
            targetMember.saveApprenticeAndSponsor(0, currentMember.getObjectId());
            currentMember.saveApprenticeAndSponsor(targetMember.getObjectId(), 0);

            sm = SystemMessage.getSystemMessage(SystemMessageId.S2_HAS_BEEN_DESIGNATED_AS_THE_APPRENTICE_OF_CLAN_MEMBER_S1);
        }
        sm.addString(currentMember.getName());
        sm.addString(targetMember.getName());
        if ((sponsor != activeChar) && (sponsor != apprentice)) {
            activeChar.sendPacket(sm);
        }
        if (sponsor != null) {
            sponsor.sendPacket(sm);
        }
        if (apprentice != null) {
            apprentice.sendPacket(sm);
        }
    }
}
