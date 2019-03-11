package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.instancemanager.PetitionManager;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import java.nio.ByteBuffer;

/**
 * <p>
 * Format: (c) Sd
 * <ul>
 * <li>S: content</li>
 * <li>d: type</li>
 * </ul>
 * </p>
 *
 * @author -Wooden-, TempyIncursion
 */
public final class RequestPetition extends IClientIncomingPacket {
    private String _content;
    private int _type; // 1 = on : 0 = off;

    @Override
    public void readImpl(ByteBuffer packet) {
        _content = readString(packet);
        _type = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (!AdminData.getInstance().isGmOnline(false)) {
            client.sendPacket(SystemMessageId.THERE_ARE_NO_GMS_CURRENTLY_VISIBLE_IN_THE_PUBLIC_LIST_AS_THEY_MAY_BE_PERFORMING_OTHER_FUNCTIONS_AT_THE_MOMENT);
            return;
        }

        if (!PetitionManager.getInstance().isPetitioningAllowed()) {
            client.sendPacket(SystemMessageId.THE_GAME_CLIENT_ENCOUNTERED_AN_ERROR_AND_WAS_UNABLE_TO_CONNECT_TO_THE_PETITION_SERVER);
            return;
        }

        if (PetitionManager.getInstance().isPlayerPetitionPending(activeChar)) {
            client.sendPacket(SystemMessageId.YOU_MAY_ONLY_SUBMIT_ONE_PETITION_ACTIVE_AT_A_TIME);
            return;
        }

        if (PetitionManager.getInstance().getPendingPetitionCount() == Config.MAX_PETITIONS_PENDING) {
            client.sendPacket(SystemMessageId.THE_PETITION_SERVICE_IS_CURRENTLY_UNAVAILABLE_PLEASE_SEND_A_SUPPORT_TICKET_ON_HTTPS_SUPPORT_4GAME_COM);
            return;
        }

        final int totalPetitions = PetitionManager.getInstance().getPlayerTotalPetitionCount(activeChar) + 1;

        if (totalPetitions > Config.MAX_PETITIONS_PER_PLAYER) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.WE_HAVE_RECEIVED_S1_PETITIONS_FROM_YOU_TODAY_AND_THAT_IS_THE_MAXIMUM_THAT_YOU_CAN_SUBMIT_IN_ONE_DAY_YOU_CANNOT_SUBMIT_ANY_MORE_PETITIONS);
            sm.addInt(totalPetitions);
            client.sendPacket(sm);
            return;
        }

        if (_content.length() > 255) {
            client.sendPacket(SystemMessageId.THE_PETITION_CAN_CONTAIN_UP_TO_800_CHARACTERS);
            return;
        }

        final int petitionId = PetitionManager.getInstance().submitPetition(activeChar, _content, _type);

        SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOUR_PETITION_APPLICATION_HAS_BEEN_ACCEPTED_NRECEIPT_NO_IS_S1);
        sm.addInt(petitionId);
        client.sendPacket(sm);

        sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_SUBMITTED_S1_PETITION_S_NYOU_MAY_SUBMIT_S2_MORE_PETITION_S_TODAY);
        sm.addInt(totalPetitions);
        sm.addInt(Config.MAX_PETITIONS_PER_PLAYER - totalPetitions);
        client.sendPacket(sm);

        sm = SystemMessage.getSystemMessage(SystemMessageId.THERE_ARE_S1_PETITIONS_CURRENTLY_ON_THE_WAITING_LIST);
        sm.addInt(PetitionManager.getInstance().getPendingPetitionCount());
        client.sendPacket(sm);
    }
}
