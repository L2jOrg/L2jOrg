package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.instancemanager.PetitionManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * <p>
 * Format: (c) d
 * <ul>
 * <li>d: Unknown</li>
 * </ul>
 * </p>
 *
 * @author -Wooden-, TempyIncursion
 */
public final class RequestPetitionCancel extends ClientPacket {

    // private int _unknown;

    @Override
    public void readImpl() {
        // _unknown = readInt(); This is pretty much a trigger packet.
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (PetitionManager.getInstance().isPlayerInConsultation(activeChar)) {
            if (activeChar.isGM()) {
                PetitionManager.getInstance().endActivePetition(activeChar);
            } else {
                activeChar.sendPacket(SystemMessageId.YOUR_PETITION_IS_BEING_PROCESSED);
            }
        } else if (PetitionManager.getInstance().isPlayerPetitionPending(activeChar)) {
            if (PetitionManager.getInstance().cancelActivePetition(activeChar)) {
                final int numRemaining = Config.MAX_PETITIONS_PER_PLAYER - PetitionManager.getInstance().getPlayerTotalPetitionCount(activeChar);

                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_PETITION_WAS_CANCELED_YOU_MAY_SUBMIT_S1_MORE_PETITION_S_TODAY);
                sm.addString(String.valueOf(numRemaining));
                activeChar.sendPacket(sm);

                // Notify all GMs that the player's pending petition has been cancelled.
                final String msgContent = activeChar.getName() + " has canceled a pending petition.";
                AdminData.getInstance().broadcastToGMs(new CreatureSay(activeChar.getObjectId(), ChatType.HERO_VOICE, "Petition System", msgContent));
            } else {
                activeChar.sendPacket(SystemMessageId.FAILED_TO_CANCEL_PETITION_PLEASE_TRY_AGAIN_LATER);
            }
        } else {
            activeChar.sendPacket(SystemMessageId.YOU_HAVE_NOT_SUBMITTED_A_PETITION);
        }
    }
}
