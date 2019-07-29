package org.l2j.gameserver.network.clientpackets.mentoring;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.mentoring.ListMenteeWaiting;

/**
 * @author UnAfraid
 */
public class RequestMenteeWaitingList extends ClientPacket {
    private int _page;
    private int _minLevel;
    private int _maxLevel;

    @Override
    public void readImpl() {
        _page = readInt();
        _minLevel = readInt();
        _maxLevel = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }
        client.sendPacket(new ListMenteeWaiting(_page, _minLevel, _maxLevel));
    }
}
