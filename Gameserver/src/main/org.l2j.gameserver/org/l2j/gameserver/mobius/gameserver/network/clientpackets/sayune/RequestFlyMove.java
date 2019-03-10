package org.l2j.gameserver.mobius.gameserver.network.clientpackets.sayune;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.actor.request.SayuneRequest;
import org.l2j.gameserver.mobius.gameserver.network.clientpackets.IClientIncomingPacket;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class RequestFlyMove extends IClientIncomingPacket {
    private int _locationId;

    @Override
    public void readImpl(ByteBuffer packet) {
        _locationId = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final SayuneRequest request = activeChar.getRequest(SayuneRequest.class);
        if (request == null) {
            return;
        }

        request.move(activeChar, _locationId);
    }
}
