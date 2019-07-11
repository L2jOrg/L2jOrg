package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;

/**
 * @author St3eT
 */
public final class ExSendSelectedQuestZoneID extends ClientPacket {
    private int _questZoneId;

    @Override
    public void readImpl() {
        _questZoneId = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        activeChar.setQuestZoneId(_questZoneId);
    }
}
