package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.handler.BypassHandler;
import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

import java.nio.ByteBuffer;

/**
 * format ch c: (id) 0xD0 h: (subid) 0x13
 *
 * @author -Wooden-
 */
public final class RequestOlympiadMatchList extends IClientIncomingPacket {
    private static final String COMMAND = "arenalist";

    @Override
    public void readImpl(ByteBuffer packet) {

    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if ((activeChar == null) || !activeChar.inObserverMode()) {
            return;
        }

        final IBypassHandler handler = BypassHandler.getInstance().getHandler(COMMAND);
        if (handler != null) {
            handler.useBypass(COMMAND, activeChar, null);
        }
    }
}