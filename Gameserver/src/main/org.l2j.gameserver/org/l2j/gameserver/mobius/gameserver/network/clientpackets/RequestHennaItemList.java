package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.HennaEquipList;

import java.nio.ByteBuffer;

/**
 * @author Tempy, Zoey76
 */
public final class RequestHennaItemList extends IClientIncomingPacket {
    @SuppressWarnings("unused")
    private int _unknown;

    @Override
    public void readImpl(ByteBuffer packet) {
        _unknown = packet.getInt(); // TODO: Identify.
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar != null) {
            activeChar.sendPacket(new HennaEquipList(activeChar));
        }
    }
}
