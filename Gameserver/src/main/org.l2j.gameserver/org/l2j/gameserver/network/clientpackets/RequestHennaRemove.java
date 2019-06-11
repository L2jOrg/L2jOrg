package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.items.L2Henna;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * @author Zoey76
 */
public final class RequestHennaRemove extends IClientIncomingPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHennaRemove.class);
    private int _symbolId;


    @Override
    public void readImpl() {
        _symbolId = readInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (!client.getFloodProtectors().getTransaction().tryPerformAction("HennaRemove")) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        L2Henna henna;
        boolean found = false;
        for (int i = 1; i <= 3; i++) {
            henna = activeChar.getHenna(i);
            if ((henna != null) && (henna.getDyeId() == _symbolId)) {
                if (activeChar.getAdena() >= henna.getCancelFee()) {
                    activeChar.removeHenna(i);
                } else {
                    activeChar.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                    client.sendPacket(ActionFailed.STATIC_PACKET);
                }
                found = true;
                break;
            }
        }
        // TODO: Test.
        if (!found) {
            LOGGER.warn(getClass().getSimpleName() + ": Player " + activeChar + " requested Henna Draw remove without any henna.");
            client.sendPacket(ActionFailed.STATIC_PACKET);
        }
    }
}
