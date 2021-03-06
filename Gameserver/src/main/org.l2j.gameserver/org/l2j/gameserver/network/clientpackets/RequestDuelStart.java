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

import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExDuelAskStart;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.world.World;

import static org.l2j.gameserver.util.MathUtil.isInsideRadius2D;

/**
 * Format:(ch) Sd
 *
 * @author -Wooden-
 */
public final class RequestDuelStart extends ClientPacket {
    private String _player;
    private int _partyDuel;

    @Override
    public void readImpl() {
        _player = readString();
        _partyDuel = readInt();
    }

    private void scheduleDeny(Player player, String name) {
        if (player != null) {
            SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_DECLINED_YOUR_CHALLENGE_TO_A_DUEL);
            sm.addString(name);
            player.sendPacket(sm);
            player.onTransactionResponse();
        }
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        final Player targetChar = World.getInstance().findPlayer(_player);
        if (targetChar == null) {
            player.sendPacket(SystemMessageId.THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL);
            return;
        }
        if (player == targetChar) {
            player.sendPacket(SystemMessageId.THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL);
            return;
        }

        // Check if duel is possible
        if (!player.canDuel()) {
            player.sendPacket(SystemMessageId.YOU_ARE_UNABLE_TO_REQUEST_A_DUEL_AT_THIS_TIME);
            return;
        } else if (!targetChar.canDuel()) {
            player.sendPacket(targetChar.getNoDuelReason());
            return;
        }
        // Players may not be too far apart
        else if (!isInsideRadius2D(player, targetChar, 250)) {
            final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_TOO_FAR_AWAY_TO_RECEIVE_A_DUEL_CHALLENGE);
            msg.addString(targetChar.getName());
            player.sendPacket(msg);
            return;
        }

        // Duel is a party duel
        if (_partyDuel == 1) {
            // Player must be in a party & the party leader
            final Party party = player.getParty();
            if ((party == null) || !party.isLeader(player)) {
                player.sendMessage("You have to be the leader of a party in order to request a party duel.");
                return;
            }
            // Target must be in a party
            else if (!targetChar.isInParty()) {
                player.sendPacket(SystemMessageId.SINCE_THE_PERSON_YOU_CHALLENGED_IS_NOT_CURRENTLY_IN_A_PARTY_THEY_CANNOT_DUEL_AGAINST_YOUR_PARTY);
                return;
            }
            // Target may not be of the same party
            else if (player.getParty().isMember(targetChar)) {
                player.sendMessage("This player is a member of your own party.");
                return;
            }

            // Check if every player is ready for a duel
            for (Player temp : player.getParty().getMembers()) {
                if (!temp.canDuel()) {
                    player.sendMessage("Not all the members of your party are ready for a duel.");
                    return;
                }
            }
            Player partyLeader = null; // snatch party leader of targetChar's party
            for (Player temp : targetChar.getParty().getMembers()) {
                if (partyLeader == null) {
                    partyLeader = temp;
                }
                if (!temp.canDuel()) {
                    player.sendPacket(SystemMessageId.THE_OPPOSING_PARTY_IS_CURRENTLY_UNABLE_TO_ACCEPT_A_CHALLENGE_TO_A_DUEL);
                    return;
                }
            }

            // Send request to targetChar's party leader
            if (partyLeader != null) {
                if (!partyLeader.isProcessingRequest()) {
                    player.onTransactionRequest(partyLeader);
                    partyLeader.sendPacket(new ExDuelAskStart(player.getName(), _partyDuel));

                    SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.C1_S_PARTY_HAS_BEEN_CHALLENGED_TO_A_DUEL);
                    msg.addString(partyLeader.getName());
                    player.sendPacket(msg);

                    msg = SystemMessage.getSystemMessage(SystemMessageId.C1_S_PARTY_HAS_CHALLENGED_YOUR_PARTY_TO_A_DUEL);
                    msg.addString(player.getName());
                    targetChar.sendPacket(msg);
                } else {
                    final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ON_ANOTHER_TASK_PLEASE_TRY_AGAIN_LATER);
                    msg.addString(partyLeader.getName());
                    player.sendPacket(msg);
                }
            }
        } else
        // 1vs1 duel
        {
            if (!targetChar.isProcessingRequest()) {
                player.onTransactionRequest(targetChar);
                targetChar.sendPacket(new ExDuelAskStart(player.getName(), _partyDuel));

                SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_BEEN_CHALLENGED_TO_A_DUEL);
                msg.addString(targetChar.getName());
                player.sendPacket(msg);

                msg = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_CHALLENGED_YOU_TO_A_DUEL);
                msg.addString(player.getName());
                targetChar.sendPacket(msg);
            } else {
                final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ON_ANOTHER_TASK_PLEASE_TRY_AGAIN_LATER);
                msg.addString(targetChar.getName());
                player.sendPacket(msg);
            }
        }
    }
}
