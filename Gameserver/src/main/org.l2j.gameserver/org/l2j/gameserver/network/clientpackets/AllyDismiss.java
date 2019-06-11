/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.SystemMessageId;

import java.nio.ByteBuffer;

public final class AllyDismiss extends IClientIncomingPacket {
    private String _clanName;

    @Override
    public void readImpl() {
        _clanName = readString();
    }

    @Override
    public void runImpl() {
        if (_clanName == null) {
            return;
        }

        final L2PcInstance player = client.getActiveChar();
        if (player == null) {
            return;
        }
        if (player.getClan() == null) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_A_CLAN_MEMBER_AND_CANNOT_PERFORM_THIS_ACTION);
            return;
        }
        final L2Clan leaderClan = player.getClan();
        if (leaderClan.getAllyId() == 0) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_CURRENTLY_ALLIED_WITH_ANY_CLANS);
            return;
        }
        if (!player.isClanLeader() || (leaderClan.getId() != leaderClan.getAllyId())) {
            player.sendPacket(SystemMessageId.THIS_FEATURE_IS_ONLY_AVAILABLE_TO_ALLIANCE_LEADERS);
            return;
        }
        final L2Clan clan = ClanTable.getInstance().getClanByName(_clanName);
        if (clan == null) {
            player.sendPacket(SystemMessageId.THAT_CLAN_DOES_NOT_EXIST);
            return;
        }
        if (clan.getId() == leaderClan.getId()) {
            player.sendPacket(SystemMessageId.ALLIANCE_LEADERS_CANNOT_WITHDRAW);
            return;
        }
        if (clan.getAllyId() != leaderClan.getAllyId()) {
            player.sendPacket(SystemMessageId.DIFFERENT_ALLIANCE);
            return;
        }

        final long currentTime = System.currentTimeMillis();
        leaderClan.setAllyPenaltyExpiryTime(currentTime + (Config.ALT_ACCEPT_CLAN_DAYS_WHEN_DISMISSED * 86400000), L2Clan.PENALTY_TYPE_DISMISS_CLAN); // 24*60*60*1000 = 86400000
        leaderClan.updateClanInDB();

        clan.setAllyId(0);
        clan.setAllyName(null);
        clan.changeAllyCrest(0, true);
        clan.setAllyPenaltyExpiryTime(currentTime + (Config.ALT_ALLY_JOIN_DAYS_WHEN_DISMISSED * 86400000), L2Clan.PENALTY_TYPE_CLAN_DISMISSED); // 24*60*60*1000 = 86400000
        clan.updateClanInDB();

        player.sendPacket(SystemMessageId.YOU_HAVE_SUCCEEDED_IN_EXPELLING_THE_CLAN);
    }
}
