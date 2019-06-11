package org.l2j.gameserver.network.clientpackets.ceremonyofchaos;

import org.l2j.gameserver.enums.CeremonyOfChaosState;
import org.l2j.gameserver.instancemanager.CeremonyOfChaosManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class RequestCuriousHouseHtml extends IClientIncomingPacket {
    @Override
    public void readImpl() {
        // Nothing to read
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player == null) {
            return;
        }

        if (CeremonyOfChaosManager.getInstance().getState() != CeremonyOfChaosState.REGISTRATION) {
            return;
        } else if (CeremonyOfChaosManager.getInstance().isRegistered(player)) {
            player.sendPacket(SystemMessageId.YOU_ARE_ON_THE_WAITING_LIST_FOR_THE_CEREMONY_OF_CHAOS);
            return;
        }

        if (CeremonyOfChaosManager.getInstance().canRegister(player, true)) {
            final NpcHtmlMessage message = new NpcHtmlMessage(0);
            message.setFile(player, "data/html/CeremonyOfChaos/invite.htm");
            player.sendPacket(message);
        }
    }
}
