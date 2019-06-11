package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author devScarlet
 */
public class NicknameChanged extends IClientOutgoingPacket {
    private final String _title;
    private final int _objectId;

    public NicknameChanged(L2Character cha) {
        _objectId = cha.getObjectId();
        _title = cha.getTitle();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.NICK_NAME_CHANGED);

        writeInt(_objectId);
        writeString(_title);
    }

}
