package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

public class ExUserViewInfo extends ServerPacket {
    private final Player _player;

    public ExUserViewInfo(Player player)
    {
        _player = player;
    }
    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_USER_VIEW_INFO_PARAMETER, buffer );
        buffer.writeInt(10);

    }
}
