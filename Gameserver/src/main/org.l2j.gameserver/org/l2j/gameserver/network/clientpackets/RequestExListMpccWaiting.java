package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExListMpccWaiting;

/**
 * @author Sdw
 */
public class RequestExListMpccWaiting extends ClientPacket {
    private int _page;
    private int _location;
    private int _level;

    @Override
    public void readImpl() {
        _page = readInt();
        _location = readInt();
        _level = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        activeChar.sendPacket(new ExListMpccWaiting(_page, _location, _level));
    }
}
