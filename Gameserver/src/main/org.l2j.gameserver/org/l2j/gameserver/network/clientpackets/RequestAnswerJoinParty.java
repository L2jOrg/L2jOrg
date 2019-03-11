package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.L2Party;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.request.PartyRequest;
import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.JoinParty;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import java.nio.ByteBuffer;

public final class RequestAnswerJoinParty extends IClientIncomingPacket {
    private int _response;

    @Override
    public void readImpl(ByteBuffer packet) {
        _response = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player == null) {
            return;
        }

        final PartyRequest request = player.getRequest(PartyRequest.class);
        if ((request == null) || request.isProcessing() || !player.removeRequest(request.getClass())) {
            return;
        }
        request.setProcessing(true);

        final L2PcInstance requestor = request.getActiveChar();
        if (requestor == null) {
            return;
        }

        final L2Party party = request.getParty();
        final L2Party requestorParty = requestor.getParty();
        if ((requestorParty != null) && (requestorParty != party)) {
            return;
        }

        requestor.sendPacket(new JoinParty(_response));

        if (_response == 1) {
            if (party.getMemberCount() >= Config.ALT_PARTY_MAX_MEMBERS) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_PARTY_IS_FULL);
                player.sendPacket(sm);
                requestor.sendPacket(sm);
                return;
            }

            // Assign the party to the leader upon accept of his partner
            if (requestorParty == null) {
                requestor.setParty(party);
            }

            player.joinParty(party);

            final MatchingRoom requestorRoom = requestor.getMatchingRoom();

            if (requestorRoom != null) {
                requestorRoom.addMember(player);
            }
        } else if (_response == -1) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_SET_TO_REFUSE_PARTY_REQUESTS_AND_CANNOT_RECEIVE_A_PARTY_REQUEST);
            sm.addPcName(player);
            requestor.sendPacket(sm);

            if (party.getMemberCount() == 1) {
                party.removePartyMember(requestor, L2Party.MessageType.NONE);
            }
        } else if (party.getMemberCount() == 1) {
            party.removePartyMember(requestor, L2Party.MessageType.NONE);
        }

        party.setPendingInvitation(false);
        request.setProcessing(false);
    }
}
