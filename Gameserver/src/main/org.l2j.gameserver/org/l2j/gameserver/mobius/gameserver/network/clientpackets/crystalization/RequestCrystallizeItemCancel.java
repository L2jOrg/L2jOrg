package org.l2j.gameserver.mobius.gameserver.network.clientpackets.crystalization;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.clientpackets.IClientIncomingPacket;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class RequestCrystallizeItemCancel extends IClientIncomingPacket {
    @Override
    public void readImpl(ByteBuffer packet) {

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
