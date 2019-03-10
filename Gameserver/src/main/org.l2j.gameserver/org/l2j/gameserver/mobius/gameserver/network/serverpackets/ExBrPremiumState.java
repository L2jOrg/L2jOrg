package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author GodKratos
 */
public class ExBrPremiumState extends IClientOutgoingPacket {
    private final L2PcInstance _activeChar;

    public ExBrPremiumState(L2PcInstance activeChar) {
        _activeChar = activeChar;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_BR_PREMIUM_STATE.writeId(packet);

        packet.putInt(_activeChar.getObjectId());
        packet.put((byte)(_activeChar.hasPremiumStatus() ? 0x01 : 0x00));
    }
}
