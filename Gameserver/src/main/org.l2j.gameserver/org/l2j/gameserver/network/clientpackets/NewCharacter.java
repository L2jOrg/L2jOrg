package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.NewCharacterSuccess;

import java.nio.ByteBuffer;

/**
 * @author Zoey76
 */
public final class NewCharacter extends IClientIncomingPacket {
    @Override
    public void readImpl(ByteBuffer packet) {

    }

    @Override
    public void runImpl() {
        client.sendPacket(NewCharacterSuccess.STATIC_PACKET);
    }
}
