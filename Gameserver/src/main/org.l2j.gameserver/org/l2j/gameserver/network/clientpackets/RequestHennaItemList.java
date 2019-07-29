package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.HennaEquipList;

/**
 * @author Tempy, Zoey76
 */
public final class RequestHennaItemList extends ClientPacket {
    @SuppressWarnings("unused")
    private int _unknown;

    @Override
    public void readImpl() {
        _unknown = readInt(); // TODO: Identify.
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar != null) {
            activeChar.sendPacket(new HennaEquipList(activeChar));
        }
    }
}
