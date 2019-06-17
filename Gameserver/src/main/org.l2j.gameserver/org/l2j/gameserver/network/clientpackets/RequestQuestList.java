package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.QuestList;

/**
 * This class ...
 *
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestQuestList extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        client.sendPacket(new QuestList(client.getActiveChar()));
    }
}
