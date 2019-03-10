package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExOlympiadMatchList;

import java.nio.ByteBuffer;

/**
 * Format: (ch)d d: unknown (always 0?)
 *
 * @author mrTJO
 */
public class RequestExOlympiadMatchListRefresh extends IClientIncomingPacket {
    @Override
    public void readImpl(ByteBuffer packet) {

    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        activeChar.sendPacket(new ExOlympiadMatchList());
    }
}