package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;

/**
 * @author St3eT
 */
public final class ExRequestAutoFish extends ClientPacket {
    private boolean _start;

    @Override
    public void readImpl() {
        _start = readByte() != 0;
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (_start) {
            activeChar.getFishing().startFishing();
        } else {
            activeChar.getFishing().stopFishing();
        }
    }
}
