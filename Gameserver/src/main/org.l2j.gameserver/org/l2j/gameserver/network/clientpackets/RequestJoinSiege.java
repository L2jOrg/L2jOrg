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

import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * @author KenM
 */
public final class RequestJoinSiege extends ClientPacket {
    private int _castleId;
    private int _isAttacker;
    private int _isJoining;

    @Override
    public void readImpl() {
        _castleId = readInt();
        _isAttacker = readInt();
        _isJoining = readInt();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        if (!player.hasClanPrivilege(ClanPrivilege.CS_MANAGE_SIEGE)) {
            client.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }

        final Clan clan = player.getClan();
        if (clan == null) {
            return;
        }

        final Castle castle = CastleManager.getInstance().getCastleById(_castleId);
        if (castle != null) {
            if (_isJoining == 1) {
                if (System.currentTimeMillis() < clan.getDissolvingExpiryTime()) {
                    client.sendPacket(SystemMessageId.YOUR_CLAN_MAY_NOT_REGISTER_TO_PARTICIPATE_IN_A_SIEGE_WHILE_UNDER_A_GRACE_PERIOD_OF_THE_CLAN_S_DISSOLUTION);
                    return;
                }
                if (_isAttacker == 1) {
                    castle.getSiege().registerAttacker(player);
                } else {
                    castle.getSiege().registerDefender(player);
                }
            } else {
                castle.getSiege().removeSiegeClan(player);
            }
            castle.getSiege().listRegisterClan(player);
        }
    }
}
