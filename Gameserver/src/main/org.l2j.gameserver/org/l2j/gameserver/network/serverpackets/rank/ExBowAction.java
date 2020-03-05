package org.l2j.gameserver.network.serverpackets.rank;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExBowAction extends ServerPacket {

    private final int objectId;

    public ExBowAction(Player ranker) {
        objectId = ranker.getObjectId();
    }

    @Override
    protected void writeImpl(GameClient client)  {
        writeId(ServerPacketId.EX_BOW_ACTION_TO);
        writeInt(objectId);
    }
}
