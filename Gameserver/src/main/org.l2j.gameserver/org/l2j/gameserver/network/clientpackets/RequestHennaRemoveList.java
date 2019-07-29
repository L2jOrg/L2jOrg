package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.HennaRemoveList;

public final class RequestHennaRemoveList extends ClientPacket {
    @SuppressWarnings("unused")
    private int _unknown;

    @Override
    public void readImpl() {
        _unknown = readInt(); // TODO: Identify.
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }
        client.sendPacket(new HennaRemoveList(activeChar));
    }
}
