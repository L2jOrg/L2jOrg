package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.NewCharacterSuccess;

/**
 * @author Zoey76
 */
public final class NewCharacter extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        client.sendPacket(NewCharacterSuccess.STATIC_PACKET);
    }
}
