package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.SystemMessageId;

public final class RequestAnswerJoinAlly extends ClientPacket {
    private int _response;

    @Override
    public void readImpl() {
        _response = readInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final L2PcInstance requestor = activeChar.getRequest().getPartner();
        if (requestor == null) {
            return;
        }

        if (_response == 0) {
            activeChar.sendPacket(SystemMessageId.NO_RESPONSE_YOUR_ENTRANCE_TO_THE_ALLIANCE_HAS_BEEN_CANCELLED);
            requestor.sendPacket(SystemMessageId.NO_RESPONSE_INVITATION_TO_JOIN_AN_ALLIANCE_HAS_BEEN_CANCELLED);
        } else {
            if (!(requestor.getRequest().getRequestPacket() instanceof RequestJoinAlly)) {
                return; // hax
            }

            final L2Clan clan = requestor.getClan();
            // we must double check this cause of hack
            if (clan.checkAllyJoinCondition(requestor, activeChar)) {
                // TODO: Need correct message id
                requestor.sendPacket(SystemMessageId.THAT_PERSON_HAS_BEEN_SUCCESSFULLY_ADDED_TO_YOUR_FRIEND_LIST);
                activeChar.sendPacket(SystemMessageId.YOU_HAVE_ACCEPTED_THE_ALLIANCE);

                activeChar.getClan().setAllyId(clan.getAllyId());
                activeChar.getClan().setAllyName(clan.getAllyName());
                activeChar.getClan().setAllyPenaltyExpiryTime(0, 0);
                activeChar.getClan().changeAllyCrest(clan.getAllyCrestId(), true);
                activeChar.getClan().updateClanInDB();
            }
        }

        activeChar.getRequest().onRequestResponse();
    }
}
