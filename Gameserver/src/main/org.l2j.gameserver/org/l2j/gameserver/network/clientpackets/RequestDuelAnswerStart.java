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

import org.l2j.gameserver.instancemanager.DuelManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * Format:(ch) ddd
 *
 * @author -Wooden-
 */
public final class RequestDuelAnswerStart extends ClientPacket {
    private int _partyDuel;
    @SuppressWarnings("unused")
    private int _unk1;
    private int _response;

    @Override
    public void readImpl() {
        _partyDuel = readInt();
        _unk1 = readInt();
        _response = readInt();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        final Player requestor = player.getActiveRequester();
        if (requestor == null) {
            return;
        }

        if (_response == 1) {
            SystemMessage msg1 = null;
            SystemMessage msg2 = null;
            if (requestor.isInDuel()) {
                msg1 = SystemMessage.getSystemMessage(SystemMessageId.C1_CANNOT_DUEL_BECAUSE_C1_IS_ALREADY_ENGAGED_IN_A_DUEL);
                msg1.addString(requestor.getName());
                player.sendPacket(msg1);
                return;
            } else if (player.isInDuel()) {
                player.sendPacket(SystemMessageId.YOU_ARE_UNABLE_TO_REQUEST_A_DUEL_AT_THIS_TIME);
                return;
            }

            if (_partyDuel == 1) {
                msg1 = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_ACCEPTED_C1_S_CHALLENGE_TO_A_PARTY_DUEL_THE_DUEL_WILL_BEGIN_IN_A_FEW_MOMENTS);
                msg1.addString(requestor.getName());

                msg2 = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_ACCEPTED_YOUR_CHALLENGE_TO_DUEL_AGAINST_THEIR_PARTY_THE_DUEL_WILL_BEGIN_IN_A_FEW_MOMENTS);
                msg2.addString(player.getName());
            } else {
                msg1 = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_ACCEPTED_C1_S_CHALLENGE_A_DUEL_THE_DUEL_WILL_BEGIN_IN_A_FEW_MOMENTS);
                msg1.addString(requestor.getName());

                msg2 = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_ACCEPTED_YOUR_CHALLENGE_TO_A_DUEL_THE_DUEL_WILL_BEGIN_IN_A_FEW_MOMENTS);
                msg2.addString(player.getName());
            }

            player.sendPacket(msg1);
            requestor.sendPacket(msg2);

            DuelManager.getInstance().addDuel(requestor, player, _partyDuel);
        } else if (_response == -1) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_SET_TO_REFUSE_DUEL_REQUESTS_AND_CANNOT_RECEIVE_A_DUEL_REQUEST);
            sm.addPcName(player);
            requestor.sendPacket(sm);
        } else {
            SystemMessage msg = null;
            if (_partyDuel == 1) {
                msg = SystemMessage.getSystemMessage(SystemMessageId.THE_OPPOSING_PARTY_HAS_DECLINED_YOUR_CHALLENGE_TO_A_DUEL);
            } else {
                msg = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_DECLINED_YOUR_CHALLENGE_TO_A_DUEL);
                msg.addPcName(player);
            }
            requestor.sendPacket(msg);
        }

        player.setActiveRequester(null);
        requestor.onTransactionResponse();
    }
}
