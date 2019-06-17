package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.ExOlympiadMatchList;

/**
 * Format: (ch)d d: unknown (always 0?)
 *
 * @author mrTJO
 */
public class RequestExOlympiadMatchListRefresh extends ClientPacket {
    @Override
    public void readImpl() {

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