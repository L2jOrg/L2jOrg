package org.l2j.gameserver.network.clientpackets.crystalization;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

/**
 * @author UnAfraid
 */
public class RequestCrystallizeItemCancel extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        // if (!client.getFloodProtectors().getTransaction().tryPerformAction("crystallize"))
        // {
        // activeChar.sendMessage("You are crystallizing too fast.");
        // return;
        // }

        if (activeChar.isInCrystallize()) {
            activeChar.setInCrystallize(false);
        }
    }
}
