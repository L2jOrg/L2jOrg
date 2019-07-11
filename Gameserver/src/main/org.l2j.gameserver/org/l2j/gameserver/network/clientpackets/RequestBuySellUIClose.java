package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;

public class RequestBuySellUIClose extends ClientPacket {
    @Override
    public void readImpl() {
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getActiveChar();
        if ((activeChar == null) || activeChar.isInventoryDisabled()) {
            return;
        }

        activeChar.sendItemList();
    }
}