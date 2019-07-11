package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author devScarlet
 */
public class NicknameChanged extends ServerPacket {
    private final String _title;
    private final int _objectId;

    public NicknameChanged(Creature cha) {
        _objectId = cha.getObjectId();
        _title = cha.getTitle();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.NICK_NAME_CHANGED);

        writeInt(_objectId);
        writeString(_title);
    }

}
