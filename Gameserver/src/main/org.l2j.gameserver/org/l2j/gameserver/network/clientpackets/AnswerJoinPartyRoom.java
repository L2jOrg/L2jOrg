package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * Format: (ch) d
 *
 * @author -Wooden-, Tryskell
 */
public final class AnswerJoinPartyRoom extends ClientPacket {
    private boolean _answer;

    @Override
    public void readImpl() {
        _answer = readInt() == 1;
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player == null) {
            return;
        }

        final L2PcInstance partner = player.getActiveRequester();
        if (partner == null) {
            player.sendPacket(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE);
            player.setActiveRequester(null);
            return;
        }

        if (_answer && !partner.isRequestExpired()) {
            final MatchingRoom room = partner.getMatchingRoom();
            if (room == null) {
                return;
            }

            room.addMember(player);
        } else {
            partner.sendPacket(SystemMessageId.THE_RECIPIENT_OF_YOUR_INVITATION_DID_NOT_ACCEPT_THE_PARTY_MATCHING_INVITATION);
        }

        // reset transaction timers
        player.setActiveRequester(null);
        partner.onTransactionResponse();
    }
}
